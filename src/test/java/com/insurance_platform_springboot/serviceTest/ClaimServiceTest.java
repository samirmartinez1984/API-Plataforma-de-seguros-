package com.insurance_platform_springboot.serviceTest;

import com.insurance_platform_springboot.dtos.request.ClaimRequestDTO;
import com.insurance_platform_springboot.dtos.response.ClaimResponseDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.mapper.ClaimMapper;
import com.insurance_platform_springboot.model.Claim;
import com.insurance_platform_springboot.model.Partner;
import com.insurance_platform_springboot.model.Policy;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.model.enums.ClaimStatus;
import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.repository.ClaimRepository;
import com.insurance_platform_springboot.repository.PartnerRepository;
import com.insurance_platform_springboot.repository.PolicyRepository;
import com.insurance_platform_springboot.repository.UserRepository;
import com.insurance_platform_springboot.service.ClaimService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el servicio de Reclamaciones (ClaimService).
 * 
 * <p>Esta es la prueba más compleja del sistema, ya que valida la interacción entre
 * múltiples entidades: Póliza Activa, Titularidad del Cliente, Rol del Supervisor y 
 * existencia del Aliado de servicio.</p>
 */
@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    /** Simulación del repositorio de reclamaciones. */
    @Mock
    private ClaimRepository claimRepository;

    /** Simulación del repositorio de pólizas para validar vigencia y titularidad. */
    @Mock
    private PolicyRepository policyRepository;

    /** Simulación del repositorio de usuarios para validar clientes y supervisores. */
    @Mock
    private UserRepository userRepository;

    /** Simulación del repositorio de aliados estratégicos. */
    @Mock
    private PartnerRepository partnerRepository;

    /** Simulación del mapper para conversión de datos. */
    @Mock
    private ClaimMapper claimMapper;

    /** Instancia del servicio bajo prueba con inyección de mocks. */
    @InjectMocks
    private ClaimService claimService;

    /**
     * Prueba el flujo exitoso de reporte de un siniestro.
     * 
     * <p>Escenario: El cliente es dueño de una póliza activa y asigna un supervisor y aliado válidos.</p>
     * <p>Verifica: Que se establezcan todas las relaciones y se guarde el reclamo correctamente.</p>
     */
    @Test
    @DisplayName("Crear Claim - Éxito: Reporte de siniestro válido")
    void crearClaim_CuandoTodoEsCorrecto_DebeRetornarClaimCreado(){
        // ARRANGE (Preparación)
        Long customerId = 1L;
        Long policyId = 10L;

        ClaimRequestDTO requestDTO = new ClaimRequestDTO();
        requestDTO.setCustomerId(customerId);
        requestDTO.setPolicyId(policyId);
        requestDTO.setSupervisorId(2L);
        requestDTO.setPartnerId(3L);

        // simular Cliente
        User mockCustomer = new User();
        mockCustomer.setId(customerId);

        // simular una Póliza ACTIVA que pertenece al cliente mockCustomer
        Policy mockPolicy = new Policy();
        mockPolicy.setId(policyId);
        mockPolicy.setStatus(PolicyStatus.ACTIVE);
        mockPolicy.setCustomer(mockCustomer);

        // simular Supervisor con el rol institucional correcto
        Role supervisorRole = new Role();
        supervisorRole.setName(RoleConstants.ROLE_SUPERVISOR);
        User mockSupervisor = new User();
        mockSupervisor.setRole(supervisorRole);

        // Programar el comportamiento de los Mocks
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(mockPolicy));
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(requestDTO.getSupervisorId())).thenReturn(Optional.of(mockSupervisor));
        when(partnerRepository.findById(requestDTO.getPartnerId())).thenReturn(Optional.of(new Partner()));

        when(claimMapper.toEntity(any())).thenReturn(new Claim());
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArguments()[0]);
        when(claimMapper.toResponse(any())).thenReturn(new ClaimResponseDTO(
                1L, policyId, customerId, 2L, 3L, "siniestro Vial", ClaimStatus.REGISTERED, null, null));

        // ACT (Acción)
        ClaimResponseDTO result = claimService.create(requestDTO);

        // ASSERT (Verificación)
        assertNotNull(result, "El resultado de la reclamación no debería ser nulo");
        // Confirmar que se llamó al repositorio para persistir los datos
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    /**
     * Prueba la regla de negocio que impide reclamar sobre pólizas ajenas.
     * 
     * <p>Escenario: Un usuario intenta reportar un siniestro sobre una póliza que le pertenece a otro cliente.</p>
     * <p>Resultado esperado: Lanzamiento de ConflictException por violación de seguridad/titularidad.</p>
     */
    @Test
    @DisplayName("Crear Claim - Error: Intento de reclamo sobre póliza ajena")
    void crearClaim_CuandoClienteNoEsTitular_DebeLanzarConflictException() {
        // ARRANQUE
        Long realOwnerId = 1L;
        Long intruderId = 2L;

        ClaimRequestDTO request = new ClaimRequestDTO();
        request.setCustomerId(intruderId); // El intruso intenta el fraude
        request.setPolicyId(10L);

        User intruder = new User();
        intruder.setId(intruderId);

        User realOwner = new User();
        realOwner.setId(realOwnerId);

        Policy mockPolicy = new Policy();
        mockPolicy.setCustomer(realOwner); // La póliza le pertenece al dueño real, no al intruso
        mockPolicy.setStatus(PolicyStatus.ACTIVE);

        when(policyRepository.findById(10L)).thenReturn(Optional.of(mockPolicy));
        when(userRepository.findById(intruderId)).thenReturn(Optional.of(intruder));

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> claimService.create(request),
                "Debe lanzar ConflictException si el cliente no es el titular de la póliza");
        
        // Confirmación de seguridad: El sistema no debe intentar guardar nada en la BD
        verify(claimRepository, never()).save(any());
    }
}