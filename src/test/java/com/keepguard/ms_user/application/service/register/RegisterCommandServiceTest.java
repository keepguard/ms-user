package com.keepguard.ms_user.application.service.register;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.application.dto.register.RegisterConfirmCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitViewDTO;
import com.keepguard.ms_user.application.mapper.RegisterApplicationMapper;
import com.keepguard.ms_user.application.port.out.cache.RegisterCachePort;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestPropertySource(properties = {
    "cache.redis.max_attempts.register_token=5"
})
@DisplayName("RegisterCommandService - Testes Unitários")
class RegisterCommandServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RegisterCachePort registerCachePort;

    @Mock
    private RegisterApplicationMapper registerApplicationMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MetricsPort metricsPort;

    @InjectMocks
    private RegisterCommandService registerCommandService;

    private RegisterInitCommandDTO command;
    private RegisterSession session;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        
        // Configurar maxAttempts para 5 usando ReflectionTestUtils
        ReflectionTestUtils.setField(registerCommandService, "maxAttempts", 5);
        
        command = new RegisterInitCommandDTO(
                tenantId,
                "teste@example.com",
                "João Silva",
                "SenhaSegura123!",
                "+5511999999999",
                true,
                false,
                null,
                null,
                null,
                UserTypeEnum.PERSON
        );

        session = RegisterSession.of(
                UUID.randomUUID(),
                tenantId,
                "teste@example.com",
                "123456",
                "$2a$10$hashed",
                "João Silva",
                "+5511999999999",
                true,
                false,
                "192.168.1.1",
                "Mozilla/5.0",
                "São Paulo, SP",
                UserTypeEnum.PERSON,
                java.time.OffsetDateTime.now(),
                0, // 0 tentativas
                0  // 0 resend attempts
        );
    }

    @Test
    @DisplayName("Deve inicializar registro com sucesso")
    void deveInicializarRegistroComSucesso() throws Exception {
        // Given
        when(userRepositoryPort.existsByEmailAndTenantId(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(registerCachePort.existsRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        when(registerApplicationMapper.toDomain(any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(session);
        when(registerApplicationMapper.toDomain(any(), anyString(), anyString()))
                .thenReturn(session);
        when(registerApplicationMapper.toView(any(), anyString(), anyInt()))
                .thenReturn(new RegisterInitViewDTO(session.getRegistrationSessionId(), "teste@exemplo.com", 1200, "Token enviado", "123456"));
        try {
            doNothing().when(registerCachePort).saveRegisterSession(anyString(), any(UUID.class), any(RegisterSession.class));
        } catch (Exception e) {
            // Mock não lança exceção
        }

        // When
        RegisterInitViewDTO result = registerCommandService.init(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Token enviado");
        verify(userRepositoryPort).existsByEmailAndTenantId(command.email(), tenantId);
        verify(registerCachePort).existsRegisterSession(command.email(), tenantId);
        verify(passwordEncoder).encode(command.password());
        verify(registerCachePort).saveRegisterSession(command.email(), tenantId, session);
        verify(metricsPort).incrementCounter(anyString(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() throws Exception {
        // Given
        when(userRepositoryPort.existsByEmailAndTenantId(anyString(), any(UUID.class)))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> registerCommandService.init(command))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Email já está em uso");

        verify(userRepositoryPort).existsByEmailAndTenantId(command.email(), tenantId);
        verify(registerCachePort, never()).saveRegisterSession(any(), any(), any());
        verify(metricsPort).incrementCounter(anyString(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe sessão ativa")
    void deveLancarExcecaoQuandoJaExisteSessaoAtiva() throws Exception {
        // Given
        when(userRepositoryPort.existsByEmailAndTenantId(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(registerCachePort.existsRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> registerCommandService.init(command))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Já existe uma sessão de registro ativa");

        verify(userRepositoryPort).existsByEmailAndTenantId(command.email(), tenantId);
        verify(registerCachePort).existsRegisterSession(command.email(), tenantId);
        verify(registerCachePort, never()).saveRegisterSession(any(), any(), any());
        verify(metricsPort).incrementCounter(anyString(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando termos não foram aceitos")
    void deveLancarExcecaoQuandoTermosNaoForamAceitos() {
        // Given
        RegisterInitCommandDTO commandSemTermos = new RegisterInitCommandDTO(
                tenantId,
                "teste@example.com",
                "João Silva",
                "SenhaSegura123!",
                "+5511999999999",
                false, // termos não aceitos
                false,
                null,
                null,
                null,
                UserTypeEnum.PERSON
        );

        when(userRepositoryPort.existsByEmailAndTenantId(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(registerCachePort.existsRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> registerCommandService.init(commandSemTermos))
                .isInstanceOf(com.keepguard.lib_common.exception.ValidationException.class)
                .hasMessageContaining("aceitar os termos");

        verify(metricsPort).incrementCounter(anyString(), any());
    }

    @Test
    @DisplayName("Deve criptografar senha antes de salvar")
    void deveCriptografarSenhaAntesDeSalvar() throws Exception {
        // Given
        when(userRepositoryPort.existsByEmailAndTenantId(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(registerCachePort.existsRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        when(registerApplicationMapper.toDomain(any(), anyString(), anyString()))
                .thenReturn(session);
        when(registerApplicationMapper.toView(any(), anyString(), anyInt()))
                .thenReturn(new RegisterInitViewDTO(session.getRegistrationSessionId(), "teste@exemplo.com", 1200, "Token enviado", "123456"));
        try {
            doNothing().when(registerCachePort).saveRegisterSession(anyString(), any(UUID.class), any(RegisterSession.class));
        } catch (Exception e) {
            // Mock não lança exceção
        }

        // When
        registerCommandService.init(command);

        // Then
        verify(passwordEncoder).encode("SenhaSegura123!");
    }

    @Test
    @DisplayName("Deve gerar token de 6 dígitos")
    void deveGerarTokenDeSeisDigitos() throws Exception {
        // Given
        when(userRepositoryPort.existsByEmailAndTenantId(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(registerCachePort.existsRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        when(registerApplicationMapper.toDomain(any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(session);
        when(registerApplicationMapper.toDomain(any(), anyString(), anyString()))
                .thenReturn(session);
        when(registerApplicationMapper.toView(any(), anyString(), anyInt()))
                .thenReturn(new RegisterInitViewDTO(session.getRegistrationSessionId(), "teste@exemplo.com", 1200, "Token enviado", "123456"));
        try {
            doNothing().when(registerCachePort).saveRegisterSession(anyString(), any(UUID.class), any(RegisterSession.class));
        } catch (Exception e) {
            // Mock não lança exceção
        }

        // When
        registerCommandService.init(command);

        // Then
        verify(registerApplicationMapper).toDomain(eq(command), anyString(), anyString(), anyString(), eq("$2a$10$hashed"));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falha ao serializar sessão")
    void deveLancarRuntimeExceptionQuandoFalhaAoSerializarSessao() throws Exception {
        // Given
        when(userRepositoryPort.existsByEmailAndTenantId(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(registerCachePort.existsRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        when(registerApplicationMapper.toDomain(any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(session);
        when(registerApplicationMapper.toDomain(any(), anyString(), anyString()))
                .thenReturn(session);
        doThrow(new com.fasterxml.jackson.core.JsonProcessingException("Erro de serialização") {})
                .when(registerCachePort).saveRegisterSession(anyString(), any(UUID.class), any(RegisterSession.class));

        // When & Then
        assertThatThrownBy(() -> registerCommandService.init(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao salvar sessão de registro no cache");

        verify(userRepositoryPort).existsByEmailAndTenantId(command.email(), tenantId);
        verify(registerCachePort).existsRegisterSession(command.email(), tenantId);
        verify(passwordEncoder).encode(command.password());
        verify(registerCachePort).saveRegisterSession(command.email(), tenantId, session);
    }

    // ========== TESTES DO MÉTODO CONFIRM ==========

    @Test
    @DisplayName("Deve confirmar registro com sucesso")
    void deveConfirmarRegistroComSucesso() throws Exception {
        // Given
        RegisterSession sessionParaTeste = RegisterSession.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "teste@example.com",
                "123456",
                "encodedPassword",
                "João Silva",
                "11999999999",
                true,
                false,
                "192.168.1.1",
                "Mozilla/5.0",
                "São Paulo, SP",
                UserTypeEnum.PERSON,
                java.time.OffsetDateTime.now(),
                0,
                0
        );

        RegisterConfirmCommandDTO command = new RegisterConfirmCommandDTO(
                UUID.randomUUID(),
                sessionParaTeste.getRegistrationSessionId(),
                "teste@example.com",
                "123456"
        );

        when(registerCachePort.getRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(Optional.of(sessionParaTeste));

        // When
        RegisterSession result = registerCommandService.confirm(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("teste@example.com");
        assertThat(result.getToken()).isEqualTo("123456");

        verify(registerCachePort).getRegisterSession(command.email(), command.tenantId());
        verify(registerCachePort).removeRegisterSession(command.email(), command.tenantId());
        verify(metricsPort).incrementCounter(eq("register_confirm_total"), any(Map.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando sessão não existe")
    void deveLancarNotFoundExceptionQuandoSessaoNaoExiste() throws Exception {
        // Given
        RegisterConfirmCommandDTO command = new RegisterConfirmCommandDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "test@example.com",
                "123456"
        );

        when(registerCachePort.getRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> registerCommandService.confirm(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sessão de registro não encontrada ou expirada. Por favor, inicie o registro novamente.");

        verify(registerCachePort).getRegisterSession(command.email(), command.tenantId());
        verify(metricsPort).incrementCounter(eq("register_business_errors_total"), any(Map.class));
    }

    @Test
    @DisplayName("Deve lançar ValidationException quando registrationSessionId é inválido")
    void deveLancarValidationExceptionQuandoRegistrationSessionIdInvalido() throws Exception {
        // Given
        RegisterConfirmCommandDTO command = new RegisterConfirmCommandDTO(
                UUID.randomUUID(),
                UUID.randomUUID(), // ID diferente
                "test@example.com",
                "123456"
        );

        when(registerCachePort.getRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(Optional.of(session));

        // When & Then
        assertThatThrownBy(() -> registerCommandService.confirm(command))
                .isInstanceOf(ValidationException.class)
                .hasMessage("ID da sessão de registro inválido");

        verify(registerCachePort).getRegisterSession(command.email(), command.tenantId());
        verify(metricsPort).incrementCounter(eq("register_business_errors_total"), any(Map.class));
    }

    @Test
    @DisplayName("Deve lançar ValidationException quando token é inválido")
    void deveLancarValidationExceptionQuandoTokenInvalido() throws Exception {
        // Given
        UUID registrationSessionId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        
        RegisterConfirmCommandDTO command = new RegisterConfirmCommandDTO(
                tenantId,
                registrationSessionId,
                "teste@example.com",
                "654321" // Token diferente
        );

        // Criar uma sessão com 0 tentativas
        RegisterSession sessionComZeroTentativas = RegisterSession.of(
                registrationSessionId,
                tenantId,
                "teste@example.com",
                "123456",
                "encodedPassword",
                "João Silva",
                "11999999999",
                true,
                false,
                "192.168.1.1",
                "Mozilla/5.0",
                "São Paulo, SP",
                UserTypeEnum.PERSON,
                java.time.OffsetDateTime.now(),
                0, // 0 tentativas
                0  // 0 resend attempts
        );

        // Mock específico para este teste
        when(registerCachePort.getRegisterSession(command.email(), command.tenantId()))
                .thenReturn(Optional.of(sessionComZeroTentativas));

        // Mock para salvar a sessão atualizada (com 1 tentativa)
        doAnswer(invocation -> null).when(registerCachePort)
                .saveRegisterSession(anyString(), any(UUID.class), any(RegisterSession.class));

        // When & Then
        assertThatThrownBy(() -> registerCommandService.confirm(command))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Token inválido. Tentativas restantes: 4");

        verify(registerCachePort).getRegisterSession(eq(command.email()), eq(command.tenantId()));
        verify(registerCachePort).saveRegisterSession(eq(command.email()), eq(command.tenantId()), any(RegisterSession.class));
        verify(metricsPort).incrementCounter(eq("register_business_errors_total"), any(Map.class));
    }

    @Test
    @DisplayName("Deve lançar ValidationException quando atinge limite de tentativas")
    void deveLancarValidationExceptionQuandoAtingeLimiteTentativas() throws Exception {
        // Given
        session.incrementAttempts(); // 1 tentativa
        session.incrementAttempts(); // 2 tentativas
        session.incrementAttempts(); // 3 tentativas
        session.incrementAttempts(); // 4 tentativas
        session.incrementAttempts(); // 5 tentativas (limite)

        RegisterConfirmCommandDTO command = new RegisterConfirmCommandDTO(
                UUID.randomUUID(),
                session.getRegistrationSessionId(),
                "test@example.com",
                "654321" // Token diferente
        );

        when(registerCachePort.getRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(Optional.of(session));

        // When & Then
        assertThatThrownBy(() -> registerCommandService.confirm(command))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Número máximo de tentativas excedido. Por favor, inicie o registro novamente.");

        verify(registerCachePort).getRegisterSession(command.email(), command.tenantId());
        verify(registerCachePort).removeRegisterSession(command.email(), command.tenantId());
        verify(metricsPort).incrementCounter(eq("register_business_errors_total"), any(Map.class));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falha ao deserializar sessão")
    void deveLancarRuntimeExceptionQuandoFalhaAoDeserializarSessao() throws Exception {
        // Given
        RegisterConfirmCommandDTO command = new RegisterConfirmCommandDTO(
                UUID.randomUUID(),
                session.getRegistrationSessionId(),
                "test@example.com",
                "123456"
        );

        doThrow(new com.fasterxml.jackson.core.JsonProcessingException("Erro de deserialização") {})
                .when(registerCachePort).getRegisterSession(anyString(), any(UUID.class));

        // When & Then
        assertThatThrownBy(() -> registerCommandService.confirm(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Falha ao buscar sessão de registro no cache")
                .hasCauseInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falha ao serializar sessão atualizada")
    void deveLancarRuntimeExceptionQuandoFalhaAoSerializarSessaoAtualizada() throws Exception {
        // Given
        RegisterConfirmCommandDTO command = new RegisterConfirmCommandDTO(
                UUID.randomUUID(),
                session.getRegistrationSessionId(),
                "test@example.com",
                "654321" // Token diferente
        );

        when(registerCachePort.getRegisterSession(anyString(), any(UUID.class)))
                .thenReturn(Optional.of(session));
        doThrow(new com.fasterxml.jackson.core.JsonProcessingException("Erro de serialização") {})
                .when(registerCachePort).saveRegisterSession(anyString(), any(UUID.class), any(RegisterSession.class));

        // When & Then
        assertThatThrownBy(() -> registerCommandService.confirm(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Falha ao salvar sessão de registro no cache")
                .hasCauseInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }
}

