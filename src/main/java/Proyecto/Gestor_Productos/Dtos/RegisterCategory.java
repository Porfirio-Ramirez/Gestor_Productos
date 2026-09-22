package Proyecto.Gestor_Productos.Dtos;

import jakarta.validation.constraints.NotBlank;

public class RegisterCategory {
    @NotBlank(message = "The category name is required")
    private String name;

    public RegisterCategory() {
    }

    public RegisterCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
