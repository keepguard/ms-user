package com.keepguard.ms_user.adapters.in.rest.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.request.ContactCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.request.ContactUpdateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.contact.mapper.ContactAdapterMapper;
import com.keepguard.ms_user.application.port.in.ContactPort;
import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import com.keepguard.ms_user.test.builder.ContactTestBuilder;
import com.keepguard.ms_user.test.builder.ContactRequestDTOBuilder;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para ContactController
 * Testa os endpoints REST e mapeamento de DTOs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Contact Controller Tests")
class ContactControllerTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @Mock
    private ContactPort contactPort;
    
    @Mock
    private ContactAdapterMapper mapper;
    
    @InjectMocks
    private ContactController contactController;
    
    private UUID contactId;
    private UUID userId;
    private UUID companyId;
    private String tenantIdStr;
    
    @BeforeEach
    void setUp() {
        contactId = UUID.randomUUID();
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        tenantIdStr = companyId.toString();
        
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
    }
    
    // === TESTES DE CREATE ===
    
    @Test
    @DisplayName("Deve criar contato com sucesso")
    void shouldCreateContactSuccessfully() throws Exception {
        // Given
        var request = new ContactCreateRequestDTO(
                userId,
                "11999999999",
                ContactTypeEnum.MOBILE,
                "Celular pessoal",
                true,
                true
        );
        
        var view = ContactTestBuilder.builder()
                .withId(contactId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toCreateCommand(any(), any())).thenReturn(ContactTestBuilder.builder().buildCreateCommand());
        when(contactPort.create(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(ContactRequestDTOBuilder.builder()
                .id(contactId)
                .userId(userId)
                .buildResponse());
        
        // When & Then
        mockMvc.perform(post("/api/v1/contacts")
                        .header("X-Company-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(contactId.toString()))
                .andExpect(jsonPath("$.user_id").value(userId.toString()));
    }
    
    // === TESTES DE GET BY ID ===
    
    @Test
    @DisplayName("Deve buscar contato por ID com sucesso")
    void shouldGetContactByIdSuccessfully() throws Exception {
        // Given
        var view = ContactTestBuilder.builder()
                .withId(contactId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toGetByIdQuery(contactId, companyId)).thenReturn(ContactTestBuilder.builder().buildGetByIdQuery());
        when(contactPort.getById(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(ContactRequestDTOBuilder.builder()
                .id(contactId)
                .userId(userId)
                .buildResponse());
        
        // When & Then
        mockMvc.perform(get("/api/v1/contacts/{id}", contactId)
                        .header("X-Company-Id", tenantIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contactId.toString()))
                .andExpect(jsonPath("$.user_id").value(userId.toString()));
    }
    
    // === TESTES DE GET BY USER ID ===
    
    @Test
    @DisplayName("Deve buscar contatos por usuário com sucesso")
    void shouldGetContactsByUserIdSuccessfully() throws Exception {
        // Given
        var view = ContactTestBuilder.builder()
                .withId(contactId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toGetByUserIdQuery(userId, companyId)).thenReturn(ContactTestBuilder.builder().buildGetByUserIdQuery());
        when(contactPort.getByUserId(any())).thenReturn(List.of(view));
        when(mapper.toResponseDTO(any())).thenReturn(ContactRequestDTOBuilder.builder()
                .id(contactId)
                .userId(userId)
                .buildResponse());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/contacts", userId)
                        .header("X-Company-Id", tenantIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(contactId.toString()))
                .andExpect(jsonPath("$[0].user_id").value(userId.toString()));
    }
    
    // === TESTES DE UPDATE ===
    
    @Test
    @DisplayName("Deve atualizar contato com sucesso")
    void shouldUpdateContactSuccessfully() throws Exception {
        // Given
        var request = new ContactUpdateRequestDTO(
                "11988888888",
                ContactTypeEnum.WHATSAPP,
                "WhatsApp atualizado",
                true,
                true
        );
        
        var view = ContactTestBuilder.builder()
                .withId(contactId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toUpdateCommand(any(), eq(contactId), eq(companyId))).thenReturn(ContactTestBuilder.builder().buildUpdateCommand());
        when(contactPort.update(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(ContactRequestDTOBuilder.builder()
                .id(contactId)
                .userId(userId)
                .buildResponse());
        
        // When & Then
        mockMvc.perform(put("/api/v1/contacts/{id}", contactId)
                        .header("X-Company-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contactId.toString()));
    }
    
    // === TESTES DE DELETE ===
    
    @Test
    @DisplayName("Deve deletar contato com sucesso")
    void shouldDeleteContactSuccessfully() throws Exception {
        // Given
        when(mapper.toDeleteCommand(contactId, companyId)).thenReturn(ContactTestBuilder.builder().buildDeleteCommand());
        
        // When & Then
        mockMvc.perform(delete("/api/v1/contacts/{id}", contactId)
                        .header("X-Company-Id", tenantIdStr))
                .andExpect(status().isNoContent());
    }
    
    // === TESTES DE SEARCH ===
    
    @Test
    @DisplayName("Deve buscar contatos com filtros e paginação com sucesso")
    void shouldSearchContactsSuccessfully() throws Exception {
        // Given
        var searchView = ContactTestBuilder.builder()
                .withId(contactId)
                .withUserId(userId)
                .buildSearchView();
        
        var pageResult = new com.keepguard.ms_user.application.dto.common.PageResultDTO<>(
                List.of(searchView), 1L, 0, 20
        );
        
        when(mapper.toSearchQuery(any(), eq(companyId), eq(userId))).thenReturn(ContactTestBuilder.builder().buildSearchQuery());
        when(contactPort.search(any())).thenReturn(pageResult);
        when(mapper.toSearchResponseDTO(any())).thenReturn(ContactRequestDTOBuilder.builder().buildResponse());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/contacts/search", userId)
                        .header("X-Company-Id", tenantIdStr)
                        .param("value", "11999999999")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content").isArray());
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando página for negativa")
    void shouldReturnBadRequestWhenPageIsNegative() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/contacts/search", userId)
                        .header("X-Company-Id", tenantIdStr)
                        .param("page", "-1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando tamanho da página for inválido")
    void shouldReturnBadRequestWhenPageSizeIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/contacts/search", userId)
                        .header("X-Company-Id", tenantIdStr)
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }
}
