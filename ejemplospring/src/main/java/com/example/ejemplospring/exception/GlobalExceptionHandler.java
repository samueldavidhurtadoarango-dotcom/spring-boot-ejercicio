package com.example.ejemplospring.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para todos los @RestController.
 * Devuelve respuestas JSON limpias y estructuradas con el código HTTP adecuado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 1. Errores de validación en @RequestBody (@Valid) ─────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<Map<String, String>> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildFieldError)
                .collect(Collectors.toList());

        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación en el cuerpo de la petición", errores);
    }

    // ── 2. Errores de validación en @RequestParam (@Validated) ───────────────
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex) {

        List<Map<String, String>> errores = ex.getConstraintViolations()
                .stream()
                .map(cv -> Map.of(
                        "campo",   cv.getPropertyPath().toString(),
                        "mensaje", cv.getMessage()
                ))
                .collect(Collectors.toList());

        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación en parámetros de la petición", errores);
    }

    // ── 3. Reglas de negocio violadas (IllegalArgumentException) ─────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRuleViolation(
            IllegalArgumentException ex) {
        return buildSimpleResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── 4. Recurso no encontrado (NoSuchElementException) ────────────────────
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return buildSimpleResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 5. Archivo demasiado grande (MultipartFile) ───────────────────────────
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(
            MaxUploadSizeExceededException ex) {
        return buildSimpleResponse(HttpStatus.PAYLOAD_TOO_LARGE,
                "El archivo supera el tamaño máximo permitido (10 MB)");
    }

    // ── 6. Error genérico no controlado ───────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildSimpleResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor: " + ex.getMessage());
    }

    // ── Helpers privados ──────────────────────────────────────────────────────
    private Map<String, String> buildFieldError(FieldError fe) {
        return Map.of(
                "campo",   fe.getField(),
                "mensaje", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido"
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String mensaje, Object detalle) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("mensaje",   mensaje);
        body.put("detalle",   detalle);
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> buildSimpleResponse(
            HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("mensaje",   mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
