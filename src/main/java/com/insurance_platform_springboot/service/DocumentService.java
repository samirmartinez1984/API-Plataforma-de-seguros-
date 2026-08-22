package com.insurance_platform_springboot.service;

import com.insurance_platform_springboot.dtos.response.DocumentResponseDTO;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.DocumentMapper;
import com.insurance_platform_springboot.model.Claim;
import com.insurance_platform_springboot.model.Document;
import com.insurance_platform_springboot.repository.ClaimRepository;
import com.insurance_platform_springboot.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ClaimRepository claimRepository;
    private final DocumentMapper documentMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public DocumentResponseDTO uploadDocument(Long claimId, MultipartFile file) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El reclamo no existe con el ID " + claimId));


        try {
            Path claimFolder = Paths.get(uploadDir, claimId.toString());
            Files.createDirectories(claimFolder);

            String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path destinationPath = claimFolder.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), destinationPath);

            Document document = new Document();
            document.setClaim(claim);
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(destinationPath.toString());
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setUploadedAt(LocalDateTime.now());
            Document saved = documentRepository.save(document);
            return documentMapper.toResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    public List<DocumentResponseDTO> listDocumentsByClaim(Long claimId) {
        return documentRepository.findByClaimId(claimId)
                .stream()
                .map(documentMapper::toResponse)
                .toList();
    }
}


