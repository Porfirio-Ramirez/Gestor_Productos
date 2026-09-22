package Proyecto.Gestor_Productos.Controllers;

import Proyecto.Gestor_Productos.Dtos.CategoryDto;
import Proyecto.Gestor_Productos.Dtos.RegisterCategory;
import Proyecto.Gestor_Productos.Mapper.CategoryMapper;
import Proyecto.Gestor_Productos.Models.Category;
import Proyecto.Gestor_Productos.Services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Endpoints de gestión de categorías. Lecturas (GET) son públicas; creación,
 * actualización y borrado requieren rol ADMIN (ver {@code SecurityConfig}).
 * Convierte entre {Category} (entidad) y sus DTOs mediante {CategoryMapper}.
 */

@Tag(name = "Category", description = "Category management endpoints")
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }
    /** Lista todas las categorías. */
    @Operation(summary = "Get all categories")
    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryService.listCategory()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }
    /** Busca una categoría por id. */
    @Operation(summary = "Get a category by id")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.listCategoryById(id);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }
    /** Crea una categoría nueva. Solo ADMIN. */
    @Operation(summary = "Create a new category")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody RegisterCategory category) {
        Category create = categoryMapper.toEntity(category);
        Category save = categoryService.addCategory(create);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDto(save));
    }
    /** Actualiza el nombre de una categoría existente. Solo ADMIN. */
    @Operation(summary = "Update an existing category")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,
                                                     @Valid @RequestBody RegisterCategory category) {
        Category updated = categoryService.updateCategory(id, categoryMapper.toEntity(category));
        return ResponseEntity.ok(categoryMapper.toDto(updated));
    }
    /** Elimina una categoría. Solo ADMIN; falla con 409 si tiene productos asociados. */
    @Operation(summary = "Delete a category")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
