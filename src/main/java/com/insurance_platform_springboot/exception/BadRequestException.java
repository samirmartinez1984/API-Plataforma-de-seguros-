package com.insurance_platform_springboot.exception;

/**
 * Excepción para indicar una mala petición (400).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}

