package com.keepguard.ms_user.infrastructure.rest;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.lib_common.exception.InvalidXApplicationException;
import com.keepguard.lib_validation.moderation.application.service.ContentViolationException;
import com.keepguard.lib_validation.moderation.domain.model.ModerationResult;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.application.service.exception.CommandOperationException;
import com.keepguard.ms_user.application.service.exception.QueryOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        log.warn("Entidade não encontrada: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExistsException(AlreadyExistsException ex) {
        log.warn("Entidade já existe: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        log.warn("Erro de validação: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ContentViolationException.class)
    public ResponseEntity<Map<String, Object>> handleContentViolationException(ContentViolationException ex) {
        ModerationResult result = ex.getModerationResult();
        
        log.warn("Conteúdo impróprio detectado: flagged={}, categories={}", 
            result.flagged(), result.categories());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());
        
        // Adicionar detalhes da moderação
        Map<String, Object> moderationDetails = new HashMap<>();
        moderationDetails.put("flagged", result.flagged());
        
        // Categorias detectadas
        String categoriesDetected = result.categories().entrySet().stream()
            .filter(entry -> entry.getValue())
            .map(entry -> entry.getKey().getDescription())
            .collect(Collectors.joining(", "));
        
        if (!categoriesDetected.isEmpty()) {
            moderationDetails.put("categories", categoriesDetected);
        }
        
        body.put("moderation", moderationDetails);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.warn("Header obrigatório ausente: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        
        if (ex.getHeaderName().equals("X-Application")) {
            body.put("message", "Header X-Application é obrigatório");
        } else {
            body.put("message", "Header " + ex.getHeaderName() + " é obrigatório");
        }
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidXApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidXApplicationException(InvalidXApplicationException ex) {
        log.warn("Header X-Application inválido: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação de argumentos: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Dados de entrada inválidos");
        body.put("path", getCurrentPath());

        // Adicionar detalhes dos erros de validação
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        body.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Erro de deserialização JSON: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Dados de entrada inválidos");
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException ex) {
        log.warn("Erro de conversão de múltiplos parâmetros: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Parâmetros inválidos");
        body.put("path", getCurrentPath());

        // Coletar todos os erros de conversão
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String rejectedValue = String.valueOf(error.getRejectedValue());
            String errorMessage = error.getDefaultMessage();
            
            // Se for erro de conversão de tipo, criar mensagem mais específica
            String errorCode = error.getCode();
            if (errorCode != null && errorCode.contains("typeMismatch")) {
                String expectedType = "tipo desconhecido";
                Object[] arguments = error.getArguments();
                if (arguments != null && arguments.length > 0) {
                    expectedType = arguments[0].toString();
                }
                fieldErrors.put(fieldName, String.format("Valor '%s' é inválido. Esperado: %s", rejectedValue, expectedType));
            } else {
                fieldErrors.put(fieldName, String.format("Valor '%s' - %s", rejectedValue, errorMessage));
            }
        });

        body.put("errors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(TypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "tipo desconhecido";
        log.warn("Erro de conversão de parâmetro: {} - valor '{}' não pode ser convertido para {}", 
                ex.getPropertyName(), ex.getValue(), requiredType);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        
        String message = String.format("Parâmetro '%s' com valor '%s' é inválido. Esperado: %s", 
                ex.getPropertyName(), ex.getValue(), requiredType);
        body.put("message", message);
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Método HTTP não suportado: {} para {}", ex.getMethod(), getCurrentPath());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        body.put("error", "Method Not Allowed");
        body.put("message", "Método " + ex.getMethod() + " não é suportado para este endpoint");
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(CommandOperationException.class)
    public ResponseEntity<Map<String, Object>> handleCommandOperationException(CommandOperationException ex) {
        log.error("Falha na operação de comando: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());
        body.put("errorCode", ex.getErrorCode());
        body.put("operation", ex.getOperation());
        body.put("context", ex.getContext());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(QueryOperationException.class)
    public ResponseEntity<Map<String, Object>> handleQueryOperationException(QueryOperationException ex) {
        log.error("Falha na operação de consulta: {}", ex.getMessage());

        // Se a causa raiz for NotFoundException, retornar 404
        if (ex.getCause() instanceof NotFoundException) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", OffsetDateTime.now());
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", "Not Found");
            body.put("message", ex.getCause().getMessage());
            body.put("path", getCurrentPath());
            body.put("errorCode", "RESOURCE_NOT_FOUND");
            body.put("operation", ex.getOperation());
            body.put("context", ex.getContext());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        // Para outros tipos de QueryOperationException, retornar 500
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", getCurrentPath());
        body.put("errorCode", ex.getErrorCode());
        body.put("operation", ex.getOperation());
        body.put("context", ex.getContext());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Erro interno do servidor");
        body.put("path", getCurrentPath());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String getCurrentPath() {
        // Implementação simples - em produção, usar RequestContextHolder
        return "/api/users";
    }
}
