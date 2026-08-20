package com.keepguard.ms_user.adapters.in.rest.address;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.lib_security.context.SecurityContext;
import com.keepguard.ms_user.adapters.in.rest.address.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.address.dto.response.AddressDetailsResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.address.mapper.AddressAdapterMapper;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.in.AddressPort;
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
@Tag(name = "Address", description = "Operações de endereços")
public class AddressController {

    private final AddressPort addressPort;
    private final AddressAdapterMapper mapper;
    private final SecurityContext securityContext;

    @PostMapping("/addresses")
    @Operation(summary = "Criar endereço", description = "Cria um novo endereço para um usuário")
    @MetricsEndpoint(endpoint = "address_create")
    public ResponseEntity<AddressDetailsResponseDTO> create(
            @Valid @RequestBody AddressCreateRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {

        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);

        log.info("Criando novo endereço para usuário: {}, application={}", request.userId(), tenantId);

        var command = mapper.toCreateCommand(request, tenantId);
        var view = addressPort.create(command);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/addresses/{id}")
    @Operation(summary = "Buscar endereço por ID", description = "Retorna um endereço pelo seu ID")
    @MetricsEndpoint(endpoint = "address_get_by_id")
    public ResponseEntity<AddressDetailsResponseDTO> getById(
            @PathVariable UUID id,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Buscando endereço por ID: application={}, id={}", tenantId, id);

        var query = mapper.toGetByIdQuery(id, tenantId);
        var view = addressPort.getById(query);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/addresses")
    @Operation(summary = "Buscar endereços por usuário", description = "Retorna todos os endereços de um usuário")
    @MetricsEndpoint(endpoint = "address_get_by_user")
    public ResponseEntity<List<AddressDetailsResponseDTO>> getByUserId(
            @PathVariable UUID userId,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Buscando endereços do usuário: application={}, userId={}", tenantId, userId);

        var query = mapper.toGetByUserIdQuery(userId, tenantId);
        var views = addressPort.getByUserId(query);
        var responses = views.stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Atualizar endereço", description = "Atualiza um endereço existente")
    @MetricsEndpoint(endpoint = "address_update")
    public ResponseEntity<AddressDetailsResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody AddressUpdateRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Atualizando endereço: application={}, id={}", tenantId, id);
        
        var command = mapper.toUpdateCommand(request, id, tenantId);
        var view = addressPort.update(command);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Deletar endereço", description = "Remove um endereço do sistema")
    @MetricsEndpoint(endpoint = "address_delete")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);
        
        log.info("Deletando endereço: application={}, id={}", tenantId, id);
        
        var command = mapper.toDeleteCommand(id, tenantId);
        addressPort.delete(command);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/addresses/search")
    @Operation(summary = "Buscar endereços", description = "Busca endereços de um usuário com filtros e paginação")
    @MetricsEndpoint(endpoint = "address_search")
    public ResponseEntity<PageResultDTO<AddressDetailsResponseDTO>> search(
            @PathVariable UUID userId,
            @Valid @ModelAttribute AddressSearchRequestDTO searchRequest,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Tenant-Id", required = true) String tenantIdHeader) {
        var tenantId = ValidationUtils.validateTenantId(tenantIdHeader);

        log.info("Buscando endereços do usuário: application={}, userId={}, city={}, state={}, type={}",
                tenantId, userId, searchRequest.getCity(), searchRequest.getState(), searchRequest.getType());

        if (searchRequest.getPage() < 0) {
            throw new IllegalArgumentException("Página deve ser maior ou igual a 0");
        }
        if (searchRequest.getSize() < 1 || searchRequest.getSize() > 100) {
            throw new IllegalArgumentException("Tamanho da página deve estar entre 1 e 100");
        }

        var query = mapper.toSearchQuery(searchRequest, tenantId, userId);
        var result = addressPort.search(query);

        var responseData = result.content().stream()
            .map(mapper::toSearchResponseDTO)
            .toList();

        var response = new PageResultDTO<>(
            responseData,
            result.totalElements(),
            result.page(),
            result.size()
        );
        
        return ResponseEntity.ok(response);
    }
}

