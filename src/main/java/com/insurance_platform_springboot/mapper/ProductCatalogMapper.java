package com.insurance_platform_springboot.mapper;

import com.insurance_platform_springboot.dtos.request.ProductCatalogRequestDTO;
import com.insurance_platform_springboot.dtos.response.ProductCatalogResponseDTO;
import com.insurance_platform_springboot.dtos.update.ProductCatalogUpdateDTO;
import com.insurance_platform_springboot.model.ProductCatalog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para convertir entre la entidad {@link ProductCatalog} y sus DTOs.
 * Implementación generada por MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ProductCatalogMapper {

    /**
     * Convierte entidad CatalogProduct a ProductCatalogResponseDTO.
     */
    ProductCatalogResponseDTO toResponse(ProductCatalog product);

    /**
     * Convierte ProductCatalogRequestDTO a entidad CatalogProduct.
     * Ignora campos gestionados por el sistema (id, createdAt, relations).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "policies", ignore = true)
    ProductCatalog toEntity(ProductCatalogRequestDTO dto);

    /**
     * Aplica actualizaciones parciales desde ProductCatalogUpdateDTO a la entidad existente.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policies", ignore = true)
    void updateEntity(ProductCatalogUpdateDTO dto, @MappingTarget ProductCatalog product);

}

