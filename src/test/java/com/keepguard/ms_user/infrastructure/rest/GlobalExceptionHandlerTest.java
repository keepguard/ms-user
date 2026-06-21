package com.keepguard.ms_user.infrastructure.rest;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para GlobalExceptionHandler
 * Testa tratamento de diferentes tipos de exceções
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {
    
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    
    @BeforeEach
    void setUp() {
        // Setup inicial se necessário
    }
    
    // === TESTES DE NOT FOUND EXCEPTION ===
    
    @Test
    @DisplayName("Deve tratar NotFoundException com sucesso")
    void shouldHandleNotFoundExceptionSuccessfully() {
        // Given
        String message = "Usuário não encontrado: 123e4567-e89b-12d3-a456-426614174000";
        NotFoundException exception = new NotFoundException(message);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleNotFoundException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(404);
        assertThat(result.getBody().get("error")).isEqualTo("Not Found");
        assertThat(result.getBody().get("message")).isEqualTo(message);
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
        assertThat(result.getBody().get("timestamp")).isNotNull();
    }
    
    @Test
    @DisplayName("Deve tratar NotFoundException com mensagem vazia")
    void shouldHandleNotFoundExceptionWithEmptyMessage() {
        // Given
        NotFoundException exception = new NotFoundException("");
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleNotFoundException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(404);
        assertThat(result.getBody().get("error")).isEqualTo("Not Found");
        assertThat(result.getBody().get("message")).isEqualTo("");
    }
    
    // === TESTES DE ALREADY EXISTS EXCEPTION ===
    
    @Test
    @DisplayName("Deve tratar AlreadyExistsException com sucesso")
    void shouldHandleAlreadyExistsExceptionSuccessfully() {
        // Given
        String message = "Email já está em uso: test@example.com";
        AlreadyExistsException exception = new AlreadyExistsException(message);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleAlreadyExistsException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(409);
        assertThat(result.getBody().get("error")).isEqualTo("Conflict");
        assertThat(result.getBody().get("message")).isEqualTo(message);
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
        assertThat(result.getBody().get("timestamp")).isNotNull();
    }
    
    @Test
    @DisplayName("Deve tratar AlreadyExistsException com mensagem nula")
    void shouldHandleAlreadyExistsExceptionWithNullMessage() {
        // Given
        AlreadyExistsException exception = new AlreadyExistsException(null);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleAlreadyExistsException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(409);
        assertThat(result.getBody().get("error")).isEqualTo("Conflict");
        assertThat(result.getBody().get("message")).isNull();
    }
    
    // === TESTES DE VALIDATION EXCEPTION ===
    
    @Test
    @DisplayName("Deve tratar ValidationException com sucesso")
    void shouldHandleValidationExceptionSuccessfully() {
        // Given
        String message = "Dados de entrada inválidos";
        ValidationException exception = new ValidationException(message);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleValidationException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(400);
        assertThat(result.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(result.getBody().get("message")).isEqualTo(message);
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
        assertThat(result.getBody().get("timestamp")).isNotNull();
    }
    
    @Test
    @DisplayName("Deve tratar ValidationException com mensagem longa")
    void shouldHandleValidationExceptionWithLongMessage() {
        // Given
        String message = "Este é um erro de validação muito longo que contém muitos detalhes sobre o que deu errado na validação dos dados de entrada fornecidos pelo usuário";
        ValidationException exception = new ValidationException(message);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleValidationException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("message")).isEqualTo(message);
    }
    
    // === TESTES DE METHOD ARGUMENT NOT VALID EXCEPTION ===
    
    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException com sucesso")
    void shouldHandleMethodArgumentNotValidExceptionSuccessfully() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("user", "email", "Email é obrigatório");
        FieldError fieldError2 = new FieldError("user", "phone", "Telefone é obrigatório");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(exception.getMessage()).thenReturn("Validation failed");
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleValidationException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(400);
        assertThat(result.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(result.getBody().get("message")).isEqualTo("Dados de entrada inválidos");
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
        assertThat(result.getBody().get("timestamp")).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.getBody().get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors.get("email")).isEqualTo("Email é obrigatório");
        assertThat(errors.get("phone")).isEqualTo("Telefone é obrigatório");
    }
    
    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException sem erros de campo")
    void shouldHandleMethodArgumentNotValidExceptionWithoutFieldErrors() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(exception.getMessage()).thenReturn("Validation failed");
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleValidationException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(400);
        assertThat(result.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(result.getBody().get("message")).isEqualTo("Dados de entrada inválidos");
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.getBody().get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors).isEmpty();
    }
    
    // === TESTES DE ILLEGAL ARGUMENT EXCEPTION ===
    
    @Test
    @DisplayName("Deve tratar IllegalArgumentException com sucesso")
    void shouldHandleIllegalArgumentExceptionSuccessfully() {
        // Given
        String message = "Argumento inválido fornecido";
        IllegalArgumentException exception = new IllegalArgumentException(message);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleIllegalArgumentException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(400);
        assertThat(result.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(result.getBody().get("message")).isEqualTo(message);
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
        assertThat(result.getBody().get("timestamp")).isNotNull();
    }
    
    @Test
    @DisplayName("Deve tratar IllegalArgumentException com mensagem nula")
    void shouldHandleIllegalArgumentExceptionWithNullMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException((String) null);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleIllegalArgumentException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(400);
        assertThat(result.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(result.getBody().get("message")).isNull();
    }
    
    // === TESTES DE HTTP MESSAGE NOT READABLE EXCEPTION ===
    
    @Test
    @DisplayName("Deve tratar HttpMessageNotReadableException com sucesso")
    void shouldHandleHttpMessageNotReadableExceptionSuccessfully() {
        // Given
        String message = "JSON parse error";
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(message);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadableException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(400);
        assertThat(result.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(result.getBody().get("message")).isEqualTo("Dados de entrada inválidos");
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
        assertThat(result.getBody().get("timestamp")).isNotNull();
    }
    
    @Test
    @DisplayName("Deve tratar HttpMessageNotReadableException com mensagem nula")
    void shouldHandleHttpMessageNotReadableExceptionWithNullMessage() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(null);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadableException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(400);
        assertThat(result.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(result.getBody().get("message")).isEqualTo("Dados de entrada inválidos");
    }
    
    // === TESTES DE EXCEÇÃO GENÉRICA ===
    
    @Test
    @DisplayName("Deve tratar Exception genérica com sucesso")
    void shouldHandleGenericExceptionSuccessfully() {
        // Given
        String message = "Erro interno do servidor";
        Exception exception = new RuntimeException(message);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleGenericException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(500);
        assertThat(result.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(result.getBody().get("message")).isEqualTo("Erro interno do servidor");
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
        assertThat(result.getBody().get("timestamp")).isNotNull();
    }
    
    @Test
    @DisplayName("Deve tratar Exception genérica com mensagem nula")
    void shouldHandleGenericExceptionWithNullMessage() {
        // Given
        Exception exception = new RuntimeException((String) null);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleGenericException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(500);
        assertThat(result.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(result.getBody().get("message")).isEqualTo("Erro interno do servidor");
    }
    
    @Test
    @DisplayName("Deve tratar Exception genérica com causa")
    void shouldHandleGenericExceptionWithCause() {
        // Given
        Exception cause = new IllegalArgumentException("Causa raiz");
        Exception exception = new RuntimeException("Erro wrapper", cause);
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleGenericException(exception);
        
        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("status")).isEqualTo(500);
        assertThat(result.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(result.getBody().get("message")).isEqualTo("Erro interno do servidor");
    }
    
    // === TESTES DE ESTRUTURA DE RESPOSTA ===
    
    @Test
    @DisplayName("Deve incluir timestamp na resposta")
    void shouldIncludeTimestampInResponse() {
        // Given
        NotFoundException exception = new NotFoundException("Test message");
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleNotFoundException(exception);
        
        // Then
        assertThat(result.getBody()).isNotNull();
        Object timestamp = result.getBody().get("timestamp");
        assertThat(timestamp).isNotNull();
        assertThat(timestamp).isInstanceOf(OffsetDateTime.class);
    }
    
    @Test
    @DisplayName("Deve incluir path na resposta")
    void shouldIncludePathInResponse() {
        // Given
        NotFoundException exception = new NotFoundException("Test message");
        
        // When
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleNotFoundException(exception);
        
        // Then
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get("path")).isEqualTo("/api/users");
    }
    
    @Test
    @DisplayName("Deve incluir status code correto na resposta")
    void shouldIncludeCorrectStatusCodeInResponse() {
        // Given
        NotFoundException notFoundException = new NotFoundException("Not found");
        AlreadyExistsException alreadyExistsException = new AlreadyExistsException("Already exists");
        ValidationException validationException = new ValidationException("Validation error");
        
        // When
        ResponseEntity<Map<String, Object>> notFoundResult = globalExceptionHandler.handleNotFoundException(notFoundException);
        ResponseEntity<Map<String, Object>> conflictResult = globalExceptionHandler.handleAlreadyExistsException(alreadyExistsException);
        ResponseEntity<Map<String, Object>> badRequestResult = globalExceptionHandler.handleValidationException(validationException);
        
        // Then
        assertThat(notFoundResult.getBody().get("status")).isEqualTo(404);
        assertThat(conflictResult.getBody().get("status")).isEqualTo(409);
        assertThat(badRequestResult.getBody().get("status")).isEqualTo(400);
    }
    
    @Test
    @DisplayName("Deve incluir error type correto na resposta")
    void shouldIncludeCorrectErrorTypeInResponse() {
        // Given
        NotFoundException notFoundException = new NotFoundException("Not found");
        AlreadyExistsException alreadyExistsException = new AlreadyExistsException("Already exists");
        ValidationException validationException = new ValidationException("Validation error");
        
        // When
        ResponseEntity<Map<String, Object>> notFoundResult = globalExceptionHandler.handleNotFoundException(notFoundException);
        ResponseEntity<Map<String, Object>> conflictResult = globalExceptionHandler.handleAlreadyExistsException(alreadyExistsException);
        ResponseEntity<Map<String, Object>> badRequestResult = globalExceptionHandler.handleValidationException(validationException);
        
        // Then
        assertThat(notFoundResult.getBody().get("error")).isEqualTo("Not Found");
        assertThat(conflictResult.getBody().get("error")).isEqualTo("Conflict");
        assertThat(badRequestResult.getBody().get("error")).isEqualTo("Bad Request");
    }
}
