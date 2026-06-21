package com.keepguard.ms_user.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterCacheService - Testes Unitários")
class RegisterCacheServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RegisterCacheService registerCacheService;

    private RegisterSession session;
    private UUID xApplication;
    private String email;
    private String key;

    @BeforeEach
    void setUp() throws Exception {
        xApplication = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        email = "teste@example.com";
        key = "register_session:teste@example.com:550e8400-e29b-41d4-a716-446655440000";

        session = RegisterSession.create(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                xApplication,
                email,
                "123456",
                "$2a$10$hashed",
                "João Silva",
                "+5511999999999",
                true,
                false,
                null,
                null,
                null,
                UserTypeEnum.PERSON
        );

        ReflectionTestUtils.setField(registerCacheService, "registerSessionTtlSeconds", 1200L);
        ReflectionTestUtils.setField(registerCacheService, "registerCachePrefix", "register_session");
    }

    @Test
    @DisplayName("Deve salvar sessão no Redis com sucesso")
    void deveSalvarSessaoNoRedisComSucesso() throws Exception {
        // Given
        String sessionJson = "{\"registrationSessionId\":\"123e4567-e89b-12d3-a456-426614174000\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(session)).thenReturn(sessionJson);

        // When
        registerCacheService.saveRegisterSession(email, xApplication, session);

        // Then
        verify(objectMapper).writeValueAsString(session);
        verify(valueOperations).set(eq(key), eq(sessionJson), eq(1200L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Deve buscar sessão do Redis com sucesso")
    void deveBuscarSessaoDoRedisComSucesso() throws Exception {
        // Given
        String sessionJson = "{\"registrationSessionId\":\"123e4567-e89b-12d3-a456-426614174000\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(sessionJson);
        when(objectMapper.readValue(sessionJson, RegisterSession.class)).thenReturn(session);

        // When
        Optional<RegisterSession> result = registerCacheService.getRegisterSession(email, xApplication);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(session);
        verify(valueOperations).get(key);
        verify(objectMapper).readValue(sessionJson, RegisterSession.class);
    }

    @Test
    @DisplayName("Deve retornar empty quando sessão não existe no Redis")
    void deveRetornarEmptyQuandoSessaoNaoExisteNoRedis() throws Exception {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        // When
        Optional<RegisterSession> result = registerCacheService.getRegisterSession(email, xApplication);

        // Then
        assertThat(result).isEmpty();
        verify(valueOperations).get(key);
        verify(objectMapper, never()).readValue(anyString(), eq(RegisterSession.class));
    }

    @Test
    @DisplayName("Deve retornar empty quando valor é vazio")
    void deveRetornarEmptyQuandoValorEVazio() throws Exception {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn("");

        // When
        Optional<RegisterSession> result = registerCacheService.getRegisterSession(email, xApplication);

        // Then
        assertThat(result).isEmpty();
        verify(valueOperations).get(key);
        verify(objectMapper, never()).readValue(anyString(), eq(RegisterSession.class));
    }

    @Test
    @DisplayName("Deve remover sessão do Redis com sucesso")
    void deveRemoverSessaoDoRedisComSucesso() {
        // When
        registerCacheService.removeRegisterSession(email, xApplication);

        // Then
        verify(redisTemplate).delete(key);
    }

    @Test
    @DisplayName("Deve verificar existência de sessão no Redis")
    void deveVerificarExistenciaDeSessaoNoRedis() {
        // Given
        when(redisTemplate.hasKey(key)).thenReturn(true);

        // When
        boolean result = registerCacheService.existsRegisterSession(email, xApplication);

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate).hasKey(key);
    }

    @Test
    @DisplayName("Deve retornar false quando sessão não existe")
    void deveRetornarFalseQuandoSessaoNaoExiste() {
        // Given
        when(redisTemplate.hasKey(key)).thenReturn(false);

        // When
        boolean result = registerCacheService.existsRegisterSession(email, xApplication);

        // Then
        assertThat(result).isFalse();
        verify(redisTemplate).hasKey(key);
    }

    @Test
    @DisplayName("Deve normalizar email para minúsculas na chave")
    void deveNormalizarEmailParaMinusculasNaChave() throws Exception {
        // Given
        String emailMaiusculo = "TESTE@EXAMPLE.COM";
        String keyEsperada = "register_session:teste@example.com:550e8400-e29b-41d4-a716-446655440000";
        String sessionJson = "{\"registrationSessionId\":\"123e4567-e89b-12d3-a456-426614174000\"}";
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(keyEsperada)).thenReturn(sessionJson);
        when(objectMapper.readValue(sessionJson, RegisterSession.class)).thenReturn(session);

        // When
        Optional<RegisterSession> result = registerCacheService.getRegisterSession(emailMaiusculo, xApplication);

        // Then
        assertThat(result).isPresent();
        verify(valueOperations).get(keyEsperada);
    }

    @Test
    @DisplayName("Deve propagar exceção ao salvar sessão")
    void devePropagarExcecaoAoSavarSessao() throws Exception {
        // Given
        when(objectMapper.writeValueAsString(session)).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Erro de serialização") {});

        // When & Then
        assertThatThrownBy(() -> registerCacheService.saveRegisterSession(email, xApplication, session))
                .isInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class)
                .hasMessageContaining("Erro de serialização");
        
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Deve propagar exceção ao buscar sessão")
    void devePropagarExcecaoAoBuscarSessao() throws Exception {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn("json_invalido");
        when(objectMapper.readValue(anyString(), eq(RegisterSession.class))).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Erro de deserialização") {});

        // When & Then
        assertThatThrownBy(() -> registerCacheService.getRegisterSession(email, xApplication))
                .isInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class)
                .hasMessageContaining("Erro de deserialização");
    }

    @Test
    @DisplayName("Deve propagar exceção ao remover sessão")
    void devePropagarExcecaoAoRemoverSessao() {
        // Given
        doThrow(new RuntimeException("Erro ao deletar")).when(redisTemplate).delete(key);

        // When & Then
        assertThatThrownBy(() -> registerCacheService.removeRegisterSession(email, xApplication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao deletar");
        
        verify(redisTemplate).delete(key);
    }

    @Test
    @DisplayName("Deve propagar exceção ao verificar existência")
    void devePropagarExcecaoAoVerificarExistencia() {
        // Given
        when(redisTemplate.hasKey(key)).thenThrow(new RuntimeException("Erro ao verificar"));

        // When & Then
        assertThatThrownBy(() -> registerCacheService.existsRegisterSession(email, xApplication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao verificar");
    }
}

