package Proyecto.Gestor_Productos.Services;

import Proyecto.Gestor_Productos.Models.Brand;
import Proyecto.Gestor_Productos.Repositories.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    private  final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getBrand(){
        return  brandRepository.findAll();
    }

    public  Brand addBrand(Brand brand){
        return brandRepository.save(brand);
    }
}
