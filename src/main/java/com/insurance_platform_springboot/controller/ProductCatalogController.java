package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.ProductCatalogRequestDTO;
import com.insurance_platform_springboot.dtos.response.ProductCatalogResponseDTO;
import com.insurance_platform_springboot.dtos.update.ProductCatalogUpdateDTO;
import com.insurance_platform_springboot.service.ProductCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de productos de seguros.
 * Permite a los administradores gestionar la oferta comercial y a los usuarios consultar los servicios disponibles.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Catálogo de Productos", description = "Endpoints para la gestión de productos y servicios de seguros")
public class ProductCatalogController {
    
    private final ProductCatalogService productCatalogService;

    /**
     * Registra un nuevo producto en el catálogo del sistema.
     * Solo accesible por usuarios con rol de Administrador.
     *
     * @param requestDTO Objeto con la información del nuevo producto (nombre, precio, tipo, etc).
     * @return ResponseEntity con el ProductCatalogResponseDTO del producto creado y estado 201.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo producto en el catálogo (Solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCatalogResponseDTO> createProduct(
            @Valid @RequestBody ProductCatalogRequestDTO requestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(productCatalogService.create(requestDTO));
    }

    /**
     * Recupera la lista completa de todos los productos y servicios registrados.
     * Accesible por cualquier usuario autenticado en el sistema.
     *
     * @return ResponseEntity con la lista de ProductCatalogResponseDTO.
     */
    @GetMapping
    @Operation(summary = "Listar todos los productos del catálogo")
    public ResponseEntity<List<ProductCatalogResponseDTO>> listarProducts(){
        return ResponseEntity.ok(productCatalogService.findAll());
    }

    /**
     * Realiza la búsqueda de un producto específico mediante su identificador único.
     *
     * @param id Identificador numérico del producto a buscar.
     * @return ResponseEntity con el ProductCatalogResponseDTO encontrado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar un producto por ID")
    public ResponseEntity<ProductCatalogResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(productCatalogService.findById(id));
    }

    /**
     * Modifica los datos de un producto existente en el catálogo.
     * Solo accesible por usuarios con rol de Administrador.
     *
     * @param id Identificador del producto que se desea actualizar.
     * @param updateDTO Objeto con los nuevos datos para el producto.
     * @return ResponseEntity con el ProductCatalogResponseDTO actualizado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente (Solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCatalogResponseDTO> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductCatalogUpdateDTO updateDTO){
        return ResponseEntity.ok(productCatalogService.update(id, updateDTO));
    }

    /**
     * Elimina de forma permanente un producto del catálogo de la plataforma.
     * Solo accesible por usuarios con rol de Administrador.
     *
     * @param id Identificador del producto a eliminar.
     * @return ResponseEntity con estado 204 No Content si la operación es exitosa.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto del catálogo (Solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productCatalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}