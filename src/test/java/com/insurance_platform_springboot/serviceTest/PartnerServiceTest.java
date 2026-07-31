package com.insurance_platform_springboot.serviceTest;

import com.insurance_platform_springboot.dtos.request.PartnerRequestDTO;
import com.insurance_platform_springboot.dtos.response.PartnerResponseDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.PartnerMapper;
import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.model.enums.PartnerType;
import com.insurance_platform_springboot.model.Partner;
import com.insurance_platform_springboot.repository.PartnerRepository;
import com.insurance_platform_springboot.service.PartnerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el servicio de Aliados (PartnerService).
 * 
 * <p>Válida la gestión de los proveedores de servicios (talleres, clínicas, etc.),
 * asegurando la unicidad de sus registros y el correcto flujo de sus estados operativos.</p>
 */
@ExtendWith(MockitoExtension.class)
public class PartnerServiceTest {

    /** Simulación del repositorio de aliados. */
    @Mock
    private PartnerRepository partnerRepository;

    /** Simulación del mapper para validación de mapeos DTO-Entidad. */
    @Mock
    private PartnerMapper partnerMapper;

    /** Instancia del servicio de aliados con mocks inyectados. */
    @InjectMocks
    private PartnerService partnerService;

    /**
     * Prueba el registro exitoso de un nuevo aliado.
     * 
     * <p>Escenario: El aliado no existe y proporciona datos de contacto válidos.</p>
     * <p>Verifica: Persistencia en el repositorio y coherencia en el DTO de respuesta.</p>
     */
    @Test
    @DisplayName("Crear Partner - Éxito: Registro válido de aliado")
    void crearPartner_CuandoDatosSonValidos_DebeRetornarPartnerCreado() {
        //  ARRANQUE (Preparación)
        PartnerRequestDTO request = new PartnerRequestDTO(
                "Taller Central", PartnerType.WORKSHOP, "Calle 123", 
                "+34 900", "taller@taller.com", PartnerStatus.ACTIVE);

        Partner mockPartner = new Partner();
        mockPartner.setId(1L);
        mockPartner.setPartnerName(request.getPartnerName());

        PartnerResponseDTO expectedResponse = new PartnerResponseDTO(
                1L, request.getPartnerName(), request.getType().name(), 
                request.getAddress(), request.getPhone(), request.getEmail(), 
                request.getStatus().name(), LocalDateTime.now());

        // Programar el comportamiento de los Mocks
        when(partnerRepository.existsByPartnerName(request.getPartnerName())).thenReturn(false);
        when(partnerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(partnerMapper.toEntity(request)).thenReturn(mockPartner);
        when(partnerRepository.save(any(Partner.class))).thenReturn(mockPartner);
        when(partnerMapper.toResponse(mockPartner)).thenReturn(expectedResponse);

        // ACT (Acción)
        PartnerResponseDTO result = partnerService.create(request);

        // ASSERT (Verificación)
        assertNotNull(result, "El aliado creado no debería ser nulo");
        assertEquals(request.getPartnerName(), result.partnerName(), "El nombre del aliado no coincide");
        
        // Confirmar que se llamó al repositorio para guardar
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    /**
     * Prueba la prevención de registros duplicados por correo electrónico.
     * 
     * <p>Resultado esperado: Lanzamiento de ConflictException.</p>
     */
    @Test
    @DisplayName("Crear Partner - Error: Email duplicado")
    void crearPartner_CuandoEmailYaExiste_DebeLanzarConflictException() {
        // --- ARRANGE ---
        PartnerRequestDTO request = new PartnerRequestDTO();
        request.setEmail("duplicado@test.com");

        // simulamos que el email ya está registrado
        when(partnerRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Partner()));

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> partnerService.create(request),
                "No se deben permitir dos aliados con el mismo email");
        
        // Verificación: El sistema no debe intentar guardar si el email existe
        verify(partnerRepository, never()).save(any(Partner.class));
    }

    /**
     * Prueba el cambio de estado operativo de un aliado (Activo/Inactivo).
     * 
     * <p>Verifica: Que la entidad cambie su campo status antes de ser guardada.</p>
     */
    @Test
    @DisplayName("Actualizar Estado - Éxito: Cambio de status operativo")
    void actualizarEstado_CuandoIdExiste_DebeCambiarStatus() {
        // ARRANGE
        Long partnerId = 1L;
        Partner existingPartner = new Partner();
        existingPartner.setId(partnerId);
        existingPartner.setStatus(PartnerStatus.ACTIVE);

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(existingPartner));
        when(partnerRepository.save(any(Partner.class))).thenReturn(existingPartner);
        
        // ACT
        partnerService.updateStatus(partnerId, PartnerStatus.INACTIVE);

        // ASSERT
        assertEquals(PartnerStatus.INACTIVE, existingPartner.getStatus(), 
                "El estado de la entidad debería ser INACTIVE tras la actualización");
        
        verify(partnerRepository, times(1)).save(existingPartner);
    }

    /**
     * Prueba el manejo de error al intentar eliminar un aliado inexistente.
     */
    @Test
    @DisplayName("Eliminar Partner - Error: Aliado no encontrado")
    void eliminarPartner_CuandoNoExiste_DebeLanzarNotFoundException() {
        //  ARRANGE
        when(partnerRepository.existsById(1L)).thenReturn(false);

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> partnerService.delete(1L),
                "Debe lanzar excepción si se intenta borrar un ID que no está en la BD");
    }
}