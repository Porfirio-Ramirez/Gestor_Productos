package Proyecto.Gestor_Productos.Services;


import Proyecto.Gestor_Productos.Models.Brand;
import Proyecto.Gestor_Productos.Models.Category;
import Proyecto.Gestor_Productos.Models.Product;
import Proyecto.Gestor_Productos.Repositories.BrandRepository;
import Proyecto.Gestor_Productos.Repositories.CategoryRepository;
import Proyecto.Gestor_Productos.Repositories.ProductsRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductService {
   private  final ProductsRepository productsRepository;
   private final CategoryRepository categoryRepository;
   private final BrandRepository brandRepository;

    public ProductService(BrandRepository brandRepository, ProductsRepository productsRepository, CategoryRepository categoryRepository) {
        this.brandRepository = brandRepository;
        this.productsRepository = productsRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> listProduct(){
        return productsRepository.findAll();
    }

    public List<Product> listByCategory(Long categoryid){
        return productsRepository.findByCategoryId(categoryid);
    }

    public Product addProduct(Product product){
        resolveCategory(product);
        resolveBrand(product);
        return productsRepository.save(product);
    }

    private  void resolveCategory(Product product){
        if (product.getCategory() != null && product.getCategory().getId() != null){
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElse(null);
            product.setCategory(category);
        }
    }

    private void resolveBrand(Product product){
        if (product.getBrand() != null && product.getBrand().getId() != null){
            Brand brand = brandRepository.findById(product.getBrand().getId())
                    .orElse(null);
            product.setBrand(brand);
        }
    }

    public Product updateProduct(Long id, Product newProduct){
        Product searchproduct = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not exits"));
        searchproduct.setName(newProduct.getName());
        searchproduct.setDescription(newProduct.getDescription());
        searchproduct.setPrice(newProduct.getPrice());
        searchproduct.setStock(newProduct.getStock());

        resolveCategory(newProduct);
        resolveBrand(newProduct);
        searchproduct.setCategory(newProduct.getCategory());
        searchproduct.setBrand(newProduct.getBrand());

        return productsRepository.save(searchproduct);
    }

    public void deleteProduct(Long id){
        boolean exits = productsRepository.existsById(id);

        if(!exits){
            throw new RuntimeException("Not exits a product to delete");
        }
     productsRepository.deleteById(id);

    }
}
