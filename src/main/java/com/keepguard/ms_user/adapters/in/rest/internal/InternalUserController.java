package com.keepguard.ms_user.adapters.in.rest.internal;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_security.context.SecurityContext;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.UserResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.user.mapper.UserAdapterMapper;
import com.keepguard.ms_user.application.port.in.UserPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST INTERNO para operações de User (chamadas entre serviços).
 * 
 * Requer autenticação JWT com ROLE_SYSTEM ou ROLE_ADMIN.
 * Usado para comunicação entre microserviços e BFF autenticado.
 * 
 * @author KeepGuard Team
 * @version 1.1
 */
@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Internal Users", description = "Operações internas de usuários (protegido com ROLE_SYSTEM ou ROLE_ADMIN)")
public class InternalUserController {
    
    private final UserPort userPort;
    private final UserAdapterMapper mapper;
    private final SecurityContext securityContext;
    
    @GetMapping("/{id}")
    @Operation(summary = "[INTERNAL] Buscar usuário por ID", 
               description = "Endpoint interno para buscar dados básicos de usuário. Requer ROLE_SYSTEM ou ROLE_ADMIN.")
    @MetricsEndpoint(endpoint = "internal_user_get_by_id")
    public ResponseEntity<UserResponseDTO> getById(
            @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader(value = "X-Company-Id", required = true) UUID companyId) {

        if (!securityContext.hasRole("ROLE_SYSTEM") && !securityContext.hasRole("ROLE_ADMIN")) {
            log.warn("[INTERNAL] Acesso negado ao endpoint de usuário por ID: chamador sem ROLE_SYSTEM ou ROLE_ADMIN");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.debug("[INTERNAL] Buscando usuário por ID: id={}", id);

        var query = mapper.toGetByIdQuery(id, companyId);
        var view = userPort.getById(query);
        var response = mapper.toGetByIdResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/code/{codeUser}")
    @Operation(summary = "[INTERNAL] Buscar usuário por codeUser", 
               description = "Endpoint interno para buscar usuário pelo codeUser. Requer ROLE_SYSTEM ou ROLE_ADMIN.")
    @MetricsEndpoint(endpoint = "internal_user_get_by_code")
    public ResponseEntity<UserResponseDTO> getByCodeUser(
            @PathVariable UUID codeUser,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader(value = "X-Company-Id", required = true) UUID companyId) {

        if (!securityContext.hasRole("ROLE_SYSTEM") && !securityContext.hasRole("ROLE_ADMIN")) {
            log.warn("[INTERNAL] Acesso negado ao endpoint de usuário por codeUser: chamador sem ROLE_SYSTEM ou ROLE_ADMIN");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.debug("[INTERNAL] Buscando usuário por codeUser: codeUser={}", codeUser);

        var query = mapper.toGetByCodeUserQuery(codeUser, companyId);
        var view = userPort.getByCodeUser(query);
        var response = mapper.toGetByCodeUserResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }
}
