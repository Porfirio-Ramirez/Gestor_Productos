package Proyecto.Gestor_Productos.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una categoría de producto (tabla {@code category}).
 * El nombre es único a nivel de base de datos (constraint {@code uk_category_name}).
 * <p>
 * Mantiene el lado inverso de la relación con {Product}: no tiene
 * cascada de borrado a propósito, para evitar eliminar productos reales
 * por accidente al borrar una categoría (si tiene productos asociados,
 * la base de datos rechaza el borrado por la foreign key).
 */

@Entity
@Table(
        name = "category",
        uniqueConstraints = @UniqueConstraint(name = "uk_category_name", columnNames = "name")
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // @JsonIgnore evita el bucle infinito de serialización: Category -> products
    // -> cada Product.category -> de vuelta a esta misma Category, y así indefinidamente.
    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Category() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
