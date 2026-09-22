package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/**
 * Acceso a datos para {Product}. Sobrescribe los métodos estándar de
 * {JpaRepository} para cargar {@code category} y {@code brand} con
 * {@code @EntityGraph}, evitando el problema N+1: como ambas relaciones son
 * LAZY en la entidad, sin esto Hibernate ejecutaría una consulta adicional
 * por cada producto al mapearlo a DTO (donde sí se leen esos nombres).
 */
public interface ProductsRepository extends JpaRepository<Product, Long> {

    /** Trae todos los productos junto con su categoría y
     * marca en una sola consulta (JOIN). */
    @EntityGraph(attributePaths = {"category", "brand"})
    @Override
    List<Product> findAll();

    /** Trae un producto por id junto con su categoría y
     * marca en una sola consulta (JOIN). */
    @EntityGraph(attributePaths = {"category", "brand"})
    @Override
    Optional<Product> findById(Long id);

    /** Filtra productos por el id de su categoría,
     * trayendo también categoría y marca en una sola consulta. */
    @EntityGraph(attributePaths = {"category", "brand"})
    List<Product> findByCategoryId(Long categoryId);


}
