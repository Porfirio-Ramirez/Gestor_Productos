package Proyecto.Gestor_Productos.Exception;
/** Se lanza cuando la contraseña y su confirmación no coinciden,
 *  en registro o reseteo de contraseña. */
public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
