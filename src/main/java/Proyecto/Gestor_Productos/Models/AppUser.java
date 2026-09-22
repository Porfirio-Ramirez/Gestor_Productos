package Proyecto.Gestor_Productos.Models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario del sistema (tabla {@code app_users}).
 * <p>
 * Se usa tanto para autenticación (email + password + rol) como para el
 * control de seguridad de la cuenta: bloqueo temporal por intentos fallidos
 * de login y verificación de email obligatoria antes de poder autenticarse.
 * <p>
 * Mantiene relaciones {@code @OneToMany} hacia sus tokens de recuperación
 * de contraseña y de verificación de email, con borrado en cascada: si este
 * usuario se elimina, sus tokens asociados se eliminan automáticamente para
 * no dejar registros huérfanos ni violar las foreign keys.
 */

@Entity
@Table(name = "app_users")
public class AppUser  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /** Contraseña ya encriptada con BCrypt;
     * nunca se guarda en texto plano. */
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // cascade = REMOVE + orphanRemoval: un token de reseteo no tiene sentido
    // sin su usuario, así que al borrar el AppUser se borran automáticamente
    // todos sus tokens asociados en esta tabla.
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    // Mismo criterio que passwordResetTokens: borrado en cascada.
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<EmailVerificationToken> emailVerificationTokens = new ArrayList<>();

    /** Contador de intentos de login fallidos consecutivos; se resetea a
     * 0 en login exitoso o al restablecer la contraseña. */
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    /** Fecha/hora hasta la cual la cuenta está bloqueada por
     * exceso de intentos fallidos. Null si no está bloqueada. */
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /** Indica si el usuario ya verificó su email. Mientras sea false,
     * el login se rechaza aunque las credenciales sean correctas. */
    @Column(nullable = false)
    private boolean enabled = false;


    public AppUser() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Dos usuarios se consideran iguales si tienen el mismo id persistido.
     * Usuarios sin id (aún no guardados) nunca se consideran iguales entre sí.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return id != null && id.equals(appUser.id);
    }

    // hashCode constante por clase: recomendado para entidades JPA, ya que el
    // id cambia de null a un valor real al persistir, y hashCode no debe variar
    // durante el ciclo de vida del objeto (rompería estructuras como HashSet).

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
