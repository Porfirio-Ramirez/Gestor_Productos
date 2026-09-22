package Proyecto.Gestor_Productos.Services;

import Proyecto.Gestor_Productos.Exception.DuplicateResourceException;
import Proyecto.Gestor_Productos.Exception.ResourceNotFoundException;
import Proyecto.Gestor_Productos.Models.Brand;
import Proyecto.Gestor_Productos.Repositories.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Lógica de negocio para la gestión de marcas ({Brand}).
 * Aplica las validaciones de duplicado que no puede resolver el DTO
 * (requieren consultar la base de datos), delegando la persistencia
 * en {BrandRepository}.
 */
@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    /** Lista todas las marcas existentes. */
    @Transactional(readOnly = true)
    public List<Brand>  listBrand() {
        return brandRepository.findAll();
    }

    /**
     * Busca una marca por id.
     * @throws ResourceNotFoundException si no existe ninguna marca con ese id.
     */
    @Transactional(readOnly = true)
    public Brand listBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
    }

    /**
     * Crea una marca nueva.
     * @throws DuplicateResourceException si ya existe una marca con el mismo nombre.
     */
    @Transactional
    public Brand addBrand(Brand brand) {
        if (brandRepository.existsByNameIgnoreCase(brand.getName())) {
            throw new DuplicateResourceException("Brand name already exists");
        }
        return brandRepository.save(brand);
    }

    /**
     * Actualiza el nombre de una marca existente.
     * @throws ResourceNotFoundException si no existe la marca a actualizar.
     * @throws DuplicateResourceException si el nuevo nombre ya lo usa otra marca distinta.
     */
    @Transactional
    public Brand updateBrand(Long id, Brand newBrand) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        // AndIdNot excluye la propia marca de la validación: si no cambia el
        // nombre, no debe chocar consigo misma como si fuera un duplicado.
        if (brandRepository.existsByNameIgnoreCaseAndIdNot(newBrand.getName(), id)) {
            throw new DuplicateResourceException("Brand name already exists");
        }

        brand.setName(newBrand.getName());
        return brandRepository.save(brand);
    }

    /**
     * Elimina una marca por id.
     * @throws ResourceNotFoundException si no existe la marca.
     * Si la marca tiene productos asociados, la base de datos rechaza el borrado
     * por la foreign key; ese caso lo captura el GlobalExceptionHandler con 409 Conflict.
     */
    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found");
        }

        brandRepository.deleteById(id);
    }
}
