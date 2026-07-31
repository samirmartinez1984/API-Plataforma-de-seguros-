package com.insurance_platform_springboot.service;

import com.insurance_platform_springboot.dtos.request.ProductCatalogRequestDTO;
import com.insurance_platform_springboot.dtos.response.ProductCatalogResponseDTO;
import com.insurance_platform_springboot.dtos.update.ProductCatalogUpdateDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.ProductCatalogMapper;
import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.repository.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for managing the insurance product catalog.
 * Provides functionality for creating, retrieving, updating, and deleting products.
 */
@Service
@RequiredArgsConstructor
public class ProductCatalogService {

    private final ProductCatalogRepository productCatalogRepository;
    private final ProductCatalogMapper productCatalogMapper;

    /**
     * Creates a new product in the catalog.
     * Validates that the product name is not duplicated.
     *
     * @param request Data of the product to be created.
     * @return ProductCatalogResponseDTO with the created product information.
     * @throws ConflictException If a product with the same name already exists.
     */
    @Transactional
    public ProductCatalogResponseDTO create(ProductCatalogRequestDTO request) {
        if (productCatalogRepository.existsByNameProduct(request.getNameProduct())){
            throw new ConflictException("Product already exists with name: " + request.getNameProduct());
        }

        ProductCatalog product = productCatalogMapper.toEntity(request);
        product.setCreatedAt(LocalDateTime.now());
        
        ProductCatalog saved = productCatalogRepository.save(product);
        return productCatalogMapper.toResponse(saved);
    }

    /**
     * Retrieves all products registered in the catalog.
     *
     * @return List of ProductCatalogResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<ProductCatalogResponseDTO> findAll(){
        return productCatalogRepository.findAll().stream()
                .map(productCatalogMapper::toResponse)
                .toList();
    }

    /**
     * Finds a specific product by its unique identifier.
     *
     * @param id The ID of the product to find.
     * @return ProductCatalogResponseDTO with the product data.
     * @throws ResourceNotFoundException If the product does not exist.
     */
    @Transactional(readOnly = true)
    public ProductCatalogResponseDTO findById(Long id){
        ProductCatalog product = productCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: " + id));
        return productCatalogMapper.toResponse(product);
    }

    /**
     * Updates an existing product's information.
     * Validates name uniqueness if it is modified.
     *
     * @param id The ID of the product to update.
     * @param request Updated product data.
     * @return ProductCatalogResponseDTO with the modified data.
     * @throws ResourceNotFoundException If the product does not exist.
     * @throws ConflictException If the new name is already in use by another product.
     */
    @Transactional
    public ProductCatalogResponseDTO update(Long id, ProductCatalogUpdateDTO request){
        ProductCatalog product = productCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: " + id));

        if (request.getNameProduct() != null &&
                !product.getNameProduct().equals(request.getNameProduct()) &&
                productCatalogRepository.existsByNameProduct(request.getNameProduct())){
            throw new ConflictException("Product name '" + request.getNameProduct() + "' is already in use");
        }
        
        productCatalogMapper.updateEntity(request, product);
        ProductCatalog saved = productCatalogRepository.save(product);
        return productCatalogMapper.toResponse(saved);
    }

    /**
     * Deletes a product from the catalog by its ID.
     *
     * @param id The ID of the product to delete.
     * @throws ResourceNotFoundException If the product does not exist.
     */
    @Transactional
    public void delete(Long id){
        if (!productCatalogRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Product not found with ID: " + id);
        }
        productCatalogRepository.deleteById(id);
    }
}
