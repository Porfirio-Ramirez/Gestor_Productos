package Proyecto.Gestor_Productos.Controllers;

import Proyecto.Gestor_Productos.Dtos.*;
import Proyecto.Gestor_Productos.Services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.PathItem;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticación y gestión de cuenta: registro, login,
 * verificación de email, recuperación de contraseña y eliminación de cuenta.
 * Las rutas GET/POST de este controller son públicas salvo donde se indique
 * lo contrario (ver reglas de {@code SecurityConfig}); toda la lógica de
 * negocio vive en {uthService}, este controller solo orquesta la
 * petición HTTP.
 */

@Tag(name = "Auth", description = "Authentication and authorization endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Registra un usuario nuevo. No devuelve token: la cuenta queda deshabilitada hasta que se verifique el email. */
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest register) {
        authService.register(register);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful. Please check your email to verify your account.");
    }

    /** Autentica al usuario y devuelve el JWT si las credenciales son correctas y el email está verificado. */
    @Operation(summary = "Login and obtain JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest login) {
        return ResponseEntity.ok(authService.login(login));
    }

    /** Elimina la cuenta de un usuario. Solo ADMIN; sus tokens asociados se eliminan en cascada. */
    @Operation(summary = "Delete user account")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        authService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
    /** Inicia el flujo de recuperación de contraseña, enviando un link por correo si el email existe. */
    @Operation(summary = "Request a password reset link")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    /** Completa el flujo de recuperación de contraseña usando el token recibido por correo. */
    @Operation(summary = "Reset password using a valid token")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }

    /** Confirma el email de un usuario recién registrado usando el token recibido por correo. */
    @Operation(summary = "Verify email with token")
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok("Email verified successfully");
    }


}
