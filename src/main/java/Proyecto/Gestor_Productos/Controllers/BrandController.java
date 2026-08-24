package Proyecto.Gestor_Productos.Controllers;

import Proyecto.Gestor_Productos.Models.Brand;
import Proyecto.Gestor_Productos.Services.BrandService;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public List<Brand> getBrand(){
        return brandService.getBrand();
    }

    @PostMapping
    public Brand addNewBrand(@RequestBody  Brand brand){
        return  brandService.addBrand(brand);
    }
}
