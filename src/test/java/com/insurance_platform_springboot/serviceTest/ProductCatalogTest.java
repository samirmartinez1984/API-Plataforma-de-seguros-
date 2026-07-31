package com.insurance_platform_springboot.serviceTest;

import com.insurance_platform_springboot.dtos.request.ProductCatalogRequestDTO;
import com.insurance_platform_springboot.dtos.response.ProductCatalogResponseDTO;
import com.insurance_platform_springboot.dtos.update.ProductCatalogUpdateDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.ProductCatalogMapper;
import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import com.insurance_platform_springboot.repository.ProductCatalogRepository;
import com.insurance_platform_springboot.service.ProductCatalogService;
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
 * Clase de pruebas unitarias para el servicio del Catálogo de Productos (ProductCatalogService).
 * 
 * <p>Aislá la lógica de gestión comercial de los productos de seguros, validando que
 * la oferta del catálogo sea íntegra y que no existan duplicidades de nombres que 
 * puedan confundir al cliente o al sistema de facturación.</p>
 */
@ExtendWith(MockitoExtension.class)
public class ProductCatalogTest {

    /** Simulación del repositorio del catálogo para evitar acceso real a MySQL. */
    @Mock
    private ProductCatalogRepository productCatalogRepository;

    /** Simulación del mapper para validación de transformaciones DTO-Entidad. */
    @Mock
    private ProductCatalogMapper productCatalogMapper;

    /** Instancia del servicio comercial con dependencias simuladas. */
    @InjectMocks
    private ProductCatalogService productCatalogService;

    /**
     * Prueba la creación exitosa de un producto de seguro en el catálogo.
     * 
     * <p>Validaciones: Verifica que el producto sea persistido y que el ID retornado 
     * sea coherente con la respuesta del servidor.</p>
     */
    @Test
    @DisplayName("Crear Producto - Éxito: Registro comercial válido")
    void crearProducto_CuandoNombreNoExiste_DebeRetornarProductoCreado() {
        // ARRANQUE (Preparación)
        ProductCatalogRequestDTO request = new ProductCatalogRequestDTO(
                "seguro Vida Plus",
                "Cobertura total por fallecimiento", 
                "Muerte, Invalidez", 
                "suicidio",
                new BigDecimal("100.00"), 
                ProductType.LIFE, 
                ProductStatus.ACTIVE);

        ProductCatalog mockProduct = new ProductCatalog();
        mockProduct.setNameProduct(request.getNameProduct());

        ProductCatalogResponseDTO expectedResponse = new ProductCatalogResponseDTO(
                1L, request.getNameProduct(), request.getDescription(), request.getCoverage(),
                request.getExclusions(), request.getBasePrice(), request.getType(), 
                request.getStatus(), null);

        // Programación de los Mocks
        when(productCatalogRepository.existsByNameProduct(request.getNameProduct())).thenReturn(false);
        when(productCatalogMapper.toEntity(request)).thenReturn(mockProduct);
        when(productCatalogRepository.save(any(ProductCatalog.class))).thenReturn(mockProduct);
        when(productCatalogMapper.toResponse(mockProduct)).thenReturn(expectedResponse);

        // ACT (Acción)
        ProductCatalogResponseDTO result = productCatalogService.create(request);

        // --- ASSERT (Verificación) ---
        assertNotNull(result, "El producto creado no debería ser nulo");
        assertEquals(request.getNameProduct(), result.nameProduct(), "El nombre del producto devuelto no coincide");
        
        // Confirmar que el repositorio guardó físicamente el producto
        verify(productCatalogRepository, times(1)).save(any(ProductCatalog.class));
    }

    /**
     * Prueba el bloqueo de creación por duplicidad de nombre de producto.
     * 
     * <p>Resultado esperado: Lanzamiento de ConflictException para proteger la integridad del catálogo.</p>
     */
    @Test
    @DisplayName("Crear Producto - Error: Bloqueo por nombre duplicado")
    void crearProducto_CuandoNombreYaExiste_DebeLanzarConflictException() {
        // ARRANQUE
        ProductCatalogRequestDTO request = new ProductCatalogRequestDTO();
        request.setNameProduct("seguro Existente");

        when(productCatalogRepository.existsByNameProduct(request.getNameProduct())).thenReturn(true);

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> productCatalogService.create(request),
                "El catálogo no debe permitir dos productos con el mismo nombre");
        
        // Verificación: Nunca se debe intentar guardar si el nombre ya existe
        verify(productCatalogRepository, never()).save(any(ProductCatalog.class));
    }

    /**
     * Prueba el manejo de error al intentar consultar un producto inexistente.
     * 
     * <p>Resultado esperado: Lanzamiento de ResourceNotFoundException.</p>
     */
    @Test
    @DisplayName("Buscar por ID - Error: Producto no encontrado")
    void buscarPorId_CuandoNoExiste_DebeLanzarNotFoundException() {
        // ARRANQUE
        when(productCatalogRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> productCatalogService.findById(1L),
                "Debe informar que el producto no existe mediante una excepción específica");
    }

    /**
     * Prueba que al actualizar no se permita usar un nombre ya registrado por otro producto.
     */
    @Test
    @DisplayName("Actualizar Producto - Error: Nuevo nombre ya está en uso")
    void actualizar_CuandoNuevoNombreYaExisteEnOtro_DebeLanzarConflictException() {
        // ARRANQUE
        Long targetId = 1L;
        ProductCatalogUpdateDTO updateDTO = new ProductCatalogUpdateDTO();
        updateDTO.setNameProduct("NombreDeOtroProducto");

        ProductCatalog existingProduct = new ProductCatalog();
        existingProduct.setId(targetId);
        existingProduct.setNameProduct("NombreActual");

        when(productCatalogRepository.findById(targetId)).thenReturn(Optional.of(existingProduct));
        when(productCatalogRepository.existsByNameProduct(updateDTO.getNameProduct())).thenReturn(true);

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> productCatalogService.update(targetId, updateDTO),
                "No se debe permitir 'robar' el nombre de otro producto durante la actualización");

        verify(productCatalogRepository, never()).save(any(ProductCatalog.class));
    }
}