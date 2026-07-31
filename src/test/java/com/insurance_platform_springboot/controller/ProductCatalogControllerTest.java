package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.ProductCatalogRequestDTO;
import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import com.insurance_platform_springboot.repository.ProductCatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Importaciones correctas para MockMvc
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para el controlador del Catálogo de Productos.
 * Válida la seguridad de roles (ADMIN vs USER) y la correcta respuesta de los endpoints.
 */
public class ProductCatalogControllerTest extends BaseControllerTest {

    /** Repositorio real para preparar datos en la base de datos de pruebas H2. */
    @Autowired
    private ProductCatalogRepository productCatalogRepository;

    @Autowired
    private com.insurance_platform_springboot.repository.PolicyRepository policyRepository;

    /**
     * Este método se ejecuta ANTES de cada test para limpiar la base de datos.
     * Así, un test no interfiere con el siguiente.
     */
    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
        productCatalogRepository.deleteAll();
    }

    /**
     * Válida que un usuario con rol 'USER' pueda consultar la lista de productos.
     * Resultado esperado: HTTP 200 OK.
     */
    @Test
    @DisplayName("GET /products - Éxito: Cliente puede ver el catálogo")
    @WithMockUser(roles = "USER") // simulamos ser un usuario normal
    void listarProductos_CuandoUsuarioEsCliente_DebeRetornar200Ok() throws Exception {
        // --- ARRANGE ---
        ProductCatalog producto = new ProductCatalog(); // <--- NOMBRE CORRECTO AQUÍ
        producto.setNameProduct("seguro de Vida Básico");
        producto.setBasePrice(new BigDecimal("50.00"));
        producto.setType(ProductType.LIFE);
        producto.setStatus(ProductStatus.ACTIVE);
        producto.setCreatedAt(LocalDateTime.now()); // Fecha de creación añadida para que no falle la BD
        productCatalogRepository.save(producto);

        // --- ACT & ASSERT ---
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nameProduct").value("seguro de Vida Básico"));
    }

    /**
     * Válida que un usuario con rol 'ADMIN' pueda crear un nuevo producto.
     * Resultado esperado: HTTP 201 Created.
     */
    @Test
    @DisplayName("POST /products - Éxito: Admin puede crear productos")
    @WithMockUser(roles = "ADMIN") // simulamos ser un Administrador
    void crearProducto_CuandoUsuarioEsAdmin_DebeRetornar201Created() throws Exception {
        // --- ARRANGE ---
        ProductCatalogRequestDTO requestDTO = new ProductCatalogRequestDTO(
                "seguro de Auto Premium", "Cobertura total", "Todo riesgo",
                "Actos de guerra", new BigDecimal("250.00"), ProductType.AUTO, ProductStatus.ACTIVE);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    /**
     * Válida que la seguridad bloquee a un usuario normal que intente crear un producto.
     * Resultado esperado: HTTP 403 Forbidden.
     */
    @Test
    @DisplayName("POST /products - Error: Cliente no puede crear productos")
    @WithMockUser(roles = "USER") // simulamos ser un Cliente
    void crearProducto_CuandoUsuarioEsCliente_DebeRetornar403Forbidden() throws Exception {
        // --- ARRANGE ---
        ProductCatalogRequestDTO request = new ProductCatalogRequestDTO(
                "Intento Malicioso", "Descripción", "Cobertura",
                "Exclusiones", BigDecimal.TEN, ProductType.AUTO, ProductStatus.ACTIVE);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Esperamos 403, que ahora manejará el GlobalExceptionHandler
    }
}