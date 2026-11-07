package Proyecto.Gestor_Productos.Mapper;

import Proyecto.Gestor_Productos.Dtos.UserDto;
import Proyecto.Gestor_Productos.Models.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {

    UserDto toDto(AppUser appUser);
}