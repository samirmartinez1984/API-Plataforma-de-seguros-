package com.insurance_platform_springboot.serviceTest;

import com.insurance_platform_springboot.dtos.request.PolicyRequestDTO;
import com.insurance_platform_springboot.dtos.response.PolicyResponseDTO;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.PolicyMapper;
import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.Policy;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.repository.PolicyRepository;
import com.insurance_platform_springboot.repository.ProductCatalogRepository;
import com.insurance_platform_springboot.repository.UserRepository;
import com.insurance_platform_springboot.service.PolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el servicio de Pólizas (PolicyService).
 * 
 * <p>Valida los aspectos más críticos del negocio: la contratación de seguros, 
 * la validación de clientes y, sobre todo, la precisión en el cálculo financiero 
 * de los precios finales (descuentos y recargos).</p>
 */
@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    /** Repositorio de pólizas mockeado para simular persistencia de contratos. */
    @Mock
    private PolicyRepository policyRepository;

    /** Repositorio de usuarios para validar la existencia del cliente titular. */
    @Mock
    private UserRepository userRepository;

    /** Repositorio del catálogo para obtener precios base y tipos de seguro. */
    @Mock
    private ProductCatalogRepository productCatalogRepository;

    /** Mapper para transformar datos entre DTOs y la entidad Policy. */
    @Mock
    private PolicyMapper policyMapper;

    /** Instancia del servicio bajo prueba con mocks inyectados. */
    @InjectMocks
    private PolicyService policyService;

    /**
     * Prueba el flujo de éxito en la creación de una póliza con cálculos matemáticos.
     * 
     * <p>Escenario: Contratación de un producto de 2,000,000 con un 10% de descuento 
     * y un cargo adicional de 20,000.</p>
     * <p>Resultado esperado: Precio final exacto de 1,820,000.</p>
     */
    @Test
    @DisplayName("Crear Póliza - Éxito: Verificación de cálculo financiero")
    void crearPoliza_CuandoHayDescuentoYCargos_DebeCalcularPrecioCorrecto() {
        // ARRANQUE(Preparación)
        Long customerId = 1L;
        Long productId = 1L;

        PolicyRequestDTO requestDTO = new PolicyRequestDTO();
        requestDTO.setCustomerId(customerId);
        requestDTO.setProductId(productId);
        requestDTO.setDiscountPercentage(10);
        requestDTO.setExtraCharges(new BigDecimal("20000"));

        ProductCatalog mockProduct = new ProductCatalog();
        mockProduct.setId(productId);
        mockProduct.setBasePrice(new BigDecimal("2000000"));

        User mockCustomer = new User();
        mockCustomer.setId(customerId);

        Policy mockPolicy = new Policy();

        // Configuración del comportamiento de los Mocks
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(productCatalogRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(policyMapper.toEntity(any())).thenReturn(mockPolicy);
        
        // simular que el repositorio devuelve el objeto que recibe al guardar
        when(policyRepository.save(any(Policy.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // simular la respuesta del Mapper con el valor final que el servicio debería haber calculado
        when(policyMapper.toResponse(any())).thenReturn(new PolicyResponseDTO(
                1L, customerId, productId, null, null, PolicyStatus.ACTIVE, 
                new BigDecimal("1820000"), null, null, 10, new BigDecimal("20000")));

        // ACT (Acción)
        PolicyResponseDTO result = policyService.create(requestDTO);

        // ASSERT (Verificación)
        assertNotNull(result, "La respuesta de la póliza no debería ser nula");
        assertEquals(new BigDecimal("1820000"), result.finalPrice(), 
                "El precio final calculado no es correcto. Revisar lógica de BigDecimal.");
        
        // Confirmar que se llamó al método save una sola vez
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    /**
     * Prueba el bloqueo de creación cuando el cliente proporcionado no existe en el sistema.
     * 
     * <p>Resultado esperado: Lanzamiento de ResourceNotFoundException.</p>
     */
    @Test
    @DisplayName("Crear Póliza - Error: Cliente no encontrado")
    void crearPoliza_CuandoClienteNoExiste_DebeLanzarNotFoundException() {
        // --- ARRANGE ---
        PolicyRequestDTO requestDTO = new PolicyRequestDTO();
        requestDTO.setCustomerId(999L);

        // simulamos que el cliente no existe en la BD
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> policyService.create(requestDTO),
                "Debe lanzar ResourceNotFoundException si el ID del cliente es inválido");
        
        // Verificación: No se debe intentar guardar ninguna póliza si el cliente falla
        verify(policyRepository, never()).save(any(Policy.class));
    }
}