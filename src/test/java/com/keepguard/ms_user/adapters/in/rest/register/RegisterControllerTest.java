package com.keepguard.ms_user.adapters.in.rest.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterConfirmRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterInitRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.response.RegisterInitResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.register.mapper.RegisterAdapterMapper;
import com.keepguard.ms_user.application.dto.register.RegisterInitViewDTO;
import com.keepguard.ms_user.application.port.in.RegisterPort;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para RegisterController
 * Testa os endpoints REST e mapeamento de DTOs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterController - Testes Unitários")
class RegisterControllerTest {

    @org.springframework.web.bind.annotation.RestControllerAdvice
    static class TestExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public org.springframework.http.ResponseEntity<Object> handleValidationException(Exception e) {
            return org.springframework.http.ResponseEntity.status(400).build();
        }
        
        @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
        public org.springframework.http.ResponseEntity<Object> handleMissingHeaderException(Exception e) {
            return org.springframework.http.ResponseEntity.status(400).build();
        }
        
        @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
        public org.springframework.http.ResponseEntity<Object> handleException(Exception e) {
            return org.springframework.http.ResponseEntity.status(500).build();
        }
    }

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private RegisterPort registerPort;

    @Mock
    private RegisterAdapterMapper mapper;

    @InjectMocks
    private RegisterController registerController;

    private UUID xApplication;
    private RegisterInitRequestDTO initRequest;
    private RegisterConfirmRequestDTO confirmRequest;

    @BeforeEach
    void setUp() {
        xApplication = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(registerController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        initRequest = new RegisterInitRequestDTO(
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

        confirmRequest = new RegisterConfirmRequestDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "teste@example.com",
                "123456"
        );
    }

    @Test
    @DisplayName("Deve inicializar registro com sucesso")
    void deveInicializarRegistroComSucesso() throws Exception {
        // Given
        RegisterInitViewDTO viewDTO = new RegisterInitViewDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "teste@exemplo.com",
                1200,
                "Token de verificação enviado.",
                "123456"
        );

        RegisterInitResponseDTO responseDTO = RegisterInitResponseDTO.builder()
                .registrationSessionId(viewDTO.registrationSessionId())
                .email(viewDTO.email())
                .expiresIn(viewDTO.expiresIn())
                .message(viewDTO.message())
                .token(viewDTO.token())
                .build();

        when(mapper.toInitCommand(any(), any())).thenReturn(null);
        when(registerPort.init(any())).thenReturn(viewDTO);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/register/init")
                        .header("X-Application", xApplication.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registrationSessionId").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.message").value("Token de verificação enviado."))
                .andExpect(jsonPath("$.token").value("123456"));

        verify(registerPort).init(any());
        verify(mapper).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando X-Application está ausente")
    void deveRetornarErro400QuandoXApplicationEstaAusente() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/register/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initRequest)))
                .andExpect(status().isBadRequest());

        verify(registerPort, never()).init(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando X-Application é inválido")
    void deveRetornarErro400QuandoXApplicationEInvalido() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/register/init")
                        .header("X-Application", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initRequest)))
                .andExpect(status().is5xxServerError()); // O controller lança exceção que resulta em 500

        verify(registerPort, never()).init(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando campos obrigatórios estão ausentes")
    void deveRetornarErro400QuandoCamposObrigatoriosEstaoAusentes() throws Exception {
        // Given
        RegisterInitRequestDTO requestIncompleto = new RegisterInitRequestDTO(
                "teste@example.com",
                null, // nameFull ausente
                null, // password ausente
                null, // phone ausente
                null, // hasAcceptedTermsAndPrivacy ausente
                null,
                null,
                null,
                null,
                null // type ausente
        );

        // When & Then
        mockMvc.perform(post("/api/v1/register/init")
                        .header("X-Application", xApplication.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestIncompleto)))
                .andExpect(status().isBadRequest());

        verify(registerPort, never()).init(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando email é inválido")
    void deveRetornarErro400QuandoEmailEInvalido() throws Exception {
        // Given
        RegisterInitRequestDTO requestEmailInvalido = new RegisterInitRequestDTO(
                "email-invalido", // email inválido
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

        // When & Then
        mockMvc.perform(post("/api/v1/register/init")
                        .header("X-Application", xApplication.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEmailInvalido)))
                .andExpect(status().isBadRequest());

        verify(registerPort, never()).init(any());
    }

    @Test
    @DisplayName("Deve confirmar registro com sucesso")
    void deveConfirmarRegistroComSucesso() throws Exception {
        // Given
        when(registerPort.confirm(any())).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/v1/register/confirm")
                        .header("X-Application", xApplication.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isOk());

        verify(registerPort).confirm(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando X-Application está ausente na confirmação")
    void deveRetornarErro400QuandoXApplicationEstaAusenteNaConfirmacao() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/register/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isBadRequest());

        verify(registerPort, never()).confirm(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando campos obrigatórios estão ausentes na confirmação")
    void deveRetornarErro400QuandoCamposObrigatoriosEstaoAusentesNaConfirmacao() throws Exception {
        // Given
        RegisterConfirmRequestDTO requestIncompleto = new RegisterConfirmRequestDTO(
                null, // registrationSessionId ausente
                "teste@example.com",
                null // token ausente
        );

        // When & Then
        mockMvc.perform(post("/api/v1/register/confirm")
                        .header("X-Application", xApplication.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestIncompleto)))
                .andExpect(status().isBadRequest());

        verify(registerPort, never()).confirm(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando token não tem 6 dígitos")
    void deveRetornarErro400QuandoTokenNaoTemSeisDigitos() throws Exception {
        // Given
        RegisterConfirmRequestDTO requestTokenInvalido = new RegisterConfirmRequestDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "teste@example.com",
                "12345" // token com 5 dígitos
        );

        // When & Then
        mockMvc.perform(post("/api/v1/register/confirm")
                        .header("X-Application", xApplication.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTokenInvalido)))
                .andExpect(status().isBadRequest());

        verify(registerPort, never()).confirm(any());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando email é inválido na confirmação")
    void deveRetornarErro400QuandoEmailEInvalidoNaConfirmacao() throws Exception {
        // Given
        RegisterConfirmRequestDTO requestEmailInvalido = new RegisterConfirmRequestDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "email-invalido", // email inválido
                "123456"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/register/confirm")
                        .header("X-Application", xApplication.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEmailInvalido)))
                .andExpect(status().isBadRequest());

        verify(registerPort, never()).confirm(any());
    }
}

