package com.Proyecto.Peliculas.exceptions;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Esta clase atrapará excepciones de toda la aplicación
 * y las convertirá en respuestas JSON estandarizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Este método se activa CADA VEZ que la validación de un DTO (@Valid) falla.
     * Atrapa la 'MethodArgumentNotValidException' y la convierte en un 400.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            HttpHeaders headers, 
            HttpStatusCode status, 
            WebRequest request) {
        
        // Creamos un mapa para guardar todos los errores de campo
        Map<String, String> errors = new HashMap<>();
        
        // Extraemos cada error de la excepción
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        // Devolvemos el mapa de errores con un estado 400 Bad Request
        // Esto EVITA que la excepción llegue a Spring Security
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Este método atrapará nuestra excepción personalizada ResourceNotFoundException
     * (la que usamos en los servicios) y la convertirá en un 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            WebRequest request) {
        
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * (Opcional pero recomendado)
     * Atrapa cualquier otra RuntimeException que se nos haya escapado
     * y la convierte en un 500 Internal Server Error.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleGenericRuntimeException(
            RuntimeException ex, 
            WebRequest request) {
        
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        
        // Devuelve 500 para errores genéricos (como "el email ya existe")
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}