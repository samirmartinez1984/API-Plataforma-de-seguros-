package com.insurance_platform_springboot.service;

import com.insurance_platform_springboot.dtos.request.PartnerRequestDTO;
import com.insurance_platform_springboot.dtos.response.PartnerResponseDTO;
import com.insurance_platform_springboot.dtos.update.PartnerUpdateDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.PartnerMapper;
import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.model.Partner;
import com.insurance_platform_springboot.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de gestionar los aliados estratégicos (Partners) del sistema.
 * Coordina la administración de proveedores como talleres, clínicas y laboratorios.
 */
@Service
@RequiredArgsConstructor
public class PartnerService {
    
    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    
    /**
     * Registra un nuevo aliado (Partner) en el sistema.
     * Válida la unicidad del nombre y el email, y establece valores por defecto para el estado.
     * 
     * @param request Datos del aliado a crear.
     * @return PartnerResponseDTO con la información del aliado registrado.
     * @throws ConflictException Si el nombre o el email ya están registrados.
     */
    @Transactional
    public PartnerResponseDTO create(PartnerRequestDTO request){
        
        // 1. Validar que el nombre sea único
        if (partnerRepository.existsByPartnerName(request.getPartnerName())){
            throw new ConflictException("El aliado con nombre '" + request.getPartnerName() + "' ya existe en el sistema.");
        }

        // 2. Validar que el email sea único (si se proporciona)
        if (request.getEmail() != null && partnerRepository.findByEmail(request.getEmail()).isPresent()){
            throw new ConflictException("El email '" + request.getEmail() + "' ya está en uso por otro aliado.");
        }

        // 3. Mapear DTO a Entidad
        Partner partner = partnerMapper.toEntity(request);
        
        // 4. Asignar valores obligatorios
        partner.setCreatedAt(LocalDateTime.now());
        
        if (partner.getStatus() == null) {
            partner.setStatus(PartnerStatus.ACTIVE);
        }
        
        // 5. Persistencia y respuesta
        Partner saved = partnerRepository.save(partner);
        return partnerMapper.toResponse(saved);
    }

    /**
     * Obtiene una lista de todos los aliados registrados en el sistema.
     * @return Lista de PartnerResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<PartnerResponseDTO> findAll() {
        return partnerRepository.findAll()
                .stream()
                .map(partnerMapper::toResponse)
                .toList();
    }

    /**
     * Busca un aliado por su identificador único.
     * @param id ID del aliado a buscar.
     * @return PartnerResponseDTO con los datos encontrados.
     * @throws ResourceNotFoundException Si el aliado no existe.
     */
    @Transactional(readOnly = true)
    public PartnerResponseDTO findById(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El aliado (Partner) no existe con el ID: " + id));
        return partnerMapper.toResponse(partner);
    }

    /**
     * Actualiza la información de un aliado existente.
     * @param id ID del aliado a actualizar.
     * @param request Datos actualizados.
     * @return PartnerResponseDTO modificado.
     */
    @Transactional
    public PartnerResponseDTO update(Long id, PartnerUpdateDTO request) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El aliado (Partner) no existe con el ID: " + id));

        if (request.getPartnerName() != null &&
                !partner.getPartnerName().equals(request.getPartnerName()) &&
                partnerRepository.existsByPartnerName(request.getPartnerName())) {
            throw new ConflictException("El nombre '" + request.getPartnerName() + "' ya está en uso.");
        }

        if (request.getEmail() != null &&
                !partner.getEmail().equals(request.getEmail()) &&
                partnerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("El email '" + request.getEmail() + "' ya está en uso.");
        }

        partnerMapper.updateEntity(request, partner);
        return partnerMapper.toResponse(partnerRepository.save(partner));
    }

    /**
     * Elimina un aliado del sistema por su ID.
     * @param id ID del aliado a eliminar.
     */
    @Transactional
    public void delete(Long id) {
        if (!partnerRepository.existsById(id)) {
            throw new ResourceNotFoundException("El aliado (Partner) no existe con el ID: " + id);
        }
        partnerRepository.deleteById(id);
    }

    /**
     * Actualiza exclusivamente el estado de un aliado (Activo/Inactivo).
     * @param id ID del aliado.
     * @param newStatus Nuevo estado a asignar.
     * @return PartnerResponseDTO actualizado.
     */
    @Transactional
    public PartnerResponseDTO updateStatus(Long id, PartnerStatus newStatus){
        Partner partnerExist = partnerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "El aliado no existe con el ID: " + id));
        partnerExist.setStatus(newStatus);
        return partnerMapper.toResponse(partnerRepository.save(partnerExist));
    }
}
