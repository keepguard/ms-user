package com.keepguard.ms_user.infrastructure.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para JacksonConfig
 * Teste simples para cobertura
 */
@DisplayName("Jackson Config Tests")
class JacksonConfigTest {
    
    @Test
    @DisplayName("Deve verificar se a classe JacksonConfig existe")
    void shouldVerifyJacksonConfigClassExists() {
        // Given
        Class<?> configClass = JacksonConfig.class;
        
        // When & Then
        assertNotNull(configClass, "Classe JacksonConfig deve existir");
        assertEquals("JacksonConfig", configClass.getSimpleName(), "Nome da classe deve ser correto");
        assertEquals("com.keepguard.ms_user.infrastructure.config", configClass.getPackageName(), "Package deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar anotações da classe JacksonConfig")
    void shouldVerifyJacksonConfigAnnotations() {
        // Given
        Class<?> configClass = JacksonConfig.class;
        
        // When & Then
        assertTrue(configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class), 
            "Classe deve ter anotação @Configuration");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe pode ser instanciada")
    void shouldVerifyJacksonConfigCanBeInstantiated() {
        // Given & When
        JacksonConfig config = new JacksonConfig();
        
        // Then
        assertNotNull(config, "JacksonConfig deve ser instanciável");
        assertEquals(JacksonConfig.class, config.getClass(), "Tipo da classe deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar se o ObjectMapper é configurado corretamente")
    void shouldVerifyObjectMapperConfiguration() {
        // Given
        JacksonConfig config = new JacksonConfig();
        ReflectionTestUtils.setField(config, "applicationTimeZone", "America/Sao_Paulo");
        
        // When
        ObjectMapper objectMapper = config.objectMapper();
        
        // Then
        assertNotNull(objectMapper, "ObjectMapper deve ser criado");
        assertTrue(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING), 
            "READ_ENUMS_USING_TO_STRING deve estar habilitado");
        assertTrue(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) == false, 
            "FAIL_ON_UNKNOWN_PROPERTIES deve estar desabilitado");
        // Verificar se o ObjectMapper pode ser usado para serialização/deserialização
        assertNotNull(objectMapper.getSerializationConfig(), "Configuração de serialização deve existir");
        assertNotNull(objectMapper.getDeserializationConfig(), "Configuração de deserialização deve existir");
    }
    
    @Test
    @DisplayName("Deve verificar se o ObjectMapper é um bean")
    void shouldVerifyObjectMapperIsBean() throws NoSuchMethodException {
        // Given
        Class<?> configClass = JacksonConfig.class;
        
        // When
        var method = configClass.getMethod("objectMapper");
        var beanAnnotation = method.getAnnotation(org.springframework.context.annotation.Bean.class);
        
        // Then
        assertNotNull(beanAnnotation, "Método objectMapper deve ter anotação @Bean");
    }
}
