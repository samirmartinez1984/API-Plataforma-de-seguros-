package com.insurance_platform_springboot.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DTO para respuestas de error estandarizadas en la API.
 */
public class ApiError {

    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;
    private final String path;
    private final String timestamp;

    public ApiError(int status, String error, String message, List<String> details, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.path = path;
        // Corregido: ISO_LOCAL_DATE_TIME es compatible con LocalDateTime
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
    public String getPath() { return path; }
    public String getTimestamp() { return timestamp; }
}