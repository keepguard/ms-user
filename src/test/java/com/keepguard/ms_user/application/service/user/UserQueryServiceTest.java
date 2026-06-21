package com.keepguard.ms_user.application.service.user;

import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.application.mapper.UserApplicationMapper;
import com.keepguard.ms_user.application.port.out.cache.UserCachePort;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.entity.UserProfile;
import com.keepguard.ms_user.infrastructure.persistence.UserRepositoryAdapter;
import com.keepguard.ms_user.test.builder.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserQueryService
 * Segue o padrão profissional usado no ms-auth
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Query Service Tests")
class UserQueryServiceTest {
    
    private UserQueryService userQueryService;
    
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserCachePort userCachePort;
    
    @Mock
    private UserApplicationMapper userApplicationMapper;
    
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    
    @Mock
    private MetricsPort metricsPort;
    
    private User user;
    private UserProfile personProfile;
    private UserDetailsViewDTO userDetailsView;
    private UUID userId;
    private UUID codeUser;
    private UUID companyId;
    
    @BeforeEach
    void setUp() {
        userQueryService = new UserQueryService(userRepositoryPort, userCachePort, userApplicationMapper, userRepositoryAdapter, metricsPort);
        
        // Criar dados de teste
        userId = UUID.randomUUID();
        codeUser = UUID.randomUUID();
        companyId = UUID.randomUUID();
        
        user = UserTestBuilder.builder()
            .withId(userId)
            .withCodeUser(codeUser)
            .withCompanyId(companyId)
            .asPerson()
            .asActive()
            .buildDomainWithId();
        
        personProfile = PersonProfile.create(
            userId,
            "John Doe",
            "12345678909", // CPF válido
            java.time.LocalDate.of(1990, 1, 1)
        );
        
        userDetailsView = UserTestBuilder.builder()
            .withId(userId)
            .withCodeUser(codeUser)
            .withCompanyId(companyId)
            .asPerson()
            .asActive()
            .buildDetailsView();
    }
    
    // === TESTES DE BUSCA POR ID ===
    
    @Test
    @DisplayName("Deve buscar usuário por ID do cache com sucesso")
    void shouldGetUserByIdFromCacheSuccessfully() {
        // Given
        var query = new UserGetByIdQueryDTO(userId, companyId);
        when(userCachePort.getUserByIdFromCache(userId.toString())).thenReturn(userDetailsView);
        
        // When
        var result = userQueryService.getById(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        verify(userCachePort).getUserByIdFromCache(userId.toString());
        verify(userRepositoryPort, never()).findById(any());
        verify(userApplicationMapper, never()).toGetByIdView(any(), any());
        verify(userCachePort, never()).cacheUserById(anyString(), any());
    }
    
    @Test
    @DisplayName("Deve buscar usuário por ID do banco quando não está no cache")
    void shouldGetUserByIdFromDatabaseWhenNotInCache() {
        // Given
        var query = new UserGetByIdQueryDTO(userId, companyId);
        when(userCachePort.getUserByIdFromCache(userId.toString())).thenReturn(null);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(userRepositoryAdapter.findPersonProfileByUserId(any(UUID.class))).thenReturn(personProfile);
        when(userApplicationMapper.toGetByIdView(eq(user), any(UserProfile.class))).thenReturn(userDetailsView);
        
        // When
        var result = userQueryService.getById(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        verify(userCachePort).getUserByIdFromCache(userId.toString());
        verify(userRepositoryPort).findById(userId);
        verify(userRepositoryAdapter).findPersonProfileByUserId(any(UUID.class));
        verify(userApplicationMapper).toGetByIdView(eq(user), any(UserProfile.class));
        verify(userCachePort).cacheUserById(userId.toString(), userDetailsView);
    }
    
    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não existe")
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        // Given
        var query = new UserGetByIdQueryDTO(userId, companyId);
        when(userCachePort.getUserByIdFromCache(userId.toString())).thenReturn(null);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> userQueryService.getById(query));
        assertEquals("Usuário não encontrado: " + userId, exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getContext().containsKey("userId"));
        assertEquals(userId, exception.getContext().get("userId"));
    }
    
    // === TESTES DE BUSCA POR CODE USER ===
    
    @Test
    @DisplayName("Deve buscar usuário por codeUser do cache com sucesso")
    void shouldGetUserByCodeUserFromCacheSuccessfully() {
        // Given
        var query = new UserGetByCodeUserQueryDTO(codeUser, companyId);
        when(userCachePort.getUserByCodeFromCache(codeUser.toString())).thenReturn(userDetailsView);
        
        // When
        var result = userQueryService.getByCodeUser(query);
        
        // Then
        assertNotNull(result);
        assertEquals(codeUser, result.codeUser());
        verify(userCachePort).getUserByCodeFromCache(codeUser.toString());
        verify(userRepositoryPort, never()).findByCodeUser(any());
        verify(userApplicationMapper, never()).toByCodeUserView(any(), any());
        verify(userCachePort, never()).cacheUserByCode(anyString(), any());
    }
    
    @Test
    @DisplayName("Deve buscar usuário por codeUser do banco quando não está no cache")
    void shouldGetUserByCodeUserFromDatabaseWhenNotInCache() {
        // Given
        var query = new UserGetByCodeUserQueryDTO(codeUser, companyId);
        when(userCachePort.getUserByCodeFromCache(codeUser.toString())).thenReturn(null);
        when(userRepositoryPort.findByCodeUser(codeUser)).thenReturn(Optional.of(user));
        when(userRepositoryAdapter.findPersonProfileByUserId(any(UUID.class))).thenReturn(personProfile);
        when(userApplicationMapper.toByCodeUserView(eq(user), any(UserProfile.class))).thenReturn(userDetailsView);
        
        // When
        var result = userQueryService.getByCodeUser(query);
        
        // Then
        assertNotNull(result);
        assertEquals(codeUser, result.codeUser());
        verify(userCachePort).getUserByCodeFromCache(codeUser.toString());
        verify(userRepositoryPort).findByCodeUser(codeUser);
        verify(userRepositoryAdapter).findPersonProfileByUserId(any(UUID.class));
        verify(userApplicationMapper).toByCodeUserView(eq(user), any(UserProfile.class));
        verify(userCachePort).cacheUserByCode(codeUser.toString(), userDetailsView);
    }
    
    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não existe por codeUser")
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistByCodeUser() {
        // Given
        var query = new UserGetByCodeUserQueryDTO(codeUser, companyId);
        when(userCachePort.getUserByCodeFromCache(codeUser.toString())).thenReturn(null);
        when(userRepositoryPort.findByCodeUser(codeUser)).thenReturn(Optional.empty());
        
        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> userQueryService.getByCodeUser(query));
        assertEquals("Usuário não encontrado com codeUser: " + codeUser, exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getContext().containsKey("codeUser"));
        assertEquals(codeUser, exception.getContext().get("codeUser"));
    }
    
    // === TESTES DE BUSCA POR EMAIL ===
    
    @Test
    @DisplayName("Deve buscar usuário por email do cache com sucesso")
    void shouldGetUserByEmailFromCacheSuccessfully() {
        // Given
        var email = "test@example.com";
        var query = new UserGetByEmailQueryDTO(email, companyId);
        when(userCachePort.getUserByEmailFromCache(email)).thenReturn(userDetailsView);
        
        // When
        var result = userQueryService.getByEmail(query);
        
        // Then
        assertNotNull(result);
        assertEquals(email, result.email());
        verify(userCachePort).getUserByEmailFromCache(email);
        verify(userRepositoryPort, never()).findByEmail(anyString());
        verify(userApplicationMapper, never()).toByEmailView(any(), any());
        verify(userCachePort, never()).cacheUserByEmail(anyString(), any());
    }
    
    @Test
    @DisplayName("Deve buscar usuário por email do banco quando não está no cache")
    void shouldGetUserByEmailFromDatabaseWhenNotInCache() {
        // Given
        var email = "test@example.com";
        var query = new UserGetByEmailQueryDTO(email, companyId);
        when(userCachePort.getUserByEmailFromCache(email)).thenReturn(null);
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepositoryAdapter.findPersonProfileByUserId(any(UUID.class))).thenReturn(personProfile);
        when(userApplicationMapper.toByEmailView(eq(user), any(UserProfile.class))).thenReturn(userDetailsView);
        
        // When
        var result = userQueryService.getByEmail(query);
        
        // Then
        assertNotNull(result);
        assertEquals(email, result.email());
        verify(userCachePort).getUserByEmailFromCache(email);
        verify(userRepositoryPort).findByEmail(email);
        verify(userRepositoryAdapter).findPersonProfileByUserId(any(UUID.class));
        verify(userApplicationMapper).toByEmailView(eq(user), any(UserProfile.class));
        verify(userCachePort).cacheUserByEmail(anyString(), eq(userDetailsView));
    }
    
    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não existe por email")
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistByEmail() {
        // Given
        var email = "test@example.com";
        var query = new UserGetByEmailQueryDTO(email, companyId);
        when(userCachePort.getUserByEmailFromCache(email)).thenReturn(null);
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());
        
        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> userQueryService.getByEmail(query));
        assertEquals("Usuário não encontrado com email: " + email, exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getContext().containsKey("email"));
        assertEquals(email, exception.getContext().get("email"));
    }
    
    // === TESTES DE VERIFICAÇÃO DE EXISTÊNCIA ===
    
    @Test
    @DisplayName("Deve retornar true quando usuário existe")
    void shouldReturnTrueWhenUserExists() {
        // Given
        when(userRepositoryPort.existsById(userId)).thenReturn(true);
        
        // When
        var result = userQueryService.existsById(userId);
        
        // Then
        assertTrue(result);
        verify(userRepositoryPort).existsById(userId);
    }
    
    @Test
    @DisplayName("Deve retornar false quando usuário não existe")
    void shouldReturnFalseWhenUserDoesNotExist() {
        // Given
        when(userRepositoryPort.existsById(userId)).thenReturn(false);
        
        // When
        var result = userQueryService.existsById(userId);
        
        // Then
        assertFalse(result);
        verify(userRepositoryPort).existsById(userId);
    }
}