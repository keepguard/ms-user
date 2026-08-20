package com.keepguard.ms_user.adapters.in.rest.usernotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyPatchRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.mapper.NotifyAdapterMapper;
import com.keepguard.ms_user.application.port.in.UserNotifyPort;
import com.keepguard.ms_user.test.builder.UserNotifyTestBuilder;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para UserNotifyController
 * Testa os endpoints REST e mapeamento de DTOs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Notify Controller Tests")
class UserNotifyControllerTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @Mock
    private UserNotifyPort userNotifyPort;
    
    @Mock
    private NotifyAdapterMapper mapper;
    
    @InjectMocks
    private UserNotifyController userNotifyController;
    
    private UUID notifyId;
    private UUID userId;
    private UUID codeUser;
    private UUID tenantId;
    private String tenantIdStr;
    
    @BeforeEach
    void setUp() {
        notifyId = UUID.randomUUID();
        userId = UUID.randomUUID();
        codeUser = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        tenantIdStr = tenantId.toString();
        
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userNotifyController).build();
    }
    
    // === TESTES DE CREATE ===
    
    @Test
    @DisplayName("Deve criar preferências de notificação com sucesso")
    void shouldCreateUserNotifySuccessfully() throws Exception {
        // Given
        var request = new UserNotifyCreateRequestDTO(
                userId,
                true,
                false,
                true,
                true
        );
        
        var view = UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toCreateCommand(any(), any())).thenReturn(UserNotifyTestBuilder.builder().buildCreateCommand());
        when(userNotifyPort.create(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withUserId(userId)
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(post("/api/v1/users/notify")
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notifyId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.notifyEmail").value(true))
                .andExpect(jsonPath("$.notifySms").value(false))
                .andExpect(jsonPath("$.notifyWhatsapp").value(true))
                .andExpect(jsonPath("$.notifyPush").value(true));
    }
    
    // === TESTES DE GET BY USER ID ===
    
    @Test
    @DisplayName("Deve buscar preferências de notificação por userId com sucesso")
    void shouldGetByUserIdSuccessfully() throws Exception {
        // Given
        var view = UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toGetByUserIdQuery(userId, tenantId)).thenReturn(UserNotifyTestBuilder.builder().buildGetByUserIdQuery());
        when(userNotifyPort.getByUserId(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withUserId(userId)
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/notify/{userId}/notify", userId)
                        .header("X-Tenant-Id", tenantIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notifyId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }
    
    // === TESTES DE GET BY CODE USER ===
    
    @Test
    @DisplayName("Deve buscar preferências de notificação por codeUser com sucesso")
    void shouldGetByCodeUserSuccessfully() throws Exception {
        // Given
        var view = UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withCodeUser(codeUser)
                .buildDetailsView();
        
        when(mapper.toGetByCodeUserQuery(codeUser, tenantId)).thenReturn(UserNotifyTestBuilder.builder().buildGetByCodeUserQuery());
        when(userNotifyPort.getByCodeUser(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withCodeUser(codeUser)
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/notify/code/{codeUser}/notify", codeUser)
                        .header("X-Tenant-Id", tenantIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notifyId.toString()));
    }
    
    // === TESTES DE PATCH BY USER ID ===
    
    @Test
    @DisplayName("Deve atualizar preferências de notificação por userId com sucesso")
    void shouldPatchByUserIdSuccessfully() throws Exception {
        // Given
        var request = new UserNotifyPatchRequestDTO(
                false,
                true,
                false,
                true
        );
        
        var view = UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withUserId(userId)
                .withNotifyEmail(false)
                .withNotifySms(true)
                .buildDetailsView();
        
        when(mapper.toPatchCommand(eq(userId), any(), eq(tenantId), any())).thenReturn(UserNotifyTestBuilder.builder().buildPatchCommand());
        when(userNotifyPort.patchByUserId(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withUserId(userId)
                .withNotifyEmail(false)
                .withNotifySms(true)
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(patch("/api/v1/users/notify/{userId}/notify", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notifyId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.notifyEmail").value(false))
                .andExpect(jsonPath("$.notifySms").value(true));
    }
    
    // === TESTES DE PATCH BY CODE USER ===
    
    @Test
    @DisplayName("Deve atualizar preferências de notificação por codeUser com sucesso")
    void shouldPatchByCodeUserSuccessfully() throws Exception {
        // Given
        var request = new UserNotifyPatchRequestDTO(
                true,
                true,
                true,
                false
        );
        
        var view = UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withCodeUser(codeUser)
                .withAllEnabled()
                .withNotifyPush(false)
                .buildDetailsView();
        
        when(mapper.toPatchCommand(any(), eq(codeUser), eq(tenantId), any())).thenReturn(UserNotifyTestBuilder.builder().buildPatchCommand());
        when(userNotifyPort.patchByCodeUser(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(UserNotifyTestBuilder.builder()
                .withId(notifyId)
                .withCodeUser(codeUser)
                .withAllEnabled()
                .withNotifyPush(false)
                .buildResponseDTO());
        
        // When & Then
        mockMvc.perform(patch("/api/v1/users/notify/code/{codeUser}/notify", codeUser)
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notifyId.toString()))
                .andExpect(jsonPath("$.notifyEmail").value(true))
                .andExpect(jsonPath("$.notifySms").value(true))
                .andExpect(jsonPath("$.notifyWhatsapp").value(true))
                .andExpect(jsonPath("$.notifyPush").value(false));
    }
}
