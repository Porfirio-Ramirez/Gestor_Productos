package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
