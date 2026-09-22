package Proyecto.Gestor_Productos.Models;

import jakarta.persistence.*;

/**
 * Entidad que representa una marca de producto (tabla {@code brand}).
 * El nombre es único a nivel de base de datos (constraint {@code uk_brand_name}).
 */

@Entity
@Table(
        name = "brand",
        uniqueConstraints = @UniqueConstraint(name = "uk_brand_name", columnNames = "name")
)
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    public Brand() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return id != null && id.equals(brand.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
