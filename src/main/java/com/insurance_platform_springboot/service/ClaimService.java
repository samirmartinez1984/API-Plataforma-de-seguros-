package com.insurance_platform_springboot.service;

import com.insurance_platform_springboot.dtos.request.ClaimRequestDTO;
import com.insurance_platform_springboot.dtos.response.ClaimResponseDTO;
import com.insurance_platform_springboot.dtos.update.ClaimUpdateDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.ClaimMapper;
import com.insurance_platform_springboot.model.Claim;
import com.insurance_platform_springboot.model.enums.ClaimStatus;
import com.insurance_platform_springboot.model.Partner;
import com.insurance_platform_springboot.model.Policy;
import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.repository.ClaimRepository;
import com.insurance_platform_springboot.repository.PartnerRepository;
import com.insurance_platform_springboot.repository.PolicyRepository;
import com.insurance_platform_springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de gestionar las reclamaciones o siniestros asociados a las pólizas.
 * Coordina la validación entre clientes, pólizas activas, supervisores y aliados de servicio.
 */
@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final ClaimMapper claimMapper;

    /**
     * Registra un nuevo siniestro (Claim) en el sistema.
     * Realiza validaciones críticas: existencia de entidades, estado activo de la póliza,
     * titularidad del cliente y rol del supervisor.
     *
     * @param requestDTO Datos de la reclamación.
     * @return ClaimResponseDTO con los detalles del registro.
     * @throws ResourceNotFoundException Si alguna de las entidades relacionadas no existe.
     * @throws ConflictException Si la póliza no está activa o el cliente no es el titular.
     */
    @Transactional
    public ClaimResponseDTO create(ClaimRequestDTO requestDTO) {

        // 1. Validar que la póliza exista y esté activa
        Policy policyExist = policyRepository.findById(requestDTO.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La póliza no existe con el ID: " + requestDTO.getPolicyId()));
        
        if (policyExist.getStatus() != PolicyStatus.ACTIVE) {
            throw new ConflictException("ERROR: La póliza " + requestDTO.getPolicyId() + " no está vigente.");
        }

        // 2. Validar cliente y titularidad de la póliza
        User userExist = userRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario cliente no existe con el ID: " + requestDTO.getCustomerId()));
        
        if (!policyExist.getCustomer().getId().equals(userExist.getId())) {
            throw new ConflictException("ERROR: El cliente con ID " + userExist.getId()
                    + " no es el titular de la póliza " + policyExist.getId());
        }

        // 3. Validar supervisor y su rol
        User supervisor = userRepository.findById(requestDTO.getSupervisorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El supervisor no existe con el ID: " + requestDTO.getSupervisorId()));
        
        if (!supervisor.getRole().getName().equals(RoleConstants.ROLE_SUPERVISOR)) {
            throw new ConflictException("ERROR: El usuario asignado no tiene el rol de SUPERVISOR.");
        }

        // 4. Validar que el aliado (Partner) exista
        Partner partnerExist = partnerRepository.findById(requestDTO.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El aliado (Partner) no existe con el ID: " + requestDTO.getPartnerId()));

        // 5. Mapear y establecer relaciones completas
        Claim claim = claimMapper.toEntity(requestDTO);
        claim.setPolicy(policyExist);
        claim.setCustomer(userExist);
        claim.setSupervisor(supervisor);
        claim.setPartner(partnerExist);

        // 6. Establecer estados y auditoría
        claim.setStatus(ClaimStatus.REGISTERED);
        claim.setCreatedAt(LocalDateTime.now());
        
        if (claim.getReportedAt() == null) {
            claim.setReportedAt(LocalDateTime.now());
        }

        // 7. Persistencia
        Claim savedClaim = claimRepository.save(claim);
        return claimMapper.toResponse(savedClaim);
    }

    /**
     * Obtiene una lista de todas las reclamaciones registradas en el sistema.
     * @return Lista de ClaimResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<ClaimResponseDTO> findAll(){
        return claimRepository.findAll()
                .stream().map(claimMapper::toResponse)
                .toList();
    }

    /**
     * Busca una reclamación específica por su ID.
     * @param id Identificador único del siniestro.
     * @return ClaimResponseDTO con los datos encontrados.
     * @throws ResourceNotFoundException Si el siniestro no existe.
     */
    @Transactional(readOnly = true)
    public ClaimResponseDTO findById(Long id){
        Claim claimExist = claimRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "La reclamación no existe con el ID: " + id));
        return claimMapper.toResponse(claimExist);
    }

    /**
     * Obtiene todas las reclamaciones asociadas a un cliente específico.
     * @param customerId ID del cliente.
     * @return Lista de reclamaciones del cliente.
     * @throws ResourceNotFoundException Si el cliente no existe.
     */
    @Transactional(readOnly = true)
    public List<ClaimResponseDTO> findByCustomerId(Long customerId){
        if (!userRepository.existsById(customerId)){
            throw new ResourceNotFoundException("El cliente no existe con el ID: " + customerId);
        }
        List<Claim> claims = claimRepository.findByCustomerId(customerId);
        return claims.stream()
                .map(claimMapper::toResponse)
                .toList();
    }

    /**
     * Actualiza la información de una reclamación existente.
     * Permite reasignar supervisores y aliados de servicio.
     *
     * @param id ID de la reclamación a actualizar.
     * @param requestDTO Datos actualizados.
     * @return ClaimResponseDTO con los cambios aplicados.
     */
    @Transactional
    public ClaimResponseDTO update(Long id, ClaimUpdateDTO requestDTO){
        Claim claimExist = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La reclamación no existe con el ID: " + id));

        claimMapper.updateEntity(requestDTO, claimExist);

        // Lógica de reasignación selectiva
        if (requestDTO.getSupervisorId() != null) {
            User newSupervisor = userRepository.findById(requestDTO.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("El nuevo supervisor no existe."));
            claimExist.setSupervisor(newSupervisor);
        }

        if (requestDTO.getPartnerId() != null){
            Partner newPartner = partnerRepository.findById(requestDTO.getPartnerId())
                    .orElseThrow(()-> new ResourceNotFoundException("El nuevo aliado no existe."));
            claimExist.setPartner(newPartner);
        }

        return claimMapper.toResponse(claimRepository.save(claimExist));
    }

    /**
     * Filtra las reclamaciones por su estado actual (APPROVED, REJECTED, etc.).
     * @param status Estado a filtrar.
     * @return Lista de reclamaciones en dicho estado.
     */
    @Transactional(readOnly = true)
    public List<ClaimResponseDTO> findByStatus(ClaimStatus status){
        return claimRepository.findByStatus(status)
                .stream()
                .map(claimMapper::toResponse)
                .toList();
    }

    /**
     * Actualiza rápidamente el estado de una reclamación.
     * @param id ID de la reclamación.
     * @param newStatus Nuevo estado a asignar.
     * @return ClaimResponseDTO actualizado.
     */
    @Transactional
    public ClaimResponseDTO updateStatus(Long id, ClaimStatus newStatus){
        Claim claimExist = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La reclamación no existe con el ID: " + id));
        claimExist.setStatus(newStatus);
        return claimMapper.toResponse(claimRepository.save(claimExist));
    }

    /**
     * Elimina físicamente una reclamación del sistema.
     * @param id ID de la reclamación a eliminar.
     */
    @Transactional
    public void delete(Long id){
        if (!claimRepository.existsById(id)){
            throw new ResourceNotFoundException("No se encontró la reclamación con ID: " + id);
        }
        claimRepository.deleteById(id);
    }
}
