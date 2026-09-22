package Proyecto.Gestor_Productos.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "This email isn't valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password must be at least 6 characters long.")
    @Pattern(regexp = ValidationPatterns.PASSWORD_PATTERN, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public  String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
