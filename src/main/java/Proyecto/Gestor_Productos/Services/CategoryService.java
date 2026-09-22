package Proyecto.Gestor_Productos.Services;

import Proyecto.Gestor_Productos.Exception.DuplicateResourceException;
import Proyecto.Gestor_Productos.Exception.ResourceNotFoundException;
import Proyecto.Gestor_Productos.Models.Category;
import Proyecto.Gestor_Productos.Repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Lógica de negocio para la gestión de categorías ({Category}).
 * Mismo patrón que {BrandService}: valida duplicados de nombre
 * antes de delegar en {CategoryRepository}.
 */
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /** Lista todas las categorías existentes. */
    @Transactional(readOnly = true)
    public List<Category> listCategory() {
        return categoryRepository.findAll();
    }

    /**
     * Busca una categoría por id.
     * @throws ResourceNotFoundException si no existe ninguna categoría con ese id.
     */
    @Transactional(readOnly = true)
    public Category listCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    /**
     * Crea una categoría nueva.
     * @throws DuplicateResourceException si ya existe una categoría con el mismo nombre.
     */
    @Transactional
    public Category addCategory(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new DuplicateResourceException("Category name already exists");
        }
        return categoryRepository.save(category);
    }

    /**
     * Actualiza el nombre de una categoría existente.
     * @throws ResourceNotFoundException si no existe la categoría a actualizar.
     * @throws DuplicateResourceException si el nuevo nombre ya lo usa otra categoría distinta.
     */
    @Transactional
    public Category updateCategory(Long id, Category newCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(newCategory.getName(), id)) {
            throw new DuplicateResourceException("Category name already exists");
        }

        category.setName(newCategory.getName());
        return categoryRepository.save(category);
    }

    /**
     * Elimina una categoría por id.
     * @throws ResourceNotFoundException si no existe la categoría.
     * Si la categoría tiene productos asociados, la base de datos rechaza el borrado
     * por la foreign key; ese caso lo captura el GlobalExceptionHandler con 409 Conflict.
     */
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }

        categoryRepository.deleteById(id);
    }
}
