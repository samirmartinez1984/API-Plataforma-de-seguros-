package com.insurance_platform_springboot.service;

import com.insurance_platform_springboot.dtos.request.PolicyRequestDTO;
import com.insurance_platform_springboot.dtos.response.PolicyResponseDTO;
import com.insurance_platform_springboot.dtos.update.PolicyUpdateDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.PolicyMapper;
import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.model.Policy;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.repository.PolicyRepository;
import com.insurance_platform_springboot.repository.ProductCatalogRepository;
import com.insurance_platform_springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de gestionar el ciclo de vida de las pólizas de seguro.
 * Coordina la lógica entre usuarios, productos del catálogo y contratos (pólizas).
 */
@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final ProductCatalogRepository catalogProductRepository;
    private final PolicyMapper policyMapper;

    /**
     * Crea una nueva póliza de seguro para un cliente.
     * Realiza el cálculo automático del precio final basado en el precio base del producto,
     * descuentos y cargos adicionales proporcionados.
     *
     * @param requestDTO Datos de la solicitud de creación de póliza.
     * @return PolicyResponseDTO con los detalles de la póliza creada.
     * @throws ResourceNotFoundException Si el cliente o el producto no existen.
     */
    @Transactional
    public PolicyResponseDTO create(PolicyRequestDTO requestDTO){

        // 1. Validar que el cliente exista
        User customer = userRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El cliente no existe con el ID: " + requestDTO.getCustomerId()));

        // 2. Validar que el producto exista en el catálogo
        ProductCatalog product = catalogProductRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto no existe con el ID: " + requestDTO.getProductId()));

        // 3. Lógica de cálculo de precios
        BigDecimal basePrice = product.getBasePrice();
        BigDecimal finalPrice;

        // Lógica de Prioridad de Precios:
        // Si hay un cálculo explícito (descuento o cargos), lo realizamos sobre la base.
        if ((requestDTO.getDiscountPercentage() != null && requestDTO.getDiscountPercentage() > 0) || 
            (requestDTO.getExtraCharges() != null && requestDTO.getExtraCharges().compareTo(BigDecimal.ZERO) > 0)) {
            
            finalPrice = basePrice;

            if (requestDTO.getDiscountPercentage() != null) {
                BigDecimal discount = basePrice.multiply(BigDecimal.valueOf(requestDTO.getDiscountPercentage()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                finalPrice = finalPrice.subtract(discount);
            }

            if (requestDTO.getExtraCharges() != null) {
                finalPrice = finalPrice.add(requestDTO.getExtraCharges());
            }
        } 
        // Si no hay cálculos, pero se envió un precio final manual, se respeta ese valor.
        else if (requestDTO.getFinalPrice() != null && requestDTO.getFinalPrice().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = requestDTO.getFinalPrice();
        } 
        // Por defecto, el precio final es el precio base del producto.
        else {
            finalPrice = basePrice;
        }

        // 4. Mapear DTO a Entidad y establecer relaciones
        Policy policy = policyMapper.toEntity(requestDTO);
        policy.setProduct(product);
        policy.setCustomer(customer);
        policy.setBasePrice(basePrice);
        policy.setFinalPrice(finalPrice);
        
        // 5. Establecer estados y auditoría
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setCreatedAt(LocalDateTime.now());

        // 6. Persistencia
        Policy savedPolicy = policyRepository.save(policy);
        
        return policyMapper.toResponse(savedPolicy);
    }

    /**
     * Obtiene una lista de todas las pólizas registradas en el sistema.
     * @return Lista de PolicyResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> findAll(){
        return policyRepository.findAll().stream()
                .map(policyMapper::toResponse)
                .toList();
    }

    /**
     * Busca una póliza por su identificador único.
     * @param id ID de la póliza.
     * @return PolicyResponseDTO con los datos de la póliza.
     * @throws ResourceNotFoundException Si la póliza no existe.
     */
    @Transactional(readOnly = true)
    public PolicyResponseDTO findById(Long id){
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La póliza no existe con el ID: " + id));
        return policyMapper.toResponse(policy);
    }

    /**
     * Busca todas las pólizas asociadas a un cliente específico.
     * @param customerId ID del cliente.
     * @return Lista de pólizas del cliente.
     * @throws ResourceNotFoundException Si el cliente no existe.
     */
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> findByCustomerId(Long customerId){
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con ID: " + customerId));
        
        List<Policy> policies = policyRepository.findByCustomer(customer);
        return policies.stream()
                .map(policyMapper::toResponse)
                .toList();
    }

    /**
     * Actualiza la información de una póliza existente.
     * @param id ID de la póliza a actualizar.
     * @param requestDTO Datos actualizados.
     * @return PolicyResponseDTO con los cambios aplicados.
     * @throws ResourceNotFoundException Si la póliza no existe.
     */
    @Transactional
    public PolicyResponseDTO update(Long id, PolicyUpdateDTO requestDTO){
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La póliza no existe con el ID: " + id));

        policyMapper.updateEntity(requestDTO, policy);
        Policy savedPolicy = policyRepository.save(policy);
        return policyMapper.toResponse(savedPolicy);
    }

    /**
     * Realiza una eliminación lógica de la póliza cambiando su estado a DELETED.
     * No se permite eliminar si la póliza tiene reclamos asociados.
     *
     * @param id ID de la póliza a eliminar.
     * @throws ResourceNotFoundException Si la póliza no existe.
     * @throws ConflictException Si la póliza tiene siniestros asociados.
     */
    @Transactional
    public void delete(Long id){
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La póliza no existe con el ID: " + id));

        if (!policy.getClaims().isEmpty()){
            throw new ConflictException(
                    "No se puede eliminar una póliza que tiene siniestros o reclamos asociados");
        }

        policy.setStatus(PolicyStatus.DELETED);
        policy.setEndDate(LocalDate.now());
        policyRepository.save(policy);
    }
}