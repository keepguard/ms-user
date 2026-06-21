package com.keepguard.ms_user.adapters.in.rest.contact;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.lib_security.context.SecurityContext;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.response.ContactDetailsResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.contact.mapper.ContactAdapterMapper;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.in.ContactPort;
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
@Tag(name = "Contact", description = "Operações de contatos")
public class ContactController {

    private final ContactPort contactPort;
    private final ContactAdapterMapper mapper;
    private final SecurityContext securityContext;

    @PostMapping("/contacts")
    @Operation(summary = "Criar contato", description = "Cria um novo contato para um usuário")
    @MetricsEndpoint(endpoint = "contact_create")
    public ResponseEntity<ContactDetailsResponseDTO> create(
            @Valid @RequestBody ContactCreateRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Criando novo contato para usuário: {}, application={}", request.userId(), xApplicationUuid);

        var command = mapper.toCreateCommand(request, xApplicationUuid);
        var view = contactPort.create(command);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/contacts/{id}")
    @Operation(summary = "Buscar contato por ID", description = "Retorna um contato pelo seu ID")
    @MetricsEndpoint(endpoint = "contact_get_by_id")
    public ResponseEntity<ContactDetailsResponseDTO> getById(
            @PathVariable UUID id,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Buscando contato por ID: application={}, id={}", xApplicationUuid, id);

        var query = mapper.toGetByIdQuery(id, xApplicationUuid);
        var view = contactPort.getById(query);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/contacts")
    @Operation(summary = "Buscar contatos por usuário", description = "Retorna todos os contatos de um usuário")
    @MetricsEndpoint(endpoint = "contact_get_by_user")
    public ResponseEntity<List<ContactDetailsResponseDTO>> getByUserId(
            @PathVariable UUID userId,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Buscando contatos do usuário: application={}, userId={}", xApplicationUuid, userId);

        var query = mapper.toGetByUserIdQuery(userId, xApplicationUuid);
        var views = contactPort.getByUserId(query);
        var responses = views.stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/contacts/{id}")
    @Operation(summary = "Atualizar contato", description = "Atualiza um contato existente")
    @MetricsEndpoint(endpoint = "contact_update")
    public ResponseEntity<ContactDetailsResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ContactUpdateRequestDTO request,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Atualizando contato: application={}, id={}", xApplicationUuid, id);
        
        var command = mapper.toUpdateCommand(request, id, xApplicationUuid);
        var view = contactPort.update(command);
        var response = mapper.toResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/contacts/{id}")
    @Operation(summary = "Deletar contato", description = "Remove um contato do sistema")
    @MetricsEndpoint(endpoint = "contact_delete")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);
        
        log.info("Deletando contato: application={}, id={}", xApplicationUuid, id);
        
        var command = mapper.toDeleteCommand(id, xApplicationUuid);
        contactPort.delete(command);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/contacts/search")
    @Operation(summary = "Buscar contatos", description = "Busca contatos de um usuário com filtros e paginação")
    @MetricsEndpoint(endpoint = "contact_search")
    public ResponseEntity<PageResultDTO<ContactDetailsResponseDTO>> search(
            @PathVariable UUID userId,
            @Valid @ModelAttribute ContactSearchRequestDTO searchRequest,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader(value = "X-Application", required = true) String xApplication) {
        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);

        log.info("Buscando contatos do usuário: application={}, userId={}, value={}, type={}",
                xApplicationUuid, userId, searchRequest.getValue(), searchRequest.getType());

        if (searchRequest.getPage() < 0) {
            throw new IllegalArgumentException("Página deve ser maior ou igual a 0");
        }
        if (searchRequest.getSize() < 1 || searchRequest.getSize() > 100) {
            throw new IllegalArgumentException("Tamanho da página deve estar entre 1 e 100");
        }

        var query = mapper.toSearchQuery(searchRequest, xApplicationUuid, userId);
        var result = contactPort.search(query);

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

