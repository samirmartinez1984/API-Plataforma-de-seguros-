package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.response.DocumentResponseDTO;
import com.insurance_platform_springboot.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/claims/{claimId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> uploadDocument(@PathVariable Long claimId,
                                                              @RequestPart("file") MultipartFile file){
        return ResponseEntity.ok(documentService.uploadDocument(claimId, file));
    }
    @GetMapping
    public ResponseEntity<List<DocumentResponseDTO>> listDocuments(@PathVariable Long claimId) {
        return ResponseEntity.ok(documentService.listDocumentsByClaim(claimId));
    }
}
