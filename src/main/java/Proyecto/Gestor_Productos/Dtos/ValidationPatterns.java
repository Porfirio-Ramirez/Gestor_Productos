package Proyecto.Gestor_Productos.Dtos;
/**
 * Patrones de validación reutilizables entre DTOs, para no duplicar la
 * misma expresión regular y mensaje en {@link RegisterRequest} y
 * {@link ResetPasswordRequest}.
 * <p>
 * {@code PASSWORD_PATTERN} exige, en cualquier orden, al menos una letra,
 * un dígito y uno de los caracteres especiales permitidos, con un mínimo
 * de 6 caracteres en total.
 */
public class ValidationPatterns {
    public static final String PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!*_-]).{6,}$";
    public static final String PASSWORD_MESSAGE =
            "The password must contain letters, numbers, and at least one special character.";
}
