package Proyecto.Gestor_Productos.Mapper;

import Proyecto.Gestor_Productos.Dtos.CategoryDto;
import Proyecto.Gestor_Productos.Dtos.RegisterCategory;
import Proyecto.Gestor_Productos.Models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(RegisterCategory register);

    CategoryDto toDto(Category category);
}
