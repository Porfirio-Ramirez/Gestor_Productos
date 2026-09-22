package Proyecto.Gestor_Productos.Security;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
/**
 * Utilidad para hashear tokens (recuperación de contraseña, verificación
 * de email) antes de guardarlos en base de datos, usando SHA-256.
 * <p>
 * El valor original del token (el enviado por correo) nunca se persiste;
 * solo su hash. Así, si la base de datos se ve comprometida, los tokens
 * guardados no pueden reconstruirse ni reutilizarse directamente.
 */
@Component
public class TokenHasher {

    /** Calcula el hash SHA-256 del token, codificado en Base64 para poder guardarse como texto. */
    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
