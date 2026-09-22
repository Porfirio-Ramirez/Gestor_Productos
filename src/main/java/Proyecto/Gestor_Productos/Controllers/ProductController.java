package Proyecto.Gestor_Productos.Controllers;

import Proyecto.Gestor_Productos.Dtos.ProductRequest;
import Proyecto.Gestor_Productos.Dtos.ProductsDto;
import Proyecto.Gestor_Productos.Mapper.ProductMapper;
import Proyecto.Gestor_Productos.Models.Product;
import Proyecto.Gestor_Productos.Services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestión de productos. Lecturas (GET) son públicas; creación,
 * actualización y borrado requieren rol ADMIN (ver {@code SecurityConfig}).
 * Convierte entre {Product} (entidad) y sus DTOs mediante {ProductMapper};
 * {@code ProductRequest} recibe categoryId/brandId como ids planos, resueltos
 * a las entidades reales en {@code ProductService}.
 */

@Tag(name = "Product", description = "Product management endpoints")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    /** Lista todos los productos. */
    @Operation(summary = "Get all products")
    @GetMapping
    public List<ProductsDto> getProducts() {
        return productService.listProduct()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /** Lista los productos de una categoría específica. */
    @Operation(summary = "Get products by category")
    @GetMapping("/category/{categoryId}")
    public List<ProductsDto> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.listByCategory(categoryId)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }
    /** Crea un producto nuevo. Solo ADMIN. */
    @Operation(summary = "Create a new product")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductsDto> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product save = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toDto(save));
    }
    /** Actualiza un producto existente, incluyendo su categoría y marca. Solo ADMIN. */
    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductsDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product update = productService.updateProduct(id, product);
        return ResponseEntity.ok(productMapper.toDto(update));
    }
    /** Elimina un producto. Solo ADMIN. */
    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
