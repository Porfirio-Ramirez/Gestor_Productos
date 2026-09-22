package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * Acceso a datos para {AppUser}. Además del CRUD estándar heredado de
 * {JpaRepository}, expone las consultas necesarias para autenticación
 * (buscar por email) y registro (validar que el email no esté ya en uso).
 */

public interface UserRepository extends JpaRepository<AppUser, Long> {
    /** Busca un usuario por su email; se usa en login
     * y en la carga de detalles de usuario para Spring Security. */
    Optional<AppUser> findByEmail(String email);
    /** Verifica si ya existe un usuario con ese email;
     *  se usa al registrar, para evitar duplicados. */
    boolean existsByEmail(String email);
}
