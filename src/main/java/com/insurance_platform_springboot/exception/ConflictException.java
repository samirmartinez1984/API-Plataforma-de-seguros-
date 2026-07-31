package com.insurance_platform_springboot.exception;

/**
 * Excepción para indicar conflicto (409).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

}

