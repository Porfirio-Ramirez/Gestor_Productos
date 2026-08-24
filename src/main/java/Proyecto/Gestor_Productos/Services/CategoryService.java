package Proyecto.Gestor_Productos.Services;

import Proyecto.Gestor_Productos.Models.Brand;
import Proyecto.Gestor_Productos.Models.Category;
import Proyecto.Gestor_Productos.Repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private  final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategory(){
        return categoryRepository.findAll();
    }

    public Category addCategory(Category category){
        return  categoryRepository.save(category);
    }
}
