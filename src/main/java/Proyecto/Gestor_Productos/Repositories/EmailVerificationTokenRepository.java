package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * Acceso a datos para {EmailVerificationToken}. Se usa en el flujo de
 * verificación de email: cuando el usuario hace clic en el link recibido
 * por correo, este repositorio permite localizar el token correspondiente
 * a partir de su hash.
 */
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    /**
     * Busca un token de verificación por su hash (no por el valor original
     * enviado por correo, que nunca se guarda en la base de datos).
     */
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
}
