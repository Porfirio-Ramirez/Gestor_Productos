package Proyecto.Gestor_Productos.Exception;
/** Se lanza cuando un recurso buscado por id
 *  (producto, categoría, marca, usuario, token) no existe. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
