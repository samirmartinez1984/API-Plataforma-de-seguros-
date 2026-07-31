package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para el repositorio del Catálogo de Productos (ProductCatalogRepository).
 * 
 * <p>Asegura que la persistencia física de la oferta comercial de seguros sea consistente 
 * y que las consultas de filtrado por tipo y estado funcionen correctamente en el motor de BD.</p>
 */
public class ProductCatalogRepositoryIntegracionTest extends BaseRepositoryIntegracionTest {

    /** Instancia real del repositorio comercial. */
    @Autowired
    private ProductCatalogRepository productCatalogRepository;

    /**
     * Valida que un producto se guarde físicamente con todos sus atributos comerciales.
     * 
     * <p>Escenario: Inserción de un seguro de auto premium en la base de datos H2.</p>
     * <p>Resultado esperado: Recuperación íntegra de los datos persistidos.</p>
     */
    @Test
    @DisplayName("ProductCatalogRepository: Guardar y Buscar por ID - Éxito")
    void saveAndFindById_DebePersistirProductoCorrectamente() {
        // --- ARRANGE ---
        ProductCatalog product = crearCatalogProduct("seguro de Auto Premium",
                BigDecimal.valueOf(150.00), ProductType.AUTO, ProductStatus.ACTIVE);

        // --- ACT ---
        ProductCatalog guardado = productCatalogRepository.save(product);
        ProductCatalog encontrado = productCatalogRepository.findById(guardado.getId()).orElse(null);

        // --- ASSERT ---
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNameProduct()).isEqualTo("seguro de Auto Premium");
        assertThat(encontrado.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    /**
     * Válida que el filtrado por categoría (Tipo de Producto) sea preciso.
     * 
     * <p>Uso en el sistema: Pantalla de selección de productos por ramo (Vida, Auto, Salud).</p>
     */
    @Test
    @DisplayName("ProductCatalogRepository: Buscar por Tipo - Éxito")
    void findByType_DebeRetornarProductosFiltrados() {
        // --- ARRANGE ---
        productCatalogRepository.save(crearCatalogProduct("seguro Auto Básico",
                BigDecimal.valueOf(50.0), ProductType.AUTO, ProductStatus.ACTIVE));

        productCatalogRepository.save(crearCatalogProduct("seguro Vida Pleno",
                BigDecimal.valueOf(200.0), ProductType.LIFE, ProductStatus.ACTIVE));

        // --- ACT ---
        List<ProductCatalog> autos = productCatalogRepository.findByType(ProductType.AUTO);

        // --- ASSERT ---
        assertThat(autos).hasSize(1);
        assertThat(autos.get(0).getType()).isEqualTo(ProductType.AUTO);
    }

    /**
     * Válida que el filtrado por estado permita distinguir productos activos de descatalogados.
     */
    @Test
    @DisplayName("ProductCatalogRepository: Buscar por Estado - Éxito")
    void findByStatus_DebeRetornarProductosSegunEstado() {
        // --- ARRANGE ---
        productCatalogRepository.save(crearCatalogProduct("seguro Activo",
                BigDecimal.TEN, ProductType.HEALTH, ProductStatus.ACTIVE));

        productCatalogRepository.save(crearCatalogProduct("seguro Inactivo",
                BigDecimal.TEN, ProductType.HEALTH, ProductStatus.INACTIVE));

        // --- ACT ---
        List<ProductCatalog> activos = productCatalogRepository.findByStatus(ProductStatus.ACTIVE);

        // --- ASSERT ---
        assertThat(activos).hasSize(1);
        assertThat(activos.get(0).getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    /**
     * Válida el mecanismo de integridad que impide nombres duplicados en el catálogo comercial.
     */
    @Test
    @DisplayName("ProductCatalogRepository: Verificar existencia por nombre - Éxito")
    void existsByNameProduct_DebeRetornarTrue_CuandoProductoExiste() {
        // --- ARRANGE ---
        String nombreUnico = "seguro Especial contra Todo Riesgo";
        productCatalogRepository.save(crearCatalogProduct(nombreUnico, BigDecimal.valueOf(300.0), ProductType.AUTO, ProductStatus.ACTIVE));

        // --- ACT & ASSERT ---
        assertThat(productCatalogRepository.existsByNameProduct(nombreUnico)).isTrue();
        assertThat(productCatalogRepository.existsByNameProduct("No existe")).isFalse();
    }
}