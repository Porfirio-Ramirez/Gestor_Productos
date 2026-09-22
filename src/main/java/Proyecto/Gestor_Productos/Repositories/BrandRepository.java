package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos para {Brand}. Además del CRUD estándar heredado de
 * {JpaRepository}, expone consultas para validar unicidad del nombre
 * al crear y actualizar marcas.
 */

public interface BrandRepository extends JpaRepository<Brand, Long> {
    /**
     * Verifica si ya existe una marca con ese nombre (sin distinguir mayúsculas/minúsculas).
     * Se usa al crear una marca nueva, para evitar duplicados.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Igual que {existsByNameIgnoreCase}, pero excluyendo un id específico.
     * Se usa al actualizar una marca: permite que conserve su propio nombre sin
     * que choque consigo misma en la validación de duplicados.
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
