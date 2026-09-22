package Proyecto.Gestor_Productos.Exception;
/** Se lanza en login cuando las credenciales son correctas pero
 * el usuario aún no verificó su email. */
public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
