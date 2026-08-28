package com.keepguard.ms_user.application.service.usernotify;

import com.keepguard.ms_user.application.dto.notify.NotifyDetailsViewDTO;
import com.keepguard.ms_user.application.dto.notify.NotifyViewDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByUserIdQueryDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByCodeUserQueryDTO;
import com.keepguard.ms_user.application.mapper.NotifyApplicationMapper;
import com.keepguard.ms_user.application.port.out.cache.NotifyCachePort;
import com.keepguard.ms_user.application.port.out.persistence.UserNotifyRepositoryPort;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.domain.entity.Notify;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserNotifyQueryService
 * Segue o padrão profissional usado no ms-auth
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Notify Query Service Tests")
class UserNotifyQueryServiceTest {
    
    private UserNotifyQueryService userNotifyQueryService;
    
    @Mock
    private UserNotifyRepositoryPort userNotifyRepositoryPort;
    
    @Mock
    private NotifyCachePort notifyCachePort;
    
    @Mock
    private NotifyApplicationMapper notifyApplicationMapper;
    
    @Mock
    private UserRepositoryPort userRepositoryPort;
    
    private Notify notify;
    private NotifyViewDTO notifyView;
    private NotifyDetailsViewDTO notifyDetailsView;
    private User user;
    private UUID userId;
    private UUID codeUser;
    private UUID companyId;
    
    @BeforeEach
    void setUp() {
        userNotifyQueryService = new UserNotifyQueryService(
            userNotifyRepositoryPort, 
            notifyCachePort, 
            notifyApplicationMapper, 
            userRepositoryPort
        );
        
        // Criar dados de teste
        userId = UUID.randomUUID();
        codeUser = UUID.randomUUID();
        companyId = UUID.randomUUID();
        
        notify = Notify.of(
            userId,
            true,  // notifyEmail
            false, // notifySms
            true,  // notifyWhatsapp
            false, // notifyPush
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            1L
        );
        
        notifyView = new NotifyViewDTO(
            UUID.randomUUID(), // id (notify não tem id próprio)
            notify.getUserId(),
            notify.isNotifyEmail(),
            notify.isNotifySms(),
            notify.isNotifyPush(),
            notify.isNotifyWhatsapp(), // marketingNotifications
            notify.getCreatedAt(),
            notify.getUpdatedAt(),
            notify.getVersion()
        );
        
        notifyDetailsView = new NotifyDetailsViewDTO(
            UUID.randomUUID(), // id (notify não tem id próprio)
            notify.getUserId(),
            notify.isNotifyEmail(),
            notify.isNotifySms(),
            notify.isNotifyPush(),
            notify.isNotifyWhatsapp(), // marketingNotifications
            notify.getCreatedAt(),
            notify.getUpdatedAt(),
            notify.getVersion()
        );
        
        user = User.of(
            userId,
            codeUser,
            companyId,
            UUID.randomUUID(), // companyId
            UserTypeEnum.PERSON,
            UserStatusEnum.ACTIVE,
            "test@example.com",
            "+5511999999999",
            "pt-BR",
            "America/Sao_Paulo",
            "https://example.com/avatar.jpg",
            null, // displayHandle
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }
    
    // === TESTES DE BUSCA POR USER ID ===
    
    @Test
    @DisplayName("Deve buscar notificação por userId do cache com sucesso")
    void shouldGetNotifyByUserIdFromCacheSuccessfully() {
        // Given
        when(userRepositoryPort.existsById(userId)).thenReturn(true);
        when(notifyCachePort.getNotifyByUserIdFromCache(userId.toString())).thenReturn(notifyView);
        when(notifyApplicationMapper.toDetailsView(notifyView)).thenReturn(notifyDetailsView);
        
        // When
        var query = new UserNotifyGetByUserIdQueryDTO(userId, companyId);
        var result = userNotifyQueryService.getByUserId(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(userRepositoryPort).existsById(userId);
        verify(notifyCachePort).getNotifyByUserIdFromCache(userId.toString());
        verify(notifyApplicationMapper).toDetailsView(notifyView);
        verify(userNotifyRepositoryPort, never()).findByUserId(any());
    }
    
    @Test
    @DisplayName("Deve buscar notificação por userId do banco quando não está no cache")
    void shouldGetNotifyByUserIdFromDatabaseWhenNotInCache() {
        // Given
        when(userRepositoryPort.existsById(userId)).thenReturn(true);
        when(notifyCachePort.getNotifyByUserIdFromCache(userId.toString())).thenReturn(null);
        when(userNotifyRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(notify));
        when(notifyApplicationMapper.toView(notify)).thenReturn(notifyView);
        when(notifyApplicationMapper.toDetailsView(notify)).thenReturn(notifyDetailsView);
        
        // When
        var query = new UserNotifyGetByUserIdQueryDTO(userId, companyId);
        var result = userNotifyQueryService.getByUserId(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(userRepositoryPort).existsById(userId);
        verify(notifyCachePort).getNotifyByUserIdFromCache(userId.toString());
        verify(userNotifyRepositoryPort).findByUserId(userId);
        verify(notifyApplicationMapper).toView(notify);
        verify(notifyApplicationMapper).toDetailsView(notify);
        verify(notifyCachePort).cacheNotifyByUserId(userId.toString(), notifyView);
    }
    
    @Test
    @DisplayName("Deve criar notificação padrão quando não existe no banco")
    void shouldCreateDefaultNotifyWhenNotExistsInDatabase() {
        // Given
        when(userRepositoryPort.existsById(userId)).thenReturn(true);
        when(notifyCachePort.getNotifyByUserIdFromCache(userId.toString())).thenReturn(null);
        when(userNotifyRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());
        
        var defaultNotify = Notify.createDefaults(userId);
        when(userNotifyRepositoryPort.save(any(Notify.class))).thenReturn(defaultNotify);
        when(notifyApplicationMapper.toView(defaultNotify)).thenReturn(notifyView);
        when(notifyApplicationMapper.toDetailsView(defaultNotify)).thenReturn(notifyDetailsView);
        
        // When
        var query = new UserNotifyGetByUserIdQueryDTO(userId, companyId);
        var result = userNotifyQueryService.getByUserId(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(userRepositoryPort).existsById(userId);
        verify(notifyCachePort).getNotifyByUserIdFromCache(userId.toString());
        verify(userNotifyRepositoryPort).findByUserId(userId);
        verify(userNotifyRepositoryPort).save(any(Notify.class));
        verify(notifyApplicationMapper).toView(defaultNotify);
        verify(notifyApplicationMapper).toDetailsView(defaultNotify);
        verify(notifyCachePort).cacheNotifyByUserId(userId.toString(), notifyView);
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // Given
        when(userRepositoryPort.existsById(userId)).thenReturn(false);
        
        // When & Then
        var query = new UserNotifyGetByUserIdQueryDTO(userId, companyId);
        assertThrows(com.keepguard.ms_user.application.service.exception.NotFoundException.class, 
            () -> userNotifyQueryService.getByUserId(query));
        
        verify(userRepositoryPort).existsById(userId);
        verify(notifyCachePort, never()).getNotifyByUserIdFromCache(any());
        verify(userNotifyRepositoryPort, never()).findByUserId(any());
    }
    
    // === TESTES DE BUSCA POR CODE USER ===
    
    @Test
    @DisplayName("Deve buscar notificação por codeUser do cache com sucesso")
    void shouldGetNotifyByCodeUserFromCacheSuccessfully() {
        // Given
        when(userRepositoryPort.findByCodeUser(codeUser)).thenReturn(Optional.of(user));
        when(notifyCachePort.getNotifyByUserIdFromCache(userId.toString())).thenReturn(notifyView);
        when(notifyApplicationMapper.toDetailsView(notifyView)).thenReturn(notifyDetailsView);
        
        // When
        var query = new UserNotifyGetByCodeUserQueryDTO(codeUser, companyId);
        var result = userNotifyQueryService.getByCodeUser(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(userRepositoryPort).findByCodeUser(codeUser);
        verify(notifyCachePort).getNotifyByUserIdFromCache(userId.toString());
        verify(notifyApplicationMapper).toDetailsView(notifyView);
        verify(userNotifyRepositoryPort, never()).findByUserId(any());
    }
    
    @Test
    @DisplayName("Deve buscar notificação por codeUser do banco quando não está no cache")
    void shouldGetNotifyByCodeUserFromDatabaseWhenNotInCache() {
        // Given
        when(userRepositoryPort.findByCodeUser(codeUser)).thenReturn(Optional.of(user));
        when(notifyCachePort.getNotifyByUserIdFromCache(userId.toString())).thenReturn(null);
        when(userNotifyRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(notify));
        when(notifyApplicationMapper.toView(notify)).thenReturn(notifyView);
        when(notifyApplicationMapper.toDetailsView(notify)).thenReturn(notifyDetailsView);
        
        // When
        var query = new UserNotifyGetByCodeUserQueryDTO(codeUser, companyId);
        var result = userNotifyQueryService.getByCodeUser(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(userRepositoryPort).findByCodeUser(codeUser);
        verify(notifyCachePort).getNotifyByUserIdFromCache(userId.toString());
        verify(userNotifyRepositoryPort).findByUserId(userId);
        verify(notifyApplicationMapper).toView(notify);
        verify(notifyApplicationMapper).toDetailsView(notify);
        verify(notifyCachePort).cacheNotifyByUserId(userId.toString(), notifyView);
    }
    
    @Test
    @DisplayName("Deve criar notificação padrão por codeUser quando não existe no banco")
    void shouldCreateDefaultNotifyByCodeUserWhenNotExistsInDatabase() {
        // Given
        when(userRepositoryPort.findByCodeUser(codeUser)).thenReturn(Optional.of(user));
        when(notifyCachePort.getNotifyByUserIdFromCache(userId.toString())).thenReturn(null);
        when(userNotifyRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());
        
        var defaultNotify = Notify.createDefaults(userId);
        when(userNotifyRepositoryPort.save(any(Notify.class))).thenReturn(defaultNotify);
        when(notifyApplicationMapper.toView(defaultNotify)).thenReturn(notifyView);
        when(notifyApplicationMapper.toDetailsView(defaultNotify)).thenReturn(notifyDetailsView);
        
        // When
        var query = new UserNotifyGetByCodeUserQueryDTO(codeUser, companyId);
        var result = userNotifyQueryService.getByCodeUser(query);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(userRepositoryPort).findByCodeUser(codeUser);
        verify(notifyCachePort).getNotifyByUserIdFromCache(userId.toString());
        verify(userNotifyRepositoryPort).findByUserId(userId);
        verify(userNotifyRepositoryPort).save(any(Notify.class));
        verify(notifyApplicationMapper).toView(defaultNotify);
        verify(notifyApplicationMapper).toDetailsView(defaultNotify);
        verify(notifyCachePort).cacheNotifyByUserId(userId.toString(), notifyView);
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe por codeUser")
    void shouldThrowExceptionWhenUserDoesNotExistByCodeUser() {
        // Given
        when(userRepositoryPort.findByCodeUser(codeUser)).thenReturn(Optional.empty());
        
        // When & Then
        var query = new UserNotifyGetByCodeUserQueryDTO(codeUser, companyId);
        assertThrows(com.keepguard.ms_user.application.service.exception.NotFoundException.class,
            () -> userNotifyQueryService.getByCodeUser(query));
        
        verify(userRepositoryPort).findByCodeUser(codeUser);
        verify(notifyCachePort, never()).getNotifyByUserIdFromCache(any());
        verify(userNotifyRepositoryPort, never()).findByUserId(any());
    }
}
