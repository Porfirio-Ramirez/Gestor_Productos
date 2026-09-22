package Proyecto.Gestor_Productos.Exception;

import java.time.LocalDateTime;
import java.util.Map;
/**
 * Cuerpo de respuesta estándar para cualquier error de la API, usado por
 * {GlobalExceptionHandler}: código de estado HTTP, mensaje legible,
 * errores por campo (cuando aplica, ej. validación) y la marca de tiempo
 * en que ocurrió.
 */
public class ApiError {
    private  int status;
    private  String message;
    private  Map<String, String> errors;
    private  String timestamp;
    /** El timestamp se asigna automáticamente al momento de crear el error,
     * no hace falta pasarlo. */
    public ApiError(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now().toString();
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
