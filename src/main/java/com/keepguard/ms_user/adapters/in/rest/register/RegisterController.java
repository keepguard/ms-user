package com.keepguard.ms_user.adapters.in.rest.register;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.lib_security.annotation.PublicEndpoint;
import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterConfirmRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterInitRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterResendRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.response.RegisterConfirmResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.response.RegisterInitResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.response.RegisterResendResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.register.mapper.RegisterAdapterMapper;
import com.keepguard.ms_user.application.port.in.RegisterPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Register", description = "Operações de registro de usuários")
public class RegisterController {

    private final RegisterPort registerPort;
    private final RegisterAdapterMapper mapper;

    @Value("${cache.redis.max_attempts.register_resend:5}")
    private int maxResendAttempts;

    @Value("${cache.redis.ttl.register_session:1200}")
    private int registerSessionTtlSeconds;

    @PostMapping("/init")
    @PublicEndpoint("Endpoint público para inicialização do registro de usuário")
    @Operation(
        summary = "Inicializar registro de usuário",
        description = """
            Inicia o processo de registro de um novo usuário no sistema.
            
            **Funcionalidades:**
            - Valida se o email já está cadastrado
            - Verifica se já existe uma sessão de registro ativa
            - Gera um token de 6 dígitos
            - Criptografa a senha
            - Armazena a sessão no Redis com TTL de 20 minutos
            
            **Validações:**
            - Email deve ser válido e único na aplicação
            - Senha deve ter entre 8 e 100 caracteres
            - Nome completo é obrigatório
            - Telefone é obrigatório
            - Termos de uso devem ser aceitos
            - Tipo de usuário é obrigatório
            
            **Respostas:**
            - 201: Registro inicializado com sucesso
            - 400: Dados inválidos
            - 409: Email já cadastrado ou sessão ativa existe
            - 500: Erro interno do servidor
            """,
        tags = {"Register"}
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Registro inicializado com sucesso",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RegisterInitResponseDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Email já cadastrado ou sessão ativa existe"
        )
    })
    @MetricsEndpoint(endpoint = "register_init")
    public ResponseEntity<RegisterInitResponseDTO> init(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados para inicialização do registro",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RegisterInitRequestDTO.class)
                )
            )
            @Valid @RequestBody RegisterInitRequestDTO request,
            @Parameter(
                description = "UUID da aplicação cliente",
                required = true,
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Iniciando registro de usuário: email={}, application={} (endpoint público)", 
                request.email(), tenantId);

        var command = mapper.toInitCommand(request, tenantId);
        var view = registerPort.init(command);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/confirm")
    @PublicEndpoint("Endpoint público para confirmação do registro de usuário")
    @Operation(
        summary = "Confirmar registro de usuário",
        description = """
            Confirma o registro de um novo usuário validando o token de verificação.
            
            **Funcionalidades:**
            - Valida o token de 6 dígitos
            - Verifica se a sessão ainda é válida
            - Incrementa contador de tentativas em caso de erro
            - Remove a sessão do Redis após confirmação bem-sucedida
            
            **Validações:**
            - Token deve ter exatamente 6 dígitos
            - Sessão deve existir e não estar expirada
            - RegistrationSessionId deve corresponder à sessão
            - Máximo de 5 tentativas de validação
            
            **Respostas:**
            - 200: Registro confirmado com sucesso
            - 400: Token inválido ou dados inválidos
            - 404: Sessão não encontrada ou expirada
            - 500: Erro interno do servidor
            
            **Comportamento de Tentativas:**
            - 1ª tentativa inválida: Retorna erro, incrementa contador
            - 2ª-4ª tentativas inválidas: Retorna erro, incrementa contador
            - 5ª tentativa inválida: Remove sessão, retorna erro
            """,
        tags = {"Register"}
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Registro confirmado com sucesso",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RegisterConfirmResponseDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Token inválido ou dados inválidos"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Sessão não encontrada ou expirada"
        )
    })
    @MetricsEndpoint(endpoint = "register_confirm")
    public ResponseEntity<RegisterConfirmResponseDTO> confirm(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados para confirmação do registro",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RegisterConfirmRequestDTO.class)
                )
            )
            @Valid @RequestBody RegisterConfirmRequestDTO request,
            @Parameter(
                description = "UUID da aplicação cliente",
                required = true,
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Confirmando registro de usuário: email={}, application={} (endpoint público)", 
                request.email(), tenantId);

        var command = mapper.toConfirmCommand(request, tenantId);
        var session = registerPort.confirm(command);
        var response = mapper.toConfirmResponseDTO(session);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend")
    @PublicEndpoint("Endpoint público para reenvio de token de registro")
    @Operation(
        summary = "Reenviar token de registro",
        description = """
            Reenvia o token de verificação de registro para o email do usuário.
            
            **Funcionalidades:**
            - Valida se a sessão ainda existe
            - Verifica o ID da sessão de registro
            - Incrementa contador de reenvios (máximo 5)
            - Mantém o mesmo token de 6 dígitos
            - Email será enviado pelo BFF
            
            **Validações:**
            - Email deve ser válido
            - RegistrationSessionId deve corresponder à sessão
            - Máximo de 5 tentativas de reenvio
            - Sessão não pode estar expirada
            
            **Respostas:**
            - 200: Token preparado para reenvio com sucesso
            - 400: Dados inválidos ou limite de reenvios excedido
            - 404: Sessão não encontrada ou expirada
            """,
        tags = {"Register"}
    )
    @MetricsEndpoint(endpoint = "register_resend")
    public ResponseEntity<RegisterResendResponseDTO> resend(
        @Valid @RequestBody RegisterResendRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Reenviando token de registro: email={}, application={} (endpoint público)", 
                request.email(), tenantId);
        
        var command = mapper.toResendCommand(request, tenantId);
        var session = registerPort.resend(command);
        var response = mapper.toResendResponseDTO(session, maxResendAttempts, registerSessionTtlSeconds);
        
        return ResponseEntity.ok(response);
    }
}

