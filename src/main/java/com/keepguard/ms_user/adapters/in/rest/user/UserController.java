package com.keepguard.ms_user.adapters.in.rest.user;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.lib_security.annotation.PublicEndpoint;
import com.keepguard.ms_user.adapters.in.rest.user.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.UserResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.user.mapper.UserAdapterMapper;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.in.UserPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "Operações de usuários")
public class UserController {

    private final UserPort userPort;
    private final UserAdapterMapper mapper;

    @PostMapping("/users")
    @PublicEndpoint("Endpoint público para criação de novos usuários")
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema (endpoint público)")
    @MetricsEndpoint(endpoint = "user_create")
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserCreateRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Criando novo usuário: {}, application={} (endpoint público)", request.email(), xApplicationUuid);

        var command = mapper.toCreateCommand(request, xApplicationUuid);
        var view = userPort.create(command);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário pelo seu ID")
    @MetricsEndpoint(endpoint = "user_get_by_id")
    public ResponseEntity<UserResponseDTO> getById(
            @PathVariable UUID id,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Buscando usuário por ID: application={}, id={}", xApplicationUuid, id);

        var query = mapper.toGetByIdQuery(id, xApplicationUuid);
        var view = userPort.getById(query);

        var response = mapper.toGetByIdResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/code/{codeUser}")
    @Operation(summary = "Buscar usuário por codeUser", description = "Retorna um usuário pelo seu codeUser")
    @MetricsEndpoint(endpoint = "user_get_by_code")
    public ResponseEntity<UserResponseDTO> getByCodeUser(
            @PathVariable UUID codeUser,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Buscando usuário por codeUser: application={}, codeUser={}", xApplicationUuid, codeUser);
        var query = mapper.toGetByCodeUserQuery(codeUser, xApplicationUuid);
        var view = userPort.getByCodeUser(query);
        var response = mapper.toGetByCodeUserResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/email/{email}")
    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário pelo seu email")
    @MetricsEndpoint(endpoint = "user_get_by_email")
    public ResponseEntity<UserResponseDTO> getByEmail(
            @PathVariable String email,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Buscando usuário por email: application={}, email={}", xApplicationUuid, email);
        var query = mapper.toGetByEmailQuery(email, xApplicationUuid);
        var view = userPort.getByEmail(query);
        var response = mapper.toGetByEmail(view);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza um usuário existente")
    @MetricsEndpoint(endpoint = "user_update")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Atualizando usuário: application={}, id={}", xApplicationUuid, id);
        var command = mapper.toUpdateCommand(request, id, xApplicationUuid);
        var view = userPort.update(command);
        var response = mapper.toResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    @PublicEndpoint("Endpoint público para deleção de usuários (compensação de SAGA)")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @MetricsEndpoint(endpoint = "user_delete")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Deletando usuário: application={}, id={}", xApplicationUuid, id);

        var command = mapper.toDeleteCommand(id, xApplicationUuid);
        userPort.delete(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/companies/{companyId}/users")
    @Operation(summary = "Buscar usuários por empresa", description = "Busca usuários de uma empresa específica com filtros e paginação")
    public ResponseEntity<PageResultDTO<UserResponseDTO>> searchByCompany(
            @PathVariable String companyId,
            @Valid @ModelAttribute UserSearchRequestDTO searchRequest,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);

        log.info("Buscando usuários da empresa: application={}, companyId={}, email={}, type={}, status={}",
                xApplicationUuid, companyId, searchRequest.getEmail(), searchRequest.getType(), searchRequest.getStatus());
        
        UUID companyIdUuid = UUID.fromString(companyId);

        if (searchRequest.getPage() < 0) {
            throw new IllegalArgumentException("Página deve ser maior ou igual a 0");
        }
        if (searchRequest.getSize() < 1 || searchRequest.getSize() > 100) {
            throw new IllegalArgumentException("Tamanho da página deve estar entre 1 e 100");
        }

        var query = mapper.toSearchQuery(searchRequest, xApplicationUuid, companyIdUuid);
        var result = userPort.search(query);

        var responseData = result.content().stream()
            .map(userSearchView -> mapper.toResponseDTO(userSearchView))
            .toList();

        var response = new PageResultDTO<>(
            responseData,
            result.totalElements(),
            result.page(),
            result.size()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}/activate")
    @PublicEndpoint("Endpoint público para ativação de usuários pendentes ou inativos")
    @Operation(summary = "Ativar usuário", description = "Ativa um usuário pendente ou inativo")
    @MetricsEndpoint(endpoint = "user_activate")
    public ResponseEntity<UserResponseDTO> activate(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Ativando usuário: application={}, id={}, motivo={}", xApplicationUuid, id, request.reason());

        var command = mapper.toStatusChangeCommand(id, request.reason(), xApplicationUuid);
        var view = userPort.activate(command);
        var response = mapper.toResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}/deactivate")
    @PublicEndpoint("Endpoint público para desativação de usuários ativos")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário ativo")
    @MetricsEndpoint(endpoint = "user_deactivate")
    public ResponseEntity<UserResponseDTO> deactivate(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Desativando usuário: application={}, id={}, motivo={}", xApplicationUuid, id, request.reason());

        var command = mapper.toStatusChangeCommand(id, request.reason(), xApplicationUuid);
        var view = userPort.deactivate(command);
        var response = mapper.toResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}/block")
    @PublicEndpoint("Endpoint público para bloqueio de usuários")
    @Operation(summary = "Bloquear usuário", description = "Bloqueia um usuário")
    @MetricsEndpoint(endpoint = "user_block")
    public ResponseEntity<UserResponseDTO> block(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Bloqueando usuário: application={}, id={}, motivo={}", xApplicationUuid, id, request.reason());
        var command = mapper.toStatusChangeCommand(id, request.reason(), xApplicationUuid);
        var view = userPort.block(command);
        var response = mapper.toResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}/unblock")
    @PublicEndpoint("Endpoint público para desbloqueio de usuários")
    @Operation(summary = "Desbloquear usuário", description = "Desbloqueia um usuário bloqueado")
    @MetricsEndpoint(endpoint = "user_unblock")
    public ResponseEntity<UserResponseDTO> unblock(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Desbloqueando usuário: application={}, id={}, motivo={}", xApplicationUuid, id, request.reason());
        var command = mapper.toStatusChangeCommand(id, request.reason(), xApplicationUuid);
        var view = userPort.unblock(command);
        var response = mapper.toResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}/suspend")
    @PublicEndpoint("Endpoint público para suspensão de usuários ativos")
    @Operation(summary = "Suspender usuário", description = "Suspende um usuário ativo")
    @MetricsEndpoint(endpoint = "user_suspend")
    public ResponseEntity<UserResponseDTO> suspend(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Suspendendo usuário: application={}, id={}, motivo={}", xApplicationUuid, id, request.reason());
        var command = mapper.toStatusChangeCommand(id, request.reason(), xApplicationUuid);
        var view = userPort.suspend(command);
        var response = mapper.toResponseDTO(view);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}/unsuspend")
    @PublicEndpoint("Endpoint público para reativação de usuários suspensos")
    @Operation(summary = "Reativar usuário suspenso", description = "Reativa um usuário suspenso")
    @MetricsEndpoint(endpoint = "user_unsuspend")
    public ResponseEntity<UserResponseDTO> unsuspend(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Reativando usuário suspenso: application={}, id={}, motivo={}", xApplicationUuid, id, request.reason());
        var command = mapper.toStatusChangeCommand(id, request.reason(), xApplicationUuid);
        var view = userPort.unsuspend(command);
        var response = mapper.toResponseDTO(view);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/users/batch/activate")
    @Operation(summary = "Ativar usuários em lote", description = "Ativa múltiplos usuários")
    @MetricsEndpoint(endpoint = "user_batch_activate")
    public ResponseEntity<List<UserResponseDTO>> activateBatch(
            @Valid @RequestBody UserBatchStatusRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Ativando usuários em lote: application={}, userIds={}, motivo={}", xApplicationUuid, request.userIds(), request.reason());
        var command = mapper.toBatchStatusCommand(request.userIds(), request.reason(), xApplicationUuid);
        var views = userPort.activateBatch(command);
        var responses = views.stream()
                .map(mapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/users/batch/deactivate")
    @Operation(summary = "Desativar usuários em lote", description = "Desativa múltiplos usuários")
    @MetricsEndpoint(endpoint = "user_batch_deactivate")
    public ResponseEntity<List<UserResponseDTO>> deactivateBatch(
            @Valid @RequestBody UserBatchStatusRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Desativando usuários em lote: application={}, userIds={}, motivo={}", xApplicationUuid, request.userIds(), request.reason());
        var command = mapper.toBatchStatusCommand(request.userIds(), request.reason(), xApplicationUuid);
        var views = userPort.deactivateBatch(command);
        var responses = views.stream()
                .map(mapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(responses);
    }

}
