package com.keepguard.ms_user.adapters.in.rest.usernotify;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.lib_security.annotation.PublicEndpoint;
import com.keepguard.lib_security.context.SecurityContext;
import com.keepguard.ms_user.adapters.in.rest.usernotify.mapper.NotifyAdapterMapper;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyPatchRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.response.UserNotifyResponseDTO;
import com.keepguard.ms_user.application.port.in.UserNotifyPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Notify", description = "Operações de preferências de notificação")
public class UserNotifyController {

    private final UserNotifyPort userNotifyPort;
    private final NotifyAdapterMapper mapper;
    private final SecurityContext securityContext;

    @PublicEndpoint("Endpoint público para criação de preferências de notificação")
    @PostMapping("/notify")
    @Operation(summary = "Criar preferências de notificação", description = "Cria preferências de notificação para um usuário")
    @MetricsEndpoint(endpoint = "user_notify_create", operation = "Criar preferências de notificação")
    public ResponseEntity<UserNotifyResponseDTO> create(
            @Valid @RequestBody UserNotifyCreateRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {

        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Criando preferências de notificação para usuário: {}, application={}", request.userId(), tenantId);
        
        var command = mapper.toCreateCommand(tenantId, request);
        var notifyView = userNotifyPort.create(command);
        var responseDTO = mapper.toResponseDTO(notifyView);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/notify/{userId}/notify")
    @Operation(summary = "Buscar preferências de notificação", description = "Retorna as preferências de notificação de um usuário")
    @MetricsEndpoint(endpoint = "user_notify_get", operation = "Buscar preferências de notificação")
    public ResponseEntity<UserNotifyResponseDTO> getByUserId(
            @PathVariable UUID userId,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {

        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Buscando preferências de notificação: application={}, userId={}", tenantId, userId);
        
        var query = mapper.toGetByUserIdQuery(userId, tenantId);
        var notifyView = userNotifyPort.getByUserId(query);
        var responseDTO = mapper.toResponseDTO(notifyView);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/notify/{userId}/notify")
    @Operation(summary = "Atualizar preferências de notificação", description = "Atualiza as preferências de notificação de um usuário")
    @MetricsEndpoint(endpoint = "user_notify_patch", operation = "Atualizar preferências de notificação")
    public ResponseEntity<UserNotifyResponseDTO> patch(
            @PathVariable UUID userId,
            @RequestBody UserNotifyPatchRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Atualizando preferências de notificação: application={}, userId={}", tenantId, userId);
        
        var command = mapper.toPatchCommand(userId, null, tenantId, request);
        var notifyView = userNotifyPort.patchByUserId(command);
        var responseDTO = mapper.toResponseDTO(notifyView);
        
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/notify/code/{codeUser}/notify")
    @Operation(summary = "Buscar preferências de notificação por codeUser", description = "Retorna as preferências de notificação de um usuário usando codeUser")
    @MetricsEndpoint(endpoint = "user_notify_get_by_code", operation = "Buscar preferências de notificação por codeUser")
    public ResponseEntity<UserNotifyResponseDTO> getByCodeUser(
            @PathVariable UUID codeUser,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Buscando preferências de notificação por codeUser: application={}, codeUser={}", tenantId, codeUser);
        
        var query = mapper.toGetByCodeUserQuery(codeUser, tenantId);
        var notifyView = userNotifyPort.getByCodeUser(query);
        var responseDTO = mapper.toResponseDTO(notifyView);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/notify/code/{codeUser}/notify")
    @Operation(summary = "Atualizar preferências de notificação por codeUser", description = "Atualiza as preferências de notificação de um usuário usando codeUser")
    @MetricsEndpoint(endpoint = "user_notify_patch_by_code", operation = "Atualizar preferências de notificação por codeUser")
    public ResponseEntity<UserNotifyResponseDTO> patchByCodeUser(
            @PathVariable UUID codeUser,
            @RequestBody UserNotifyPatchRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Atualizando preferências de notificação por codeUser: application={}, codeUser={}", tenantId, codeUser);
        
        var command = mapper.toPatchCommand(null, codeUser, tenantId, request);
        var notifyView = userNotifyPort.patchByCodeUser(command);
        var responseDTO = mapper.toResponseDTO(notifyView);
        
        return ResponseEntity.ok(responseDTO);
    }
}
