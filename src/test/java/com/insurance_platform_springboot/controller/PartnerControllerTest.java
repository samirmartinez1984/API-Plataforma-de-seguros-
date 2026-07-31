package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.PartnerRequestDTO;
import com.insurance_platform_springboot.model.Partner;
import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.model.enums.PartnerType;
import com.insurance_platform_springboot.repository.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para el controlador de Aliados (PartnerController).
 * 
 * <p>Válida la accesibilidad de los endpoints públicos y protege las rutas administrativas
 * para que solo los usuarios con el rol adecuado (ADMIN) puedan alterar la red de proveedores.</p>
 */
public class PartnerControllerTest extends BaseControllerTest {

    /** Repositorio real inyectado para la preparación de datos de prueba en la BD H2. */
    @Autowired
    private PartnerRepository partnerRepository;

    /**
     * Limpia el estado de la base de datos de pruebas antes de la ejecución de cada test.
     * Garantiza el aislamiento y evita falsos positivos por datos residuales.
     */
    @BeforeEach
    void setUp() {
        partnerRepository.deleteAll();
    }

    /**
     * Válida que la red de aliados sea pública para consulta por parte de los clientes.
     * 
     * <p>Escenario: Un usuario con rol USER hace un GET a /partners.</p>
     * <p>Resultado esperado: HTTP 200 OK y la lista de aliados en formato JSON.</p>
     */
    @Test
    @DisplayName("GET /partners - Éxito: Cualquier cliente puede ver la red de aliados")
    @WithMockUser(roles = "USER")
    void listarPartner_CuandoUsuarioEsCliente_DebeRetornar200Ok() throws Exception {
        // --- ARRANGE ---
        // sembramos un aliado válido directamente en la base de datos de pruebas
        Partner partner = new Partner();
        partner.setPartnerName("Taller Central");
        partner.setType(PartnerType.WORKSHOP);
        partner.setStatus(PartnerStatus.ACTIVE);
        partner.setAddress("Calle 123");
        partner.setPhone("5555-000");
        partner.setEmail("taller@central.com");
        partner.setCreatedAt(LocalDateTime.now());
        partnerRepository.save(partner);

        // --- ACT & ASSERT ---
        mockMvc.perform(get("/partners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Verificamos que el JSON dé respuesta contenga el aliado que sembramos
                .andExpect(jsonPath("$[0].partnerName").value("Taller Central")); 
    }

    /**
     * Válida que el personal autorizado pueda expandir la red de aliados.
     * 
     * <p>Escenario: Un Administrador envía un POST válido para registrar una clínica.</p>
     * <p>Resultado esperado: HTTP 201 Created y los datos persistidos en el JSON de respuesta.</p>
     */
    @Test
    @DisplayName("POST /partners - Éxito: Admin registra nuevo aliado")
    @WithMockUser(roles = "ADMIN") 
    void crearPartner_CuandoUsuarioEsAdmin_DebeRetornar201Created() throws Exception {
        // --- ARRANGE ---
        // Construimos el payload JSON (DTO) que simula la petición del frontend
        PartnerRequestDTO requestDTO = new PartnerRequestDTO("Clínica de Salud",
                PartnerType.CLINIC, "Av. Pedro de Heredia", "555-000",
                "salud@clinica.com", PartnerStatus.ACTIVE);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                
                // Verificamos código de éxito y validamos el campo devuelto
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.partnerName").value("Clínica de Salud"));
    }

    /**
     * Válida la seguridad de la plataforma bloqueando intentos de manipulación no autorizados.
     * 
     * <p>Escenario: Un cliente normal (USER) intenta registrar un taller mediante un ataque directo a la API.</p>
     * <p>Resultado esperado: Bloqueo de Spring Security y respuesta estructurada 403 Forbidden del GlobalExceptionHandler.</p>
     */
    @Test
    @DisplayName("POST /partners - Error de Seguridad: Cliente no tiene permisos")
    @WithMockUser(roles = "USER")
    void crearPartner_CuandoUsuarioEsCliente_DebeRetornar403Forbidden() throws Exception{
        // --- ARRANGE ---
        // El intruso prepara un payload de registro
        PartnerRequestDTO requestDTO = new PartnerRequestDTO("Taller Pirata", PartnerType.WORKSHOP,
                "Desconocida", "000-000", "pirata@test.com", PartnerStatus.ACTIVE);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))

                // Esperamos que el filtro de seguridad lo intercepte (403)
                .andExpect(status().isForbidden())
                // Validamos que el manejador global devuelve el mensaje seguro en español
                .andExpect(jsonPath("$.error").value("Acceso Denegado"));
    }
}