package com.keepguard.ms_user.adapters.in.rest.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_user.adapters.in.rest.address.dto.request.AddressCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.address.dto.request.AddressUpdateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.address.mapper.AddressAdapterMapper;
import com.keepguard.ms_user.application.port.in.AddressPort;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import com.keepguard.ms_user.test.builder.AddressTestBuilder;
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
 * Testes unitários para AddressController
 * Testa os endpoints REST e mapeamento de DTOs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Address Controller Tests")
class AddressControllerTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @Mock
    private AddressPort addressPort;
    
    @Mock
    private AddressAdapterMapper mapper;
    
    @InjectMocks
    private AddressController addressController;
    
    private UUID addressId;
    private UUID userId;
    private UUID tenantId;
    private String tenantIdStr;
    
    @BeforeEach
    void setUp() {
        addressId = UUID.randomUUID();
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        tenantIdStr = tenantId.toString();
        
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
    }
    
    // === TESTES DE CREATE ===
    
    @Test
    @DisplayName("Deve criar endereço com sucesso")
    void shouldCreateAddressSuccessfully() throws Exception {
        // Given
        var request = new AddressCreateRequestDTO(
                userId,
                "Rua das Flores",
                "123",
                "Apto 45",
                "Centro",
                "São Paulo",
                "SP",
                "01234567",
                "Brasil",
                AddressTypeEnum.RESIDENTIAL,
                true,
                true
        );
        
        var view = AddressTestBuilder.builder()
                .withId(addressId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toCreateCommand(any(), any())).thenReturn(AddressTestBuilder.builder().buildCreateCommand());
        when(addressPort.create(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(AddressTestBuilder.builder().withId(addressId).withUserId(userId).buildResponseDTO());
        
        // When & Then
        mockMvc.perform(post("/api/v1/addresses")
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(addressId.toString()))
                .andExpect(jsonPath("$.user_id").value(userId.toString()));
    }
    
    // === TESTES DE GET BY ID ===
    
    @Test
    @DisplayName("Deve buscar endereço por ID com sucesso")
    void shouldGetAddressByIdSuccessfully() throws Exception {
        // Given
        var view = AddressTestBuilder.builder()
                .withId(addressId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toGetByIdQuery(addressId, tenantId)).thenReturn(AddressTestBuilder.builder().buildGetByIdQuery());
        when(addressPort.getById(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(AddressTestBuilder.builder().withId(addressId).withUserId(userId).buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/addresses/{id}", addressId)
                        .header("X-Tenant-Id", tenantIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()))
                .andExpect(jsonPath("$.user_id").value(userId.toString()));
    }
    
    // === TESTES DE GET BY USER ID ===
    
    @Test
    @DisplayName("Deve buscar endereços por usuário com sucesso")
    void shouldGetAddressesByUserIdSuccessfully() throws Exception {
        // Given
        var view = AddressTestBuilder.builder()
                .withId(addressId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toGetByUserIdQuery(userId, tenantId)).thenReturn(AddressTestBuilder.builder().buildGetByUserIdQuery());
        when(addressPort.getByUserId(any())).thenReturn(List.of(view));
        when(mapper.toResponseDTO(any())).thenReturn(AddressTestBuilder.builder().withId(addressId).withUserId(userId).buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/addresses", userId)
                        .header("X-Tenant-Id", tenantIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(addressId.toString()))
                .andExpect(jsonPath("$[0].user_id").value(userId.toString()));
    }
    
    // === TESTES DE UPDATE ===
    
    @Test
    @DisplayName("Deve atualizar endereço com sucesso")
    void shouldUpdateAddressSuccessfully() throws Exception {
        // Given
        var request = new AddressUpdateRequestDTO(
                "Rua Atualizada",
                "456",
                "Sala 10",
                "Vila Nova",
                "Rio de Janeiro",
                "RJ",
                "20000000",
                "Brasil",
                AddressTypeEnum.COMMERCIAL,
                false,
                true
        );
        
        var view = AddressTestBuilder.builder()
                .withId(addressId)
                .withUserId(userId)
                .buildDetailsView();
        
        when(mapper.toUpdateCommand(any(), eq(addressId), eq(tenantId))).thenReturn(AddressTestBuilder.builder().buildUpdateCommand());
        when(addressPort.update(any())).thenReturn(view);
        when(mapper.toResponseDTO(view)).thenReturn(AddressTestBuilder.builder().withId(addressId).withUserId(userId).buildResponseDTO());
        
        // When & Then
        mockMvc.perform(put("/api/v1/addresses/{id}", addressId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()));
    }
    
    // === TESTES DE DELETE ===
    
    @Test
    @DisplayName("Deve deletar endereço com sucesso")
    void shouldDeleteAddressSuccessfully() throws Exception {
        // Given
        when(mapper.toDeleteCommand(addressId, tenantId)).thenReturn(AddressTestBuilder.builder().buildDeleteCommand());
        
        // When & Then
        mockMvc.perform(delete("/api/v1/addresses/{id}", addressId)
                        .header("X-Tenant-Id", tenantIdStr))
                .andExpect(status().isNoContent());
    }
    
    // === TESTES DE SEARCH ===
    
    @Test
    @DisplayName("Deve buscar endereços com filtros e paginação com sucesso")
    void shouldSearchAddressesSuccessfully() throws Exception {
        // Given
        var searchView = AddressTestBuilder.builder()
                .withId(addressId)
                .withUserId(userId)
                .buildSearchView();
        
        var pageResult = new com.keepguard.ms_user.application.dto.common.PageResultDTO<>(
                List.of(searchView), 1L, 0, 20
        );
        
        when(mapper.toSearchQuery(any(), eq(tenantId), eq(userId))).thenReturn(AddressTestBuilder.builder().buildSearchQuery());
        when(addressPort.search(any())).thenReturn(pageResult);
        when(mapper.toSearchResponseDTO(any())).thenReturn(AddressTestBuilder.builder().withId(addressId).withUserId(userId).buildResponseDTO());
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/addresses/search", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .param("city", "São Paulo")
                        .param("state", "SP")
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
        mockMvc.perform(get("/api/v1/users/{userId}/addresses/search", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .param("page", "-1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando tamanho da página for inválido")
    void shouldReturnBadRequestWhenPageSizeIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/addresses/search", userId)
                        .header("X-Tenant-Id", tenantIdStr)
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }
}
