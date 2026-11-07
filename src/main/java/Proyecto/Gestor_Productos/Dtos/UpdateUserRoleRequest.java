package Proyecto.Gestor_Productos.Dtos;

import Proyecto.Gestor_Productos.Models.Role;
import jakarta.validation.constraints.NotNull;



public class UpdateUserRoleRequest {
    @NotNull(message = "Role is required")
    private Role role;

    public @NotNull(message = "Role is required") Role getRole() {
        return role;
    }

    public void setRole(@NotNull(message = "Role is required") Role role) {
        this.role = role;
    }
}
