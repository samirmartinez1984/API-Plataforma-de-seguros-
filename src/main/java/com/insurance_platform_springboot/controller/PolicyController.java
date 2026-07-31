package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.PolicyRequestDTO;
import com.insurance_platform_springboot.dtos.update.PolicyUpdateDTO;
import com.insurance_platform_springboot.dtos.response.PolicyResponseDTO;
import com.insurance_platform_springboot.security.CustomUserDetails;
import com.insurance_platform_springboot.service.PolicyService;
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
 * Controlador REST para la gestión integral de pólizas de seguro.
 * Permite la emisión de contratos, consulta de vigencias y administración por parte de personal autorizado.
 */
@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
@Tag(name = "Pólizas", description = "Endpoints para la gestión y consulta de pólizas de seguro")
public class PolicyController {

    private final PolicyService policyService;

    /**
     * Registra y emite una nueva póliza de seguro en el sistema.
     * Permitido para Administradores y para Clientes (USER) que adquieren su propio seguro.
     *
     * @param requestDTO Datos de la solicitud de la póliza (cliente, producto, fechas, etc).
     * @return ResponseEntity con el PolicyResponseDTO de la póliza creada y estado 201.
     */
    @PostMapping
    @Operation(summary = "Crear una nueva póliza de seguro (Admin/User)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PolicyResponseDTO> createPolicy(@Valid @RequestBody PolicyRequestDTO requestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.create(requestDTO));
    }

    /**
     * Recupera el listado completo de todas las pólizas registradas en la plataforma.
     * Solo accesible por personal administrativo o supervisores.
     *
     * @return ResponseEntity con la lista de PolicyResponseDTO.
     */
    @GetMapping
    @Operation(summary = "Listar todas las pólizas (Admin/Supervisor)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<PolicyResponseDTO>> findAllPolicies(){
        return ResponseEntity.ok(policyService.findAll());
    }

    /**
     * Busca los detalles técnicos de una póliza mediante su identificador único.
     * Solo accesible por personal administrativo o supervisores.
     *
     * @param id Identificador numérico de la póliza a buscar.
     * @return ResponseEntity con el PolicyResponseDTO encontrado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar póliza por ID (Admin/Supervisor)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<PolicyResponseDTO> findPolicyById(@PathVariable Long id){
        return ResponseEntity.ok(policyService.findById(id));
    }

    /**
     * Obtiene el listado de pólizas pertenecientes exclusivamente al cliente autenticado.
     * Utiliza el ID de usuario extraído de forma segura desde el token JWT.
     *
     * @param authentication Objeto de seguridad de Spring que contiene el principal autenticado.
     * @return ResponseEntity con la lista de pólizas del usuario logueado.
     */
    @GetMapping("/my-policies")
    @Operation(summary = "Obtener mis pólizas personales (Cliente)")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PolicyResponseDTO>> getMyPolicies(Authentication authentication) {
        Long customerId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        List<PolicyResponseDTO> policies = policyService.findByCustomerId(customerId);
        return ResponseEntity.ok(policies);
    }

    /**
     * Actualiza la información de una póliza existente (precios, fechas, estado).
     * Solo accesible por usuarios con rol de Administrador.
     *
     * @param id Identificador de la póliza que se desea actualizar.
     * @param requestDTO Objeto con los nuevos datos para la póliza.
     * @return ResponseEntity con el PolicyResponseDTO actualizado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de una póliza (Solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponseDTO> updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyUpdateDTO requestDTO){
        return ResponseEntity.ok(policyService.update(id, requestDTO));
    }

    /**
     * Realiza una baja lógica de la póliza en el sistema (cambio de estado a DELETED).
     * Solo accesible por usuarios con rol de Administrador.
     *
     * @param id Identificador de la póliza a eliminar.
     * @return ResponseEntity con estado 204 No Content si el borrado fue exitoso.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lógicamente una póliza (Solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id){
        policyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}