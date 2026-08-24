package Proyecto.Gestor_Productos.Controllers;

import Proyecto.Gestor_Productos.Models.Category;
import Proyecto.Gestor_Productos.Models.Product;
import Proyecto.Gestor_Productos.Services.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getCategory(){
        return  categoryService.getCategory();
    }

    @PostMapping
    public Category addNewCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }
}
