package Proyecto.Gestor_Productos.Controllers;

import Proyecto.Gestor_Productos.Dtos.ProductsDto;
import Proyecto.Gestor_Productos.Models.Product;
import Proyecto.Gestor_Productos.Services.ProductService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private  final ProductService productService;

    public ProductController(ProductService productService) {

        this.productService = productService;
    }

    @GetMapping
    public List<ProductsDto> getProduct(){

        return productService.listProduct()
                .stream().map(ProductsDto::new).toList();
    }

    @GetMapping("/category/{categoryid}")
    public List<ProductsDto> getByCategory(@PathVariable Long categoryid){
        return productService.listByCategory(categoryid)
                .stream().map(ProductsDto::new).toList();
    }

    @PostMapping
    public ProductsDto createProduct(@RequestBody Product product){
        Product guardado =  productService.addProduct(product);
        return new ProductsDto(guardado);
    }

    @PutMapping("/{id}")
    public ProductsDto updateProduct(@PathVariable Long id, @RequestBody Product product){
       Product update = productService.updateProduct(id, product);
       return new ProductsDto(update);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return "Product deleted";
    }


}
