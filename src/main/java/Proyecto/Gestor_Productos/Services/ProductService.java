package Proyecto.Gestor_Productos.Services;

import Proyecto.Gestor_Productos.Exception.ResourceNotFoundException;
import Proyecto.Gestor_Productos.Models.Brand;
import Proyecto.Gestor_Productos.Models.Category;
import Proyecto.Gestor_Productos.Models.Product;
import Proyecto.Gestor_Productos.Repositories.BrandRepository;
import Proyecto.Gestor_Productos.Repositories.CategoryRepository;
import Proyecto.Gestor_Productos.Repositories.ProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Lógica de negocio para la gestión de productos ({Product}).
 * Resuelve las relaciones con {Category} y {Brand} a partir
 * de los ids planos que llegan en el DTO de entrada (resolveCategory}
 * y {resolveBrand}), convirtiéndolos en las entidades reales antes
 * de persistir.
 */

@Service
public class ProductService {
    private final ProductsRepository productsRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public ProductService(BrandRepository brandRepository, ProductsRepository productsRepository, CategoryRepository categoryRepository) {
        this.brandRepository = brandRepository;
        this.productsRepository = productsRepository;
        this.categoryRepository = categoryRepository;
    }
    /** Lista todos los productos, con su categoría y marca ya cargadas (ver @EntityGraph en el repositorio). */
    @Transactional(readOnly = true)
    public List<Product> listProduct() {
        return productsRepository.findAll();
    }
    /** Lista los productos que pertenecen a una categoría específica. */
    @Transactional(readOnly = true)
    public List<Product> listByCategory(Long categoryId) {
        return productsRepository.findByCategoryId(categoryId);
    }

    /**
     * Crea un producto nuevo, resolviendo su categoría y marca a partir de
     * los ids que trae el objeto recibido (mapeado desde el DTO de entrada).
     * @throws ResourceNotFoundException si falta la categoría/marca o no existen.
     */
    @Transactional
    public Product addProduct(Product product) {
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        Long brandId = product.getBrand() != null ? product.getBrand().getId() : null;

        product.setCategory(resolveCategory(categoryId));
        product.setBrand(resolveBrand(brandId));
        return productsRepository.save(product);
    }

    /**
     * Busca la categoría real en base de datos a partir de su id.
     * @throws ResourceNotFoundException si el id es nulo, o si no existe esa categoría.
     */
    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new ResourceNotFoundException("Category is required");
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    }

    /**
     * Busca la marca real en base de datos a partir de su id.
     * @throws ResourceNotFoundException si el id es nulo, o si no existe esa marca.
     */
    private Brand resolveBrand(Long brandId) {
        if (brandId == null) {
            throw new ResourceNotFoundException("Brand is required");
        }
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

    }

    /**
     * Actualiza los datos de un producto existente, incluyendo su categoría y marca.
     * @throws ResourceNotFoundException si el producto, la categoría o la marca no existen.
     */
    @Transactional
    public Product updateProduct(Long id, Product newProduct) {
        Product searchproduct = productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        searchproduct.setName(newProduct.getName());
        searchproduct.setDescription(newProduct.getDescription());
        searchproduct.setPrice(newProduct.getPrice());
        searchproduct.setStock(newProduct.getStock());

        Long categoryId = newProduct.getCategory() != null ? newProduct.getCategory().getId() : null;
        Long brandId = newProduct.getBrand() != null ? newProduct.getBrand().getId() : null;

        searchproduct.setCategory(resolveCategory(categoryId));
        searchproduct.setBrand(resolveBrand(brandId));

        return productsRepository.save(searchproduct);
    }

    /**
     * Elimina un producto por id.
     * @throws ResourceNotFoundException si no existe el producto.
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productsRepository.deleteById(id);
    }
}
