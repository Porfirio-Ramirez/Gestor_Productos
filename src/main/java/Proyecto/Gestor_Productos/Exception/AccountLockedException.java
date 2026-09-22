package Proyecto.Gestor_Productos.Exception;
/** Se lanza al intentar hacer login mientras la
 * cuenta está bloqueada por intentos fallidos. */
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}
