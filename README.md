# Gestor de Productos

API REST desarrollada con **Spring Boot** para la gestión de productos de una tienda, con autenticación JWT completa, control de acceso por roles, recuperación de contraseña, verificación de email y protección contra fuerza bruta.

## Tabla de contenidos

- [Stack tecnológico](#stack-tecnológico)
- [Características principales](#características-principales)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
- [Documentación de la API](#documentación-de-la-api)
- [Endpoints principales](#endpoints-principales)
- [Seguridad](#seguridad)
- [Estructura del proyecto](#estructura-del-proyecto)

## Stack tecnológico

- **Java 25**
- **Spring Boot 4.1.0** (Web, Data JPA, Security, Validation, Mail)
- **MySQL 8** como base de datos
- **Hibernate** para el mapeo objeto-relacional (esquema gestionado automáticamente con `ddl-auto=update`)
- **JWT** (jjwt) para autenticación stateless
- **MapStruct** para el mapeo entre entidades y DTOs
- **Springdoc OpenAPI / Swagger UI** para documentación interactiva de la API
- **Spring Mail** para el envío de correos (verificación de cuenta, recuperación de contraseña, alertas de seguridad)

## Características principales

- CRUD completo de **Productos**, **Categorías** y **Marcas**
- Registro y login de usuarios con **JWT**
- Roles de usuario (`USER` / `ADMIN`) con autorización a nivel de endpoint
- **Verificación de email obligatoria**: la cuenta queda deshabilitada hasta confirmar el correo
- **Recuperación de contraseña** vía token de un solo uso enviado por correo
- **Bloqueo de cuenta** tras 3 intentos fallidos de login, con notificación automática por correo
- Confirmación de contraseña en registro y en reseteo, con política de complejidad (letras, dígitos y carácter especial)
- Manejo centralizado de errores con respuestas HTTP consistentes
- Documentación interactiva vía Swagger UI

## Requisitos previos

- JDK 25
- MySQL 8 corriendo localmente (o accesible por red)
- Una cuenta de correo (Gmail u otro proveedor SMTP) para el envío de notificaciones

## Configuración

El proyecto **no lleva ninguna credencial en el código**. Toda la configuración sensible se toma de variables de entorno.

1. Copia el archivo de ejemplo y complétalo con tus propios valores:

   ```bash
   cp .secrets.properties.example .secrets.properties
   ```

2. Variables de entorno / propiedades necesarias:

   | Variable              | Descripción                                              | Obligatoria |
   |------------------------|-----------------------------------------------------------|:-----------:|
   | `DB_URL`              | URL de conexión a MySQL                                   | No (tiene valor por defecto) |
   | `DB_USERNAME`         | Usuario de la base de datos                                | No (tiene valor por defecto) |
   | `DB_PASSWORD`         | Contraseña de la base de datos                             | **Sí** |
   | `JWT_SECRET`          | Clave secreta para firmar los JWT (mínimo 32 caracteres)   | **Sí** |
   | `JWT_EXPIRATION_MS`   | Duración del token en milisegundos                         | No (por defecto 1 hora) |
   | `MAIL_USERNAME`       | Cuenta de correo remitente                                  | **Sí** |
   | `MAIL_PASSWORD`       | Contraseña de aplicación del correo remitente               | **Sí** |
   | `SPRING_PROFILES_ACTIVE` | Perfil activo (`dev` o `prod`)                          | No (por defecto `dev`) |

   > `.secrets.properties` está incluido en `.gitignore` y nunca debe subirse al repositorio.

3. Crea la base de datos en MySQL:

   ```sql
   CREATE DATABASE tiendas;
   ```

   Hibernate se encarga de crear y mantener las tablas automáticamente al arrancar la aplicación.

## Cómo levantar el proyecto

Con Maven Wrapper (no requiere tener Maven instalado):

```bash
./mvnw spring-boot:run       # Linux/Mac
mvnw.cmd spring-boot:run     # Windows
```

La aplicación arranca por defecto en `http://localhost:8080`.

## Documentación de la API

Con la aplicación corriendo, la documentación interactiva está disponible en:

```
http://localhost:8080/swagger-ui.html
```

Desde ahí se pueden probar todos los endpoints, incluyendo los protegidos: tras hacer login, usa el botón **Authorize** y pega el JWT recibido.

## Endpoints principales

### Autenticación (`/api/auth`)

| Método | Endpoint                  | Descripción                                  | Acceso |
|--------|----------------------------|-----------------------------------------------|--------|
| POST   | `/register`                | Registra un usuario nuevo                     | Público |
| POST   | `/login`                   | Autentica y devuelve un JWT                   | Público |
| POST   | `/verify-email`            | Confirma el email con el token recibido       | Público |
| POST   | `/forgot-password`         | Inicia el flujo de recuperación de contraseña | Público |
| POST   | `/reset-password`          | Completa el reseteo de contraseña             | Público |
| DELETE | `/users/{id}`              | Elimina una cuenta de usuario                 | ADMIN |

### Productos (`/api/products`)

| Método | Endpoint                        | Descripción                      | Acceso |
|--------|----------------------------------|-----------------------------------|--------|
| GET    | `/`                              | Lista todos los productos         | Público |
| GET    | `/category/{categoryId}`         | Lista productos por categoría     | Público |
| POST   | `/`                              | Crea un producto                  | ADMIN |
| PUT    | `/{id}`                          | Actualiza un producto             | ADMIN |
| DELETE | `/{id}`                          | Elimina un producto               | ADMIN |

### Categorías (`/api/category`) y Marcas (`/api/brand`)

Ambos siguen el mismo patrón: lecturas (`GET`) públicas, y creación/actualización/borrado restringidos a `ADMIN`.

## Seguridad

- **Autenticación stateless** con JWT (HS256), sin sesiones en el servidor.
- **Contraseñas** cifradas con BCrypt.
- **Autorización por rol** (`@PreAuthorize`) en operaciones de escritura.
- **Bloqueo de cuenta**: 3 intentos fallidos de login bloquean la cuenta por 15 minutos, con correo de alerta automático.
- **Tokens de un solo uso** (recuperación de contraseña y verificación de email), guardados **hasheados** en base de datos — el valor real solo viaja por correo y nunca se persiste en texto plano.
- **Protección contra enumeración de usuarios**: `forgot-password` responde igual exista o no el email.
- **CORS** configurado para permitir el consumo desde un frontend en desarrollo local.
- Manejo de errores consistente vía `GlobalExceptionHandler`, con códigos HTTP apropiados (400, 401, 403, 404, 409, 423, 500).

## Estructura del proyecto

```
src/main/java/Proyecto/Gestor_Productos/
├── Config/          # Configuración de JPA y OpenAPI/Swagger
├── Controllers/      # Endpoints REST
├── Dtos/             # Objetos de entrada/salida de la API
├── Exception/         # Excepciones de negocio y manejador global
├── Mapper/            # Conversión entre entidades y DTOs (MapStruct)
├── Models/            # Entidades JPA
├── Repositories/      # Acceso a datos (Spring Data JPA)
├── Security/          # JWT, filtros, hasheo de tokens, UserDetailsService
└── Services/          # Lógica de negocio
```
