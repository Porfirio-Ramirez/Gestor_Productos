package Proyecto.Gestor_Productos.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;
/**
 * Manejador global de excepciones: centraliza la traducción de cada tipo
 * de excepción de negocio/framework a una respuesta HTTP consistente
 * ({ApiError}), evitando repetir try/catch en cada controller.
 * Spring evalúa el handler más específico que coincida con la excepción
 * lanzada, sin importar el orden en que estén declarados los métodos aquí.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /** Errores de validación de Bean Validation
     * (@Valid) en el body del request; agrupa un mensaje por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manageValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(),
                "The submitted data is invalid.", errors);
        return ResponseEntity.badRequest().body(body);
    }
    /** Recurso no encontrado (producto, categoría, marca, usuario, token, etc.). */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> manageNotFound(ResourceNotFoundException ex) {
        ApiError body = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    /** Nombre duplicado (categoría, marca) o email ya registrado. */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> manageDuplicate(DuplicateResourceException ex) {
        ApiError body = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    /** Conflicto de concurrencia: el recurso fue modificado por otra
     * petición entre lectura y guardado. */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> manageOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        ApiError body = new ApiError(HttpStatus.CONFLICT.value(),
                "The resource was modified by another request. Retry the operation.", null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    /**
     * Violación de restricción de la base de datos, típicamente una foreign
     * key: por ejemplo, intentar borrar una categoría o marca que todavía
     * tiene productos asociados.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> manageIntegrity(DataIntegrityViolationException ex) {
        ApiError body = new ApiError(HttpStatus.CONFLICT.value(),
                "The operation violates a data constraint.", null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    /** Red de seguridad final: cualquier excepción no capturada
     * por un handler más específico. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manageGeneric(Exception ex) {
        ApiError body = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error ocurred.", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    /** Credenciales incorrectas en login
     * (lanzada por Spring Security, ej. BadCredentialsException). */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> manageAuthentication(AuthenticationException ex) {
        ApiError body = new ApiError(HttpStatus.UNAUTHORIZED.value(),
                "Invalid credentials.", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
    /** Cuenta bloqueada temporalmente por exceso de intentos fallidos de login. */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiError> manageAccountLocked(AccountLockedException ex) {
        ApiError body = new ApiError(HttpStatus.LOCKED.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.LOCKED).body(body);
    }
    /** Contraseña y confirmación no coinciden, en registro o reseteo de contraseña. */
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiError> managePasswordMismatch(PasswordMismatchException ex) {
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    /** Credenciales correctas, pero el usuario todavía no verificó su email. */
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiError> manageEmailNotVerified(EmailNotVerifiedException ex) {
        ApiError body = new ApiError(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}
