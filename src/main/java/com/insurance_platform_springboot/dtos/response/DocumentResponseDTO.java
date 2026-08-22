package com.insurance_platform_springboot.dtos.response;

import java.time.LocalDateTime;

public record DocumentResponseDTO(
        Long id,
        String fileName,
        String contentType,
        Long fileSize,
        LocalDateTime uploadedAt
) {
}
