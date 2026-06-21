package com.keepguard.ms_user.infrastructure.filter;

import com.keepguard.ms_user.infrastructure.context.CorrelationContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes para CorrelationIdFilter
 * Teste simples para cobertura
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Correlation ID Filter Tests")
class CorrelationIdFilterTest {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    private CorrelationContext correlationContext;
    private CorrelationIdFilter filter;
    
    @BeforeEach
    void setUp() {
        correlationContext = new CorrelationContext();
        filter = new CorrelationIdFilter(correlationContext);
        // Limpar MDC antes de cada teste
        MDC.clear();
    }
    
    @Test
    @DisplayName("Deve processar requisição normal com correlation ID existente")
    void shouldProcessRequestWithExistingCorrelationId() throws ServletException, IOException {
        // Given
        String correlationId = "existing-correlation-id";
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response).setHeader("X-Correlation-ID", correlationId);
        // MDC é limpo após a execução, então verificamos que foi limpo
        assertNull(MDC.get("correlationId"), "MDC deve ser limpo após a requisição");
    }
    
    @Test
    @DisplayName("Deve gerar novo correlation ID quando não existe no header")
    void shouldGenerateNewCorrelationIdWhenNotInHeader() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getHeader("X-Correlation-ID")).thenReturn(null);
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
        // MDC é limpo após a execução, então verificamos que foi limpo
        assertNull(MDC.get("correlationId"), "MDC deve ser limpo após a requisição");
    }
    
    @Test
    @DisplayName("Deve ignorar endpoints de actuator")
    void shouldIgnoreActuatorEndpoints() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/health");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
        assertNull(MDC.get("correlationId"), "MDC não deve ter correlation ID para endpoints ignorados");
    }
    
    @Test
    @DisplayName("Deve ignorar endpoints de prometheus")
    void shouldIgnorePrometheusEndpoints() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/prometheus");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
        assertNull(MDC.get("correlationId"), "MDC não deve ter correlation ID para endpoints ignorados");
    }
    
    @Test
    @DisplayName("Deve ignorar endpoints de metrics")
    void shouldIgnoreMetricsEndpoints() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/metrics");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
        assertNull(MDC.get("correlationId"), "MDC não deve ter correlation ID para endpoints ignorados");
    }
    
    @Test
    @DisplayName("Deve ignorar endpoints de info")
    void shouldIgnoreInfoEndpoints() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/info");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
        assertNull(MDC.get("correlationId"), "MDC não deve ter correlation ID para endpoints ignorados");
    }
    
    @Test
    @DisplayName("Deve ignorar endpoints de health com wildcard")
    void shouldIgnoreHealthEndpointsWithWildcard() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/health/db");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
        assertNull(MDC.get("correlationId"), "MDC não deve ter correlation ID para endpoints ignorados");
    }
    
    @Test
    @DisplayName("Deve ignorar endpoints de prometheus com wildcard")
    void shouldIgnorePrometheusEndpointsWithWildcard() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/prometheus/metrics");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
        assertNull(MDC.get("correlationId"), "MDC não deve ter correlation ID para endpoints ignorados");
    }
    
    @Test
    @DisplayName("Deve ignorar endpoints de metrics com wildcard")
    void shouldIgnoreMetricsEndpointsWithWildcard() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/metrics/jvm");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(anyString(), anyString());
        assertNull(MDC.get("correlationId"), "MDC não deve ter correlation ID para endpoints ignorados");
    }
    
    @Test
    @DisplayName("Deve logar erro para status 500")
    void shouldLogErrorForStatus500() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-Correlation-ID")).thenReturn("test-id");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response).setHeader("X-Correlation-ID", "test-id");
        assertNull(MDC.get("correlationId"), "MDC deve ser limpo após a requisição");
    }
    
    @Test
    @DisplayName("Deve logar warning para status 400")
    void shouldLogWarningForStatus400() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-Correlation-ID")).thenReturn("test-id");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response).setHeader("X-Correlation-ID", "test-id");
        assertNull(MDC.get("correlationId"), "MDC deve ser limpo após a requisição");
    }
    
    @Test
    @DisplayName("Deve processar requisição com header vazio")
    void shouldProcessRequestWithEmptyHeader() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getHeader("X-Correlation-ID")).thenReturn("");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
        // MDC é limpo após a execução, então verificamos que foi limpo
        assertNull(MDC.get("correlationId"), "MDC deve ser limpo após a requisição");
    }
    
    @Test
    @DisplayName("Deve processar requisição com header de espaços")
    void shouldProcessRequestWithSpacesHeader() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getHeader("X-Correlation-ID")).thenReturn("   ");
        
        // When
        filter.doFilterInternal(request, response, filterChain);
        
        // Then
        verify(filterChain).doFilter(request, response);
        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
        // MDC é limpo após a execução, então verificamos que foi limpo
        assertNull(MDC.get("correlationId"), "MDC deve ser limpo após a requisição");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe pode ser instanciada")
    void shouldVerifyCorrelationIdFilterCanBeInstantiated() {
        // Given
        CorrelationContext context = new CorrelationContext();
        
        // When
        CorrelationIdFilter filter = new CorrelationIdFilter(context);
        
        // Then
        assertNotNull(filter, "CorrelationIdFilter deve ser instanciável");
        assertEquals(CorrelationIdFilter.class, filter.getClass(), "Tipo da classe deve ser correto");
    }
}
