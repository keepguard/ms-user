package com.keepguard.ms_user.adapters.in.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.adapters.in.rest.user.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.user.mapper.UserAdapterMapper;
import com.keepguard.ms_user.application.port.in.UserPort;
import com.keepguard.ms_user.application.dto.user.UserDetailsViewDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchViewDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.test.builder.UserTestBuilder;
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
 * Testes unitários para UserController
 * Testa os endpoints REST e mapeamento de DTOs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller Tests")
class UserControllerTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @Mock
    private UserPort userPort;
    
    @Mock
    private UserAdapterMapper mapper;
    
    @InjectMocks
    private UserController userController;
    
    private UUID userId;
    private UUID codeUser;
    private UUID companyId;
    private UUID tenantId;
    private String tenantIdStr;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        codeUser = UUID.randomUUID();
        companyId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        tenantIdStr = tenantId.toString();
        
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }
    
    // === TESTES DE CREATE ===
    
    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        var request = new UserCreateRequestDTO(
                companyId,
                UserTypeEnum.PERSON,
                "test@example.com",
                "+5511999999999",
                "pt-BR",
                "America/Sao_Paulo",
                "https://example.com/avatar.jpg",
                null,
                null
        );
        
        var view = UserTestBuilder.builder()
                .withId(userId)
                .withCodeUser(codeUser)
                .withEmail("test@example.com")
                .buildDetailsView();
        
        when(mapper.toCreateCommand(any(), any())).thenReturn(UserTestBuilder.builder().buildCreateCommand());
        when(userPort.create(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(UserTestBuilder.builder().buildResponseDTO());
        
        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
    
    // === TESTES DE GET BY ID ===
    
    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Given
        var view = UserTestBuilder.builder()
                .withId(userId)
                .withCodeUser(codeUser)
                .buildDetailsView();
        
        var query = UserTestBuilder.builder().buildGetByIdQuery();
        when(mapper.toGetByIdQuery(userId, tenantId, companyId)).thenReturn(query);
        when(userPort.getById(any())).thenReturn(view);
        when(mapper.toGetByIdResponseDTO(view)).thenReturn(UserTestBuilder.builder()
                .withId(userId)
                .withCodeUser(codeUser)
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/{id}", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .header("X-Company-Id", companyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }
    
    // === TESTES DE GET BY CODE USER ===
    
    @Test
    @DisplayName("Deve buscar usuário por codeUser com sucesso")
    void shouldGetUserByCodeUserSuccessfully() throws Exception {
        // Given
        var view = UserTestBuilder.builder()
                .withId(userId)
                .withCodeUser(codeUser)
                .buildDetailsView();
        
        var query = UserTestBuilder.builder().buildGetByCodeUserQuery();
        when(mapper.toGetByCodeUserQuery(codeUser, null, companyId)).thenReturn(query);
        when(userPort.getByCodeUser(any())).thenReturn(view);
        when(mapper.toGetByCodeUserResponseDTO(view)).thenReturn(UserTestBuilder.builder()
                .withId(userId)
                .withCodeUser(codeUser)
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/code/{codeUser}", codeUser)
                        .header("X-Company-Id", companyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeUser").value(codeUser.toString()));
    }
    
    // === TESTES DE GET BY EMAIL ===
    
    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void shouldGetUserByEmailSuccessfully() throws Exception {
        // Given
        var email = "test@example.com";
        var view = UserTestBuilder.builder()
                .withId(userId)
                .withEmail(email)
                .buildDetailsView();
        
        var query = UserTestBuilder.builder().buildGetByEmailQuery();
        when(mapper.toGetByEmailQuery(email, null, companyId)).thenReturn(query);
        when(userPort.getByEmail(any())).thenReturn(view);
        when(mapper.toGetByEmail(view)).thenReturn(UserTestBuilder.builder().buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/email/{email}", email)
                        .header("X-Company-Id", companyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }
    
    // === TESTES DE UPDATE ===
    
    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        var request = new UserUpdateRequestDTO(
                companyId,
                codeUser,
                UserTypeEnum.PERSON,
                UserStatusEnum.ACTIVE,
                "updated@example.com",
                "+5511988888888",
                "pt-BR",
                "America/Sao_Paulo",
                "https://example.com/avatar2.jpg",
                null,
                null
        );
        
        var view = UserTestBuilder.builder()
                .withId(userId)
                .withEmail("updated@example.com")
                .buildDetailsView();
        
        when(mapper.toUpdateCommand(any(), eq(userId), eq(tenantId))).thenReturn(UserTestBuilder.builder().buildUpdateCommand());
        when(userPort.update(any())).thenReturn(view);
        when(mapper.toResponseDTO((UserDetailsViewDTO) view)).thenReturn(UserTestBuilder.builder()
                .withId(userId)
                .withEmail("updated@example.com")
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }
    
    // === TESTES DE DELETE ===
    
    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given
        when(mapper.toDeleteCommand(userId, tenantId)).thenReturn(UserTestBuilder.builder().buildDeleteCommand());
        
        // When & Then
        mockMvc.perform(delete("/api/v1/users/{id}", userId)
                        .header("X-Tenant-Id", tenantIdStr))
                .andExpect(status().isNoContent());
    }
    
    // === TESTES DE SEARCH ===
    
    @Test
    @DisplayName("Deve buscar usuários por empresa com filtros e paginação com sucesso")
    void shouldSearchUsersByCompanySuccessfully() throws Exception {
        // Given
        var searchView = UserTestBuilder.builder()
                .withId(userId)
                .withCompanyId(companyId)
                .buildSearchView();
        
        var pageResult = new PageResultDTO<>(
                List.of(searchView), 1L, 0, 20
        );
        
        var searchQuery = UserTestBuilder.builder().buildSearchQuery();
        when(mapper.toSearchQuery(any(), eq(tenantId), eq(companyId))).thenReturn(searchQuery);
        when(userPort.search(any())).thenReturn(pageResult);
        when(mapper.toResponseDTO(any(UserSearchViewDTO.class))).thenReturn(UserTestBuilder.builder().buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/companies/{companyId}/users", companyId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .param("email", "test@example.com")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content").isArray());
    }
    
    // === TESTES DE STATUS - ACTIVATE ===
    
    @Test
    @DisplayName("Deve ativar usuário com sucesso")
    void shouldActivateUserSuccessfully() throws Exception {
        // Given
        var request = new UserStatusChangeRequestDTO("Reativação solicitada");
        
        var view = UserTestBuilder.builder()
                .withId(userId)
                .withStatus(UserStatusEnum.ACTIVE)
                .buildDetailsView();
        
        var statusCommand = UserTestBuilder.builder().buildStatusChangeCommand();
        when(mapper.toStatusChangeCommand(eq(userId), any(), eq(tenantId))).thenReturn(statusCommand);
        when(userPort.activate(any())).thenReturn(view);
        when(mapper.toResponseDTO((UserDetailsViewDTO) view)).thenReturn(UserTestBuilder.builder()
                .withId(userId)
                .asActive()
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(patch("/api/v1/users/{id}/activate", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
    
    // === TESTES DE STATUS - DEACTIVATE ===
    
    @Test
    @DisplayName("Deve desativar usuário com sucesso")
    void shouldDeactivateUserSuccessfully() throws Exception {
        // Given
        var request = new UserStatusChangeRequestDTO("Usuário solicitou desativação");
        
        var view = UserTestBuilder.builder()
                .withId(userId)
                .withStatus(UserStatusEnum.INACTIVE)
                .buildDetailsView();
        
        var statusCommand = UserTestBuilder.builder().buildStatusChangeCommand();
        when(mapper.toStatusChangeCommand(eq(userId), any(), eq(tenantId))).thenReturn(statusCommand);
        when(userPort.deactivate(any())).thenReturn(view);
        when(mapper.toResponseDTO((UserDetailsViewDTO) view)).thenReturn(UserTestBuilder.builder()
                .withId(userId)
                .asInactive()
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(patch("/api/v1/users/{id}/deactivate", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }
    
    // === TESTES DE BATCH OPERATIONS ===
    
    @Test
    @DisplayName("Deve ativar usuários em lote com sucesso")
    void shouldActivateBatchSuccessfully() throws Exception {
        // Given
        var userIds = List.of(userId, UUID.randomUUID());
        var request = new UserBatchStatusRequestDTO(userIds, "Ativação em lote");
        
        var views = List.of(
                UserTestBuilder.builder().withId(userId).asActive().buildDetailsView(),
                UserTestBuilder.builder().asActive().buildDetailsView()
        );
        
        var batchCommand = UserTestBuilder.builder().buildBatchStatusCommand();
        when(mapper.toBatchStatusCommand(eq(userIds), any(), eq(tenantId))).thenReturn(batchCommand);
        when(userPort.activateBatch(any())).thenReturn(views);
        when(mapper.toResponseDTO(any(UserDetailsViewDTO.class))).thenReturn(UserTestBuilder.builder().asActive().buildResponseDTO());
        
        // When & Then
        mockMvc.perform(patch("/api/v1/users/batch/activate")
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
