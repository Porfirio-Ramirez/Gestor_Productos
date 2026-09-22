package Proyecto.Gestor_Productos.Mapper;

import Proyecto.Gestor_Productos.Dtos.BrandDto;
import Proyecto.Gestor_Productos.Dtos.RegisterBrand;
import Proyecto.Gestor_Productos.Models.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BrandMapper {
    @Mapping(target = "id", ignore = true)
    Brand toEntity(RegisterBrand register);

    BrandDto toDto(Brand brand);
}
