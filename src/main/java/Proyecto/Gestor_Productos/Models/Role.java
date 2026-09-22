package Proyecto.Gestor_Productos.Models;

/**
 * Roles de autorización disponibles para un {AppUser}.
 * Se persiste como texto (EnumType.STRING) en la columna {@code role}
 * de la tabla {@code app_users}, y se usa en las reglas de
 * {@code @PreAuthorize("hasRole(...)")} de los controllers.
 */

public enum Role {
    USER,
    ADMIN
}
