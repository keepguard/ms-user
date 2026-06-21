package com.keepguard.ms_user.infrastructure.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para CorrelationContext
 * Teste simples para cobertura
 */
@DisplayName("Correlation Context Tests")
class CorrelationContextTest {
    
    private CorrelationContext correlationContext;
    
    @BeforeEach
    void setUp() {
        correlationContext = new CorrelationContext();
        // Limpar MDC antes de cada teste
        MDC.clear();
    }
    
    @AfterEach
    void tearDown() {
        // Limpar MDC após cada teste
        MDC.clear();
    }
    
    @Test
    @DisplayName("Deve verificar constantes da classe")
    void shouldVerifyConstants() {
        // Given & When & Then
        assertEquals("X-Correlation-ID", CorrelationContext.CORRELATION_ID_HEADER, 
            "Header deve ser correto");
        assertEquals("correlationId", CorrelationContext.CORRELATION_ID_MDC_KEY, 
            "MDC key deve ser correta");
    }
    
    @Test
    @DisplayName("Deve gerar correlation ID quando não existe no MDC")
    void shouldGenerateCorrelationIdWhenNotInMDC() {
        // Given
        // MDC está limpo
        
        // When
        String correlationId = correlationContext.getCorrelationId();
        
        // Then
        assertNotNull(correlationId, "Correlation ID deve ser gerado");
        assertTrue(correlationId.startsWith("ms-user-"), "Correlation ID deve ter prefixo correto");
        assertTrue(correlationId.length() > 20, "Correlation ID deve ter tamanho adequado");
        
        // Verificar se foi adicionado ao MDC
        assertEquals(correlationId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY), 
            "Correlation ID deve estar no MDC");
    }
    
    @Test
    @DisplayName("Deve retornar correlation ID existente no MDC")
    void shouldReturnExistingCorrelationIdFromMDC() {
        // Given
        String existingId = "existing-correlation-id";
        MDC.put(CorrelationContext.CORRELATION_ID_MDC_KEY, existingId);
        
        // When
        String correlationId = correlationContext.getCorrelationId();
        
        // Then
        assertEquals(existingId, correlationId, "Deve retornar o ID existente");
    }
    
    @Test
    @DisplayName("Deve definir correlation ID válido")
    void shouldSetValidCorrelationId() {
        // Given
        String newId = "new-correlation-id";
        
        // When
        correlationContext.setCorrelationId(newId);
        
        // Then
        assertEquals(newId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY), 
            "Correlation ID deve ser definido no MDC");
    }
    
    @Test
    @DisplayName("Deve gerar novo ID quando correlation ID é null")
    void shouldGenerateNewIdWhenCorrelationIdIsNull() {
        // Given
        // MDC está limpo
        
        // When
        correlationContext.setCorrelationId(null);
        
        // Then
        String correlationId = MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY);
        assertNotNull(correlationId, "Correlation ID deve ser gerado");
        assertTrue(correlationId.startsWith("ms-user-"), "Correlation ID deve ter prefixo correto");
    }
    
    @Test
    @DisplayName("Deve gerar novo ID quando correlation ID é vazio")
    void shouldGenerateNewIdWhenCorrelationIdIsEmpty() {
        // Given
        // MDC está limpo
        
        // When
        correlationContext.setCorrelationId("");
        
        // Then
        String correlationId = MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY);
        assertNotNull(correlationId, "Correlation ID deve ser gerado");
        assertTrue(correlationId.startsWith("ms-user-"), "Correlation ID deve ter prefixo correto");
    }
    
    @Test
    @DisplayName("Deve gerar novo ID quando correlation ID é apenas espaços")
    void shouldGenerateNewIdWhenCorrelationIdIsOnlySpaces() {
        // Given
        // MDC está limpo
        
        // When
        correlationContext.setCorrelationId("   ");
        
        // Then
        String correlationId = MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY);
        assertNotNull(correlationId, "Correlation ID deve ser gerado");
        assertTrue(correlationId.startsWith("ms-user-"), "Correlation ID deve ter prefixo correto");
    }
    
    @Test
    @DisplayName("Deve limpar correlation ID do MDC")
    void shouldClearCorrelationIdFromMDC() {
        // Given
        String testId = "test-correlation-id";
        MDC.put(CorrelationContext.CORRELATION_ID_MDC_KEY, testId);
        assertEquals(testId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY), 
            "Correlation ID deve estar no MDC");
        
        // When
        correlationContext.clearCorrelationId();
        
        // Then
        assertNull(MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY), 
            "Correlation ID deve ser removido do MDC");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe pode ser instanciada")
    void shouldVerifyCorrelationContextCanBeInstantiated() {
        // Given & When
        CorrelationContext context = new CorrelationContext();
        
        // Then
        assertNotNull(context, "CorrelationContext deve ser instanciável");
        assertEquals(CorrelationContext.class, context.getClass(), "Tipo da classe deve ser correto");
    }
}
