package Proyecto.Gestor_Productos.Exception;
/** Se lanza al intentar crear/actualizar un recurso
 * (categoría, marca, usuario) con un nombre/email que ya existe. */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
