package Proyecto.Gestor_Productos.Controllers;

import Proyecto.Gestor_Productos.Dtos.BrandDto;
import Proyecto.Gestor_Productos.Dtos.RegisterBrand;
import Proyecto.Gestor_Productos.Mapper.BrandMapper;
import Proyecto.Gestor_Productos.Models.Brand;
import Proyecto.Gestor_Productos.Services.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestión de marcas. Lecturas (GET) son públicas; creación,
 * actualización y borrado requieren rol ADMIN (ver {@code SecurityConfig}).
 * Convierte entre {Brand} (entidad) y sus DTOs mediante {BrandMapper}.
 */
@Tag(name = "Brand", description = "Brand management endpoints")
@RestController
@RequestMapping("/api/brand")
public class BrandController {
    private final BrandService brandService;
    private final BrandMapper brandMapper;

    public BrandController(BrandService brandService, BrandMapper brandMapper) {
        this.brandService = brandService;
        this.brandMapper = brandMapper;
    }

    /** Lista todas las marcas. */
    @Operation(summary = "Get all brands")
    @GetMapping
    public List<BrandDto> getBrands() {
        return brandService.listBrand().stream()
                .map(brandMapper::toDto)
                .toList();
    }
    /** Busca una marca por id. */
    @Operation(summary = "Get a brand by id")
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable Long id) {
        Brand brand = brandService.listBrandById(id);
        return ResponseEntity.ok(brandMapper.toDto(brand));
    }
    /** Crea una marca nueva. Solo ADMIN. */
    @Operation(summary = "Create a new brand")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandDto> createBrand(@Valid @RequestBody RegisterBrand brand) {
        Brand entity = brandMapper.toEntity(brand);
        Brand saved = brandService.addBrand(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(brandMapper.toDto(saved));
    }
    /** Actualiza el nombre de una marca existente. Solo ADMIN. */
    @Operation(summary = "Update an existing brand")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable Long id,
                                               @Valid @RequestBody RegisterBrand brand) {
        Brand entity = brandMapper.toEntity(brand);
        Brand updated = brandService.updateBrand(id, entity);
        return ResponseEntity.ok(brandMapper.toDto(updated));
    }
    /** Elimina una marca. Solo ADMIN; falla con 409 si tiene productos asociados. */
    @Operation(summary = "Delete a brand")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
