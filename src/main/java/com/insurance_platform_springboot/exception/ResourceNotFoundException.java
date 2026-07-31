package com.insurance_platform_springboot.exception;

/**
 * Excepción lanzada cuando un recurso no es encontrado.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

