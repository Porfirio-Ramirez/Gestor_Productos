package Proyecto.Gestor_Productos.Services;

import Proyecto.Gestor_Productos.Dtos.*;
import Proyecto.Gestor_Productos.Exception.*;
import Proyecto.Gestor_Productos.Models.AppUser;
import Proyecto.Gestor_Productos.Models.EmailVerificationToken;
import Proyecto.Gestor_Productos.Models.PasswordResetToken;
import Proyecto.Gestor_Productos.Models.Role;
import Proyecto.Gestor_Productos.Repositories.EmailVerificationTokenRepository;
import Proyecto.Gestor_Productos.Repositories.PasswordResetTokenRepository;
import Proyecto.Gestor_Productos.Repositories.UserRepository;
import Proyecto.Gestor_Productos.Security.JwtService;
import Proyecto.Gestor_Productos.Security.TokenHasher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Lógica de negocio de autenticación y gestión de cuenta: registro, login,
 * verificación de email, recuperación de contraseña, bloqueo por intentos
 * fallidos y eliminación de cuenta.
 * <p>
 * Los flujos de verificación de email y recuperación de contraseña siguen
 * el mismo patrón: se genera un token aleatorio (UUID), se guarda solo su
 * hash (nunca el valor original) en base de datos, y el valor original se
 * envía por correo dentro de un link. Así, si la base de datos se ve
 * comprometida, los tokens guardados no pueden usarse directamente.
 */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final TokenHasher tokenHasher;
    private final EmailService emailService;

    private static final int RESET_TOKEN_EXPIRATION_MINUTES = 30;
    private static final int VERIFICATION_TOKEN_EXPIRATION_MINUTES = 60 * 24; // 24 horas
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_DURATION_MINUTES = 15;


    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       EmailVerificationTokenRepository emailVerificationTokenRepository,
                       TokenHasher tokenHasher,
                       EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.tokenHasher = tokenHasher;
        this.emailService = emailService;
    }

    /**
     * Registra un usuario nuevo con rol USER fijo (nunca se toma el rol del
     * request, para evitar que alguien se autoasigne privilegios de ADMIN)
     * y cuenta deshabilitada hasta que verifique su email. Envía el correo
     * de verificación al finalizar.
     * @throws PasswordMismatchException si password y confirmPassword no coinciden.
     * @throws DuplicateResourceException si el email ya está registrado.
     */
    @Transactional
    public void register(RegisterRequest registerRequest){
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        AppUser appUser = new AppUser();

        appUser.setEmail(registerRequest.getEmail());
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setRole(Role.USER);
        appUser.setEnabled(false);
        userRepository.save(appUser);

        sendVerificationEmail(appUser);
    }

    /**
     * Autentica al usuario y devuelve un JWT si las credenciales son correctas.
     * <p>
     * Lleva control de intentos fallidos: cada contraseña incorrecta incrementa
     * un contador, y al llegar a {@value #MAX_FAILED_ATTEMPTS} la cuenta se
     * bloquea temporalmente y se notifica por correo. El reseteo del contador
     * ocurre tanto en login exitoso como al restablecer la contraseña.
     * <p>
     * {@code noRollbackFor = BadCredentialsException.class}: sin esto, al
     * lanzar esa excepción Spring revertiría también el incremento del
     * contador de intentos fallidos que se guardó justo antes, porque por
     * defecto una RuntimeException hace rollback de toda la transacción.
     *
     * @throws ResourceNotFoundException si el email no existe.
     * @throws AccountLockedException si la cuenta está bloqueada.
     * @throws EmailNotVerifiedException si las credenciales son correctas pero el email no está verificado.
     */
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public AuthResponse login(LoginRequest request){
        AppUser appUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (appUser.getLockedUntil() != null && appUser.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException("Account is locked. Try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            appUser.setFailedAttempts(appUser.getFailedAttempts() + 1);
            if (appUser.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                appUser.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));

                emailService.sendSimpleEmail(
                        appUser.getEmail(),
                        "Security alert: account locked",
                        "We detected " + MAX_FAILED_ATTEMPTS + " failed login attempts on your account. "
                                + "For your security, your account has been temporarily locked for "
                                + LOCK_DURATION_MINUTES + " minutes.\n\n"
                                + "If this wasn't you, we recommend changing your password immediately "
                                + "using the \"Reset password\" option on the login screen.\n\n"
                                + "If this was you, please wait a few minutes and try logging in again."
                );
            }
            userRepository.save(appUser);
            throw e;
        }

        if (!appUser.isEnabled()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in");
        }

        appUser.setFailedAttempts(0);
        appUser.setLockedUntil(null);
        userRepository.save(appUser);

        String token = jwtService.generateToken(appUser.getEmail(), appUser.getRole().name());
        return new AuthResponse(token);
    }

    /**
     * Elimina una cuenta de usuario. Gracias a la cascada configurada en
     * {AppUser} sobre sus relaciones con los tokens, sus tokens de
     * verificación y de reseteo de contraseña se eliminan automáticamente
     * junto con el usuario.
     * @throws ResourceNotFoundException si no existe el usuario.
     */
    @Transactional
    public void deleteAccount(Long id){
        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Inicia el flujo de recuperación de contraseña: si el email existe,
     * genera un token, lo guarda hasheado y envía el link por correo.
     * <p>
     * Si el email no existe, el método simplemente no hace nada (no lanza
     * excepción), para no revelar qué correos están registrados en el
     * sistema (protección contra "user enumeration").
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(appUser -> {
            String rawToken = UUID.randomUUID().toString();
            String hashedToken = tokenHasher.hash(rawToken);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(appUser);
            resetToken.setTokenHash(hashedToken);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRATION_MINUTES));
            passwordResetTokenRepository.save(resetToken);

            String resetLink = "http://localhost:5173/reset-password?token=" + rawToken;
            emailService.sendSimpleEmail(
                    appUser.getEmail(),
                    "Password Reset Request",
                    "Use this link to reset your password (valid for "
                            + RESET_TOKEN_EXPIRATION_MINUTES + " minutes):\n\n" + resetLink
            );
        });

    }

    /**
     * Completa el flujo de recuperación de contraseña: valida el token
     * (existe, no usado, no expirado), actualiza la contraseña, y limpia
     * el bloqueo de la cuenta si lo tuviera (completar este flujo ya es
     * prueba suficiente de que quien lo hace es el dueño legítimo de la cuenta).
     * <p>
     * Se usa el mismo mensaje de error genérico ("Invalid or expired token")
     * para los tres casos posibles (no existe, ya usado, expirado), para no
     * darle pistas a un atacante sobre cuál de las tres razones aplica.
     *
     * @throws PasswordMismatchException si newPassword y confirmNewPassword no coinciden.
     * @throws ResourceNotFoundException si el token no existe, ya fue usado, o expiró.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        String hashedToken = tokenHasher.hash(request.getToken());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token"));

        if (resetToken.isUsed()) {
            throw new ResourceNotFoundException("Invalid or expired token");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Invalid or expired token");
        }

        AppUser appUser = resetToken.getUser();
        appUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        appUser.setFailedAttempts(0);
        appUser.setLockedUntil(null);
        userRepository.save(appUser);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    /**
     * Confirma el email de un usuario a partir del token recibido por correo
     * al registrarse. Habilita la cuenta ({@code enabled = true}) para que
     * pueda hacer login.
     * @throws ResourceNotFoundException si el token no existe, ya fue usado, o expiró.
     */
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        String hashedToken = tokenHasher.hash(request.getToken());

        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token"));

        if (verificationToken.isUsed()) {
            throw new ResourceNotFoundException("Invalid or expired token");
        }

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Invalid or expired token");
        }

        AppUser appUser = verificationToken.getUser();
        appUser.setEnabled(true);
        userRepository.save(appUser);

        verificationToken.setUsed(true);
        emailVerificationTokenRepository.save(verificationToken);
    }

    /** Genera y envía el token de verificación de email a un usuario recién registrado. */
    private void sendVerificationEmail(AppUser appUser) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = tokenHasher.hash(rawToken);

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUser(appUser);
        verificationToken.setTokenHash(hashedToken);
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_TOKEN_EXPIRATION_MINUTES));
        emailVerificationTokenRepository.save(verificationToken);

        String verifyLink = "http://localhost:5173/verify-email?token=" + rawToken;
        emailService.sendSimpleEmail(
                appUser.getEmail(),
                "Confirm your email address",
                "Thank you for registering. Use this link to verify your account:\n\n" + verifyLink
        );
    }

}
