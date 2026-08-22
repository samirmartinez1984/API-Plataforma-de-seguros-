package com.insurance_platform_springboot.mapper;

import com.insurance_platform_springboot.dtos.response.DocumentResponseDTO;
import com.insurance_platform_springboot.model.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentResponseDTO toResponse(Document document);
}
