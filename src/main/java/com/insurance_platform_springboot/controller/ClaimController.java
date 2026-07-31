package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.ClaimRequestDTO;
import com.insurance_platform_springboot.dtos.response.ClaimResponseDTO;
import com.insurance_platform_springboot.dtos.update.ClaimUpdateDTO;
import com.insurance_platform_springboot.model.enums.ClaimStatus;
import com.insurance_platform_springboot.security.CustomUserDetails;
import com.insurance_platform_springboot.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de reclamaciones y siniestros de seguros.
 * Permite el reporte de incidentes, seguimiento de estados y administración de casos por supervisores.
 */
@RestController
@RequestMapping("/claims")
@RequiredArgsConstructor
@Tag(name = "Reclamaciones", description = "Endpoints para la gestión de siniestros y reclamos de seguros")
public class ClaimController {
    
    private final ClaimService claimService;

    /**
     * Registra un nuevo siniestro en la plataforma.
     * Permitido para Clientes (USER) y Administradores.
     *
     * @param requestDTO Datos detallados del incidente reportado.
     * @return ResponseEntity con el ClaimResponseDTO creado y estado 201.
     */
    @PostMapping
    @Operation(summary = "Reportar un nuevo siniestro (Admin/User)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ClaimResponseDTO> createClaim(@Valid @RequestBody ClaimRequestDTO requestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(claimService.create(requestDTO));
    }

    /**
     * Obtiene el listado de todos los siniestros registrados en el sistema.
     * Solo accesible por personal administrativo o supervisores.
     *
     * @return ResponseEntity con la lista de ClaimResponseDTO.
     */
    @GetMapping
    @Operation(summary = "Listar todas las reclamaciones (Admin/Supervisor)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<ClaimResponseDTO>> findAllClaims(){
        return ResponseEntity.ok(claimService.findAll());
    }

    /**
     * Busca los detalles técnicos de una reclamación específica por su ID.
     * Accesible por personal autorizado y por el propio cliente titular.
     *
     * @param id Identificador numérico del siniestro.
     * @return ResponseEntity con los datos del ClaimResponseDTO encontrado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar reclamación por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'USER')")
    public ResponseEntity<ClaimResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(claimService.findById(id));
    }

    /**
     * Recupera todas las reclamaciones reportadas exclusivamente por el cliente autenticado.
     *
     * @param authentication Objeto que contiene la identidad del usuario logueado.
     * @return ResponseEntity con la lista de siniestros del cliente.
     */
    @GetMapping("/my-claims")
    @Operation(summary = "Obtener mis reclamaciones personales (Cliente)")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClaimResponseDTO>> getMyClaims(Authentication authentication) {
        Long customerId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(claimService.findByCustomerId(customerId));
    }

    /**
     * Filtra los siniestros según su estado actual de gestión.
     * Solo accesible por personal administrativo o supervisores.
     *
     * @param status Estado por el cual filtrar (ej: REGISTERED, IN_REVIEW).
     * @return ResponseEntity con la lista de reclamaciones filtradas.
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Listar reclamaciones por estado (Admin/Supervisor)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<ClaimResponseDTO>> findByStatus(@PathVariable ClaimStatus status){
        return ResponseEntity.ok(claimService.findByStatus(status));
    }

    /**
     * Modifica la información general de una reclamación (descripción, supervisor, aliado).
     * Solo accesible por personal administrativo o supervisores.
     *
     * @param id Identificador del siniestro a actualizar.
     * @param requestDTO Objeto con los nuevos datos del siniestro.
     * @return ResponseEntity con el ClaimResponseDTO actualizado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de un siniestro (Admin/Supervisor)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ClaimResponseDTO> updateClaim(@PathVariable Long id, @Valid @RequestBody ClaimUpdateDTO requestDTO){
        return ResponseEntity.ok(claimService.update(id, requestDTO));
    }

    /**
     * Actualiza rápidamente el estado de gestión de un siniestro.
     * Solo accesible por personal administrativo o supervisores.
     *
     * @param id Identificador del siniestro.
     * @param status Nuevo estado a asignar (ej: APPROVED, REJECTED).
     * @return ResponseEntity con el siniestro actualizado.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de un siniestro (Admin/Supervisor)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ClaimResponseDTO> updateStatusClaim(
            @PathVariable Long id, 
            @RequestParam ClaimStatus status){
        return ResponseEntity.ok(claimService.updateStatus(id, status));
    }

    /**
     * Elimina permanentemente un registro de siniestro del sistema.
     * Solo accesible por usuarios con rol de Administrador.
     *
     * @param id Identificador del siniestro a eliminar.
     * @return ResponseEntity con estado 204 No Content.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un siniestro (Solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
       claimService.delete(id);
       return ResponseEntity.noContent().build();
    }
}