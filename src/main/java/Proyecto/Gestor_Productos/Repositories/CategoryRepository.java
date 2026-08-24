package Proyecto.Gestor_Productos.Repositories;

import Proyecto.Gestor_Productos.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
