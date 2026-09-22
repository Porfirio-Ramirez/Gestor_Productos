package Proyecto.Gestor_Productos.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
/**
 * Generación y validación de JWT (algoritmo HS256). La clave secreta se
 * toma de configuración externa ({@code jwt.secret}, vía variable de
 * entorno) y nunca queda hardcodeada en el código.
 */
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String keyText;

    @Value("${jwt.expiration-ms}")
    private Long expirationMS;

    private SecretKey key;

    /** Construye la SecretKey a partir del texto plano de jwt.secret
     * al arrancar la aplicación. */
    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(keyText.getBytes());
    }

    /**
     * Genera un JWT firmado para el usuario dado, con el rol como claim
     * personalizado y expiración según {@code jwt.expiration-ms}.
     */
    public String generateToken(String email, String role){
        Date now = new Date();
        Date expire = new Date(now.getTime() + expirationMS);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expire)
                .signWith(key)
                .compact();
    }

    /** Extrae el email (subject) de un token ya validado. */
    public String getEmail(String token){
        return parseClaims(token).getSubject();
    }

    /** Verifica firma y expiración del token; false si es
     * inválido o expiró, sin propagar la excepción. */
    public boolean isvalid(String token){
        try{
            parseClaims(token);
            return  true;
        }catch (Exception e){
            return false;
        }
    }

    /** Parsea y valida el token, devolviendo sus claims (payload) si la
     * firma y el formato son correctos. */
    private Claims parseClaims(String token){
        return  Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
