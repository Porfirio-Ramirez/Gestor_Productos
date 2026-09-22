package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Acceso a datos para {Category}. Además del CRUD estándar heredado de
 * {JpaRepository}, expone consultas para validar unicidad del nombre
 * al crear y actualizar categorías.
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Verifica si ya existe una categoría con ese nombre (sin distinguir mayúsculas/minúsculas).
     * Se usa al crear una categoría nueva, para evitar duplicados.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Igual que {existsByNameIgnoreCase}, pero excluyendo un id específico.
     * Se usa al actualizar una categoría: permite que conserve su propio nombre sin
     * que choque consigo misma en la validación de duplicados.
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
