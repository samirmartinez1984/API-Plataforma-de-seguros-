package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio para productos del catálogo.
 */
public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Long> {

    /** Buscar productos por tipo. */
    List<ProductCatalog> findByType(ProductType type);

    /** Buscar productos por estado. */
    List<ProductCatalog> findByStatus(ProductStatus status);

    /** Buscar productos con precio base menor al indicado. */
    List<ProductCatalog> findByBasePriceLessThan(BigDecimal price);

    /** Buscar productos cuyo nombre contenga la palabra clave (case-insensitive). */
    List<ProductCatalog> findByNameProductContainingIgnoreCase(String keyword);

    Boolean existsByNameProduct(String nameProduct);
}
