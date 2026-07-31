package com.insurance_platform_springboot.exception;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Manejador global de excepciones para la API.
 * Intercepta todas las excepciones lanzadas por los controladores y servicios,
 * transformándolas en respuestas JSON estructuradas (ApiError) con mensajes en español.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones cuando no se encuentra un recurso en la base de datos (Ej.: ID inexistente).
     * @return 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(), 
                "Recurso no encontrado", 
                ex.getMessage(), 
                null, 
                path);
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones por peticiones mal formuladas o lógica de negocio inválida.
     * @return 400 Bad Request.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(), 
                "Petición incorrecta", 
                ex.getMessage(), 
                null, 
                path);
        log.warn("Bad request: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de conflicto de datos (Ej.: Email duplicado, nombres ya usados).
     * @return 409 Conflict.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT.value(), 
                "Conflicto de estado", 
                ex.getMessage(), 
                null, 
                path);
        log.warn("Conflict: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    /**
     * Capturador genérico (Fallback) para cualquier excepción no controlada por los métodos anteriores.
     * @return 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        // Se oculta la traza exacta al usuario final por seguridad, pero se guarda en detalles
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Error interno del servidor", 
                "Ocurrió un error inesperado al procesar la solicitud. Por favor, intente más tarde.", 
                List.of(ex.getMessage()), 
                path);
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Sobreescribe el manejo por defecto de Spring para errores de validación de DTOs (@Valid).
     * Extrae los mensajes de error de cada campo y los devuelve en una lista detallada.
     * @return 400 Bad Request con lista de campos inválidos.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add("El campo '" + error.getField() + "' " + error.getDefaultMessage());
        }
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(), 
                "Error de validación", 
                "Los datos enviados no cumplen con las reglas de validación.", 
                details, 
                path);
        log.warn("Validation failed: {}", details);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Sobreescribe el manejo por defecto para errores de lectura del cue: JSON mal formado).
     * @return 400 Bad Request.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(), 
                "Formato JSON inválido", 
                "La estructura del JSON enviado es incorrecta o contiene tipos de datos no soportados.", 
                List.of(ex.getMostSpecificCause().getMessage()), 
                path);
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja las excepciones de seguridad cuando un usuario autenticado intenta
     * acceder a un recurso para el cual no tiene los roles necesarios.
     * @return 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                "Acceso Denegado",
                "No tienes los permisos necesarios para realizar esta acción o acceder a este recurso.",
                null,
                ((ServletWebRequest) request).getRequest().getRequestURI());
        log.warn("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }
}