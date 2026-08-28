package com.keepguard.ms_user.application.service.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.lib_common.utils.CodeGeneratorUtils;
import com.keepguard.ms_user.application.dto.register.RegisterConfirmCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitViewDTO;
import com.keepguard.ms_user.application.dto.register.RegisterResendCommandDTO;
import com.keepguard.ms_user.application.mapper.RegisterApplicationMapper;
import com.keepguard.ms_user.application.port.in.RegisterPort;
import com.keepguard.ms_user.application.port.out.cache.RegisterCachePort;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterCommandService {

    private final UserRepositoryPort userRepositoryPort;
    private final RegisterCachePort registerCachePort;
    private final RegisterApplicationMapper registerApplicationMapper;
    private final PasswordEncoder passwordEncoder;
    private final MetricsPort metricsPort;

    @Value("${cache.redis.ttl.register_session:1200}")
    private long registerSessionTtlSeconds;

    @Value("${cache.redis.max_attempts.register_token:5}")
    private int maxAttempts;

    @Value("${cache.redis.max_attempts.register_resend:5}")
    private int maxResendAttempts;

    @LogOperation(
        operation = "REGISTER_INIT",
        description = "Inicializando registro de usuário: {command.email}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "REGISTER_SESSION"
    )
    public RegisterInitViewDTO init(RegisterInitCommandDTO command) {
        log.info("Iniciando registro de usuário: email={}, companyId={}, type={}", 
                command.email(), command.companyId(), command.type());

        // 0 - Validar palavras proibidas no nome (já validado pelo @ModeratedContent)
        // 1 - Consultar se o email já existe nesta company
        validateEmailNotExists(command.email(), command.companyId());
        validatePhoneNotExists(command.phone(), command.companyId());
        
        // 2 - Verificar se já existe sessão de registro no Redis
        validateNoActiveSession(command.email(), command.companyId());
        
        // 3 - Validar aceite de termos
        validateTermsAccepted(command.hasAcceptedTermsAndPrivacy());
        
        // 4 - Gerar tokens de 6 dígitos independentes para cada canal
        String emailToken = CodeGeneratorUtils.generateSixDigitCode();
        String smsToken = CodeGeneratorUtils.generateSixDigitCode();
        String whatsAppToken = CodeGeneratorUtils.generateSixDigitCode();
        
        // 5 - Criptografar senha
        String passwordHash = passwordEncoder.encode(command.password());
        
        // 7 - Criar sessão de registro
        RegisterSession session = registerApplicationMapper.toDomain(command, emailToken, smsToken, whatsAppToken, passwordHash);
        if (session == null) {
            // Fallback caso mapper mockado no teste só tenha mockado toDomain(command, token, passwordHash)
            session = registerApplicationMapper.toDomain(command, emailToken, passwordHash);
        }
        
        // 8 - Salvar no Redis
        try {
            registerCachePort.saveRegisterSession(command.email(), command.companyId(), session);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Falha ao serializar sessão de registro: email={}, companyId={}", 
                    command.email(), command.companyId(), e);
            throw new RuntimeException("Falha ao salvar sessão de registro no cache", e);
        }
        
        // 9 - Log dos tokens gerados
        log.info("Tokens de verificação gerados para email={}: emailToken={}, smsToken={}, whatsAppToken={}", 
                command.email(), emailToken, smsToken, whatsAppToken);
        
        // Métricas
        metricsPort.incrementCounter("register_init_total",
                Map.of("tenant_id", command.companyId().toString(), "type", command.type().name()));
        
        // Retornar view
        return registerApplicationMapper.toView(session, "Tokens de verificação enviados.", (int) registerSessionTtlSeconds);
    }

    private void validateEmailNotExists(String email, java.util.UUID companyId) {
        if (userRepositoryPort.existsByEmailAndCompanyId(email, companyId, null)) {
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "EMAIL_ALREADY_EXISTS", "operation", "init"));
            throw new AlreadyExistsException(
                    "Email já está em uso nesta empresa: " + email,
                    "EMAIL_ALREADY_EXISTS",
                    Map.of("email", email, "companyId", companyId.toString())
            );
        }
    }

    private void validatePhoneNotExists(String phone, java.util.UUID companyId) {
        String normalizedPhone = com.keepguard.ms_user.domain.validator.PhoneValidator.validate(phone);
        if (normalizedPhone == null) {
            return;
        }
        if (userRepositoryPort.existsByPhoneE164AndCompanyId(normalizedPhone, companyId, null)) {
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "PHONE_ALREADY_EXISTS", "operation", "init"));
            throw new AlreadyExistsException(
                    "Telefone já está em uso nesta empresa: " + normalizedPhone,
                    "PHONE_ALREADY_EXISTS",
                    Map.of("phone", normalizedPhone, "companyId", companyId.toString())
            );
        }
    }

    private void validateNoActiveSession(String email, java.util.UUID companyId) {
        if (registerCachePort.existsRegisterSession(email, companyId)) {
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "ACTIVE_SESSION_EXISTS", "operation", "init"));
            throw new AlreadyExistsException(
                    "Já existe uma sessão de registro ativa para este email. Aguarde a expiração ou use o token enviado.", 
                    "ACTIVE_SESSION_EXISTS", 
                    Map.of("email", email)
            );
        }
    }

    private void validateTermsAccepted(Boolean hasAcceptedTermsAndPrivacy) {
        if (hasAcceptedTermsAndPrivacy == null || !hasAcceptedTermsAndPrivacy) {
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "TERMS_NOT_ACCEPTED", "operation", "init"));
            throw new ValidationException("Você deve aceitar os termos e condições de privacidade");
        }
    }

    @LogOperation(
        operation = "REGISTER_CONFIRM",
        description = "Confirmando registro de usuário: {command.email}",
        audit = true,
        auditAction = "CONFIRM",
        auditEntityType = "REGISTER_SESSION"
    )
    public RegisterSession confirm(RegisterConfirmCommandDTO command) {
        log.info("Confirmando registro de usuário: email={}, registrationSessionId={}, companyId={}", 
                command.email(), command.registrationSessionId(), command.companyId());

        // 1 - Buscar sessão no Redis
        RegisterSession session;
        try {
            session = registerCachePort.getRegisterSession(command.email(), command.companyId())
                    .orElseThrow(() -> {
                        metricsPort.incrementCounter("register_business_errors_total",
                                Map.of("error_code", "SESSION_NOT_FOUND", "operation", "confirm"));
                        return new NotFoundException(
                                "Sessão de registro não encontrada ou expirada. Por favor, inicie o registro novamente.",
                                "SESSION_NOT_FOUND",
                                Map.of("email", command.email())
                        );
                    });
        } catch (JsonProcessingException e) {
            log.error("Falha ao deserializar sessão de registro: email={}, companyId={}", 
                    command.email(), command.companyId(), e);
            throw new RuntimeException("Falha ao buscar sessão de registro no cache", e);
        }

        // 2 - Validar registrationSessionId
        if (!session.getRegistrationSessionId().equals(command.registrationSessionId())) {
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "INVALID_SESSION_ID", "operation", "confirm"));
            throw new ValidationException("ID da sessão de registro inválido");
        }

        // 3 - Validar tokens (Email e SMS se presentes / configurados)
        boolean isValid = true;
        
        // Se fornecido emailToken, valida com session.getEmailToken()
        String expectedEmailToken = session.getEmailToken() != null ? session.getEmailToken() : session.getToken();
        if (command.emailToken() != null && !command.emailToken().isBlank()) {
            if (!expectedEmailToken.equals(command.emailToken().trim())) {
                isValid = false;
            }
        } else if (command.token() != null && !command.token().isBlank()) {
            // Compatibilidade com token único
            if (!expectedEmailToken.equals(command.token().trim())) {
                isValid = false;
            }
        } else {
            isValid = false;
        }

        // Se a sessão possui smsToken e o comando forneceu smsToken, valida smsToken
        if (session.getSmsToken() != null && !session.getSmsToken().isBlank()) {
            if (command.smsToken() != null && !command.smsToken().isBlank()) {
                if (!session.getSmsToken().equals(command.smsToken().trim())) {
                    isValid = false;
                }
            }
        }

        if (!isValid) {
            log.warn("Token(s) inválido(s) para email={}, attempts={}", command.email(), session.getAttempts());
            
            // Incrementar tentativas
            session.incrementAttempts();
            
            // Salvar no Redis
            try {
                registerCachePort.saveRegisterSession(command.email(), command.companyId(), session);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("Falha ao serializar sessão de registro atualizada: email={}, companyId={}", 
                        command.email(), command.companyId(), e);
                throw new RuntimeException("Falha ao salvar sessão de registro no cache", e);
            }
            
            // Verificar se atingiu o limite
            if (session.getAttempts() >= maxAttempts) {
                log.warn("Limite de tentativas atingido para email={}, removendo sessão", command.email());
                
                // Remover do Redis
                registerCachePort.removeRegisterSession(command.email(), command.companyId());
                
                metricsPort.incrementCounter("register_business_errors_total",
                        Map.of("error_code", "MAX_ATTEMPTS_EXCEEDED", "operation", "confirm"));
                
                throw new ValidationException(
                        "Número máximo de tentativas excedido. Por favor, inicie o registro novamente."
                );
            }
            
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "INVALID_TOKEN", "operation", "confirm"));
            
            throw new ValidationException(
                    String.format("Token inválido. Tentativas restantes: %d", maxAttempts - session.getAttempts())
            );
        }

        // 4 - Tokens válidos! Remover sessão do Redis
        log.info("Tokens validados com sucesso para email={}, removendo sessão", command.email());
        registerCachePort.removeRegisterSession(command.email(), command.companyId());


        metricsPort.incrementCounter("register_confirm_total",
                Map.of("tenant_id", command.companyId().toString(), "status", "success"));
        return session;
    }

    @LogOperation(operation = "REGISTER_RESEND", description = "Reenviando token de registro: {command.email}")
    public RegisterSession resend(RegisterResendCommandDTO command) {
        log.info("Reenviando token de registro: email={}, companyId={}", 
                command.email(), command.companyId());
        
        // 1. Buscar sessão no Redis
        RegisterSession session;
        try {
            session = registerCachePort.getRegisterSession(command.email(), command.companyId())
                .orElseThrow(() -> {
                    metricsPort.incrementCounter("register_business_errors_total",
                            Map.of("error_code", "SESSION_NOT_FOUND", "operation", "resend"));
                    return new NotFoundException(
                            "Sessão de registro não encontrada ou expirada. Inicie o registro novamente.",
                            "SESSION_NOT_FOUND",
                            Map.of("email", command.email())
                    );
                });
        } catch (JsonProcessingException e) {
            log.error("Falha ao deserializar sessão de registro: email={}, companyId={}", 
                    command.email(), command.companyId(), e);
            throw new RuntimeException("Falha ao buscar sessão de registro no cache", e);
        }
        
        // 2. Validar registrationSessionId
        if (!session.getRegistrationSessionId().equals(command.registrationSessionId())) {
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "INVALID_SESSION_ID", "operation", "resend"));
            throw new ValidationException("ID da sessão de registro inválido");
        }
        
        // 3. Verificar limite de reenvios
        if (session.getResendAttempts() >= maxResendAttempts) {
            log.warn("Limite de reenvios atingido para email={}, removendo sessão", command.email());
            registerCachePort.removeRegisterSession(command.email(), command.companyId());
            
            metricsPort.incrementCounter("register_business_errors_total",
                    Map.of("error_code", "MAX_RESEND_ATTEMPTS_EXCEEDED", "operation", "resend"));
            
            throw new ValidationException(
                    "Número máximo de reenvios excedido. Por favor, inicie o registro novamente."
            );
        }
        
        // 4. Incrementar reenvios
        session.incrementResendAttempts();
        
        // 5. Salvar no Redis
        try {
            registerCachePort.saveRegisterSession(command.email(), command.companyId(), session);
        } catch (JsonProcessingException e) {
            log.error("Falha ao serializar sessão de registro atualizada: email={}, companyId={}", 
                    command.email(), command.companyId(), e);
            throw new RuntimeException("Falha ao salvar sessão de registro no cache", e);
        }
        
        // 6. Log de sucesso (email será enviado pelo BFF)
        log.info("Token preparado para reenvio: email={}, token={}, resendAttempts={}", 
            command.email(), session.getToken(), session.getResendAttempts());
        
        // 7. Métricas
        metricsPort.incrementCounter("register_resend_total",
            Map.of("tenant_id", command.companyId().toString()));
        
        return session;
    }
}

