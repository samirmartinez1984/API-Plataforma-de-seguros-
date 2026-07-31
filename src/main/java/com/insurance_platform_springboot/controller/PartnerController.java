package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.PartnerRequestDTO;
import com.insurance_platform_springboot.dtos.response.PartnerResponseDTO;
import com.insurance_platform_springboot.dtos.update.PartnerUpdateDTO;
import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de aliados estratégicos (Partners).
 * Permite registrar y administrar proveedores de servicios como talleres y clínicas.
 */
@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
@Tag(name = "Aliados (Partners)", description = "Endpoints para la gestión de aliados y proveedores de servicios")
public class PartnerController {

    private final PartnerService partnerService;

    /**
     * Registra un nuevo aliado en el sistema.
     * @param request Datos del aliado a crear.
     * @return PartnerResponseDTO con los detalles del aliado creado.
     */
    @PostMapping
    @Operation(summary = "Registrar un nuevo aliado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartnerResponseDTO> createPartner(@Valid @RequestBody PartnerRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.create(request));
    }

    /**
     * Obtiene la lista completa de aliados registrados.
     * @return Lista de PartnerResponseDTO.
     */
    @GetMapping
    @Operation(summary = "Listar todos los aliados")
    public ResponseEntity<List<PartnerResponseDTO>> findAllPartners() {
        return ResponseEntity.ok(partnerService.findAll());
    }

    /**
     * Busca un aliado por su identificador único.
     * @param id ID del aliado.
     * @return PartnerResponseDTO con los datos encontrados.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar aliado por ID")
    public ResponseEntity<PartnerResponseDTO> findPartnerById(@PathVariable Long id) {
        return ResponseEntity.ok(partnerService.findById(id));
    }

    /**
     * Actualiza la información de un aliado existente.
     * @param id ID del aliado a actualizar.
     * @param request Datos actualizados.
     * @return PartnerResponseDTO con los cambios aplicados.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de un aliado")
    public ResponseEntity<PartnerResponseDTO> updatePartner(@PathVariable Long id, @Valid @RequestBody PartnerUpdateDTO request) {
        return ResponseEntity.ok(partnerService.update(id, request));
    }

    /**
     * Actualiza exclusivamente el estado de un aliado.
     * @param id ID del aliado.
     * @param status Nuevo estado (ACTIVE/INACTIVE).
     * @return PartnerResponseDTO con el estado actualizado.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de un aliado")
    public ResponseEntity<PartnerResponseDTO> updatePartnerStatus(
            @PathVariable Long id, 
            @RequestParam PartnerStatus status) {
        return ResponseEntity.ok(partnerService.updateStatus(id, status));
    }

    /**
     * Elimina a un aliado del sistema.
     * @param id ID del aliado a eliminar.
     * @return 204 No Content si la operación fue exitosa.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un aliado")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        partnerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
