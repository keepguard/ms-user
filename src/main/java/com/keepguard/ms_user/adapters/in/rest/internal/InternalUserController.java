package com.keepguard.ms_user.adapters.in.rest.internal;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_security.annotation.PublicEndpoint;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.UserResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.user.mapper.UserAdapterMapper;
import com.keepguard.ms_user.application.port.in.UserPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST INTERNO para operações de User (chamadas entre serviços)
 * 
 * Endpoints internos não requerem autenticação JWT.
 * Usados apenas para comunicação entre microserviços.
 * 
 * @author KeepGuard Team
 * @version 1.0
 * @since 2025-11-06
 */
@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Internal Users", description = "Operações internas de usuários (sem autenticação)")
public class InternalUserController {
    
    private final UserPort userPort;
    private final UserAdapterMapper mapper;
    
    @GetMapping("/{id}")
    @PublicEndpoint
    @Operation(summary = "[INTERNAL] Buscar usuário por ID", 
               description = "Endpoint interno para buscar dados básicos de usuário. Não requer autenticação JWT.")
    @MetricsEndpoint(endpoint = "internal_user_get_by_id")
    public ResponseEntity<UserResponseDTO> getById(
            @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader(value = "X-Company-Id", required = true) String companyIdHeader) {

        log.debug("[INTERNAL] Buscando usuário por ID: id={}", id);

        UUID companyId = UUID.fromString(companyIdHeader);
        var query = mapper.toGetByIdQuery(id, null, companyId);
        var view = userPort.getById(query);
        var response = mapper.toGetByIdResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/code/{codeUser}")
    @PublicEndpoint
    @Operation(summary = "[INTERNAL] Buscar usuário por codeUser", 
               description = "Endpoint interno para buscar usuário pelo codeUser (campo 'sub' do JWT). Não requer autenticação JWT. Exige X-Company-Id resolvido pelo BFF via cache tenant→company.")
    @MetricsEndpoint(endpoint = "internal_user_get_by_code")
    public ResponseEntity<UserResponseDTO> getByCodeUser(
            @PathVariable UUID codeUser,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader(value = "X-Company-Id", required = true) String companyIdHeader) {

        log.debug("[INTERNAL] Buscando usuário por codeUser: codeUser={}", codeUser);

        UUID companyId = UUID.fromString(companyIdHeader);
        var query = mapper.toGetByCodeUserQuery(codeUser, null, companyId);
        var view = userPort.getByCodeUser(query);
        var response = mapper.toGetByCodeUserResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }
}

