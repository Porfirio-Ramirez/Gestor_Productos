package Proyecto.Gestor_Productos.Mapper;

import Proyecto.Gestor_Productos.Dtos.ProductRequest;
import Proyecto.Gestor_Productos.Dtos.ProductsDto;
import Proyecto.Gestor_Productos.Models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "brand", source = "brand.name")
    ProductsDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "brand.id", source = "brandId")
    Product toEntity(ProductRequest request);
}
