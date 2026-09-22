package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * Acceso a datos para {PasswordResetToken}. Se usa en el flujo de
 * "olvidé mi contraseña": permite localizar el token correspondiente a
 * partir de su hash cuando el usuario intenta restablecer su contraseña.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    /**
     * Busca un token de reseteo por su hash (no por el valor original
     * enviado por correo, que nunca se guarda en la base de datos).
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
