package Proyecto.Gestor_Productos.Dtos;

import jakarta.validation.constraints.NotBlank;

public class RegisterBrand {
    @NotBlank(message = "The brand name is required")
    private String name;

    public RegisterBrand() {
    }

    public RegisterBrand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
