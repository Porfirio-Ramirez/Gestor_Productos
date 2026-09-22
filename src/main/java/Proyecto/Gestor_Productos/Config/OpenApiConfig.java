package Proyecto.Gestor_Productos.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
/**
 * Configuración de Swagger/OpenAPI: metadatos generales de la API y el
 * esquema de seguridad "bearerAuth", que hace aparecer el botón
 * "Authorize" en Swagger UI para probar endpoints protegidos con un JWT.
 */

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product management API",
                version = "1.0",
                description = "Store catalog REST API with JWT security."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"

)
public class OpenApiConfig {}
