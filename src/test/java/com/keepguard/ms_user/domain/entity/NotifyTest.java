package com.keepguard.ms_user.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para entidade de domínio Notify
 */
@DisplayName("Notify Domain Entity Tests")
class NotifyTest {
    
    @Test
    @DisplayName("Deve criar Notify com valores padrão")
    void shouldCreateNotifyWithDefaults() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // When
        Notify notify = Notify.createDefaults(userId);
        
        // Then
        assertNotNull(notify, "Notify deve ser criado");
        assertEquals(userId, notify.getUserId(), "UserId deve ser o mesmo");
        assertTrue(notify.isNotifyEmail(), "Email deve estar habilitado por padrão");
        assertTrue(notify.isNotifySms(), "SMS deve estar habilitado por padrão");
        assertTrue(notify.isNotifyWhatsapp(), "WhatsApp deve estar habilitado por padrão");
        assertTrue(notify.isNotifyPush(), "Push deve estar habilitado por padrão");
        assertNotNull(notify.getCreatedAt(), "CreatedAt deve ser definido");
        assertNotNull(notify.getUpdatedAt(), "UpdatedAt deve ser definido");
        assertEquals(0L, notify.getVersion(), "Version deve ser 0");
    }
    
    @Test
    @DisplayName("Deve criar Notify com valores específicos")
    void shouldCreateNotifyWithSpecificValues() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        // When
        Notify notify = Notify.of(userId, false, true, false, true, now, now, 1L);
        
        // Then
        assertNotNull(notify, "Notify deve ser criado");
        assertEquals(userId, notify.getUserId(), "UserId deve ser o mesmo");
        assertFalse(notify.isNotifyEmail(), "Email deve estar desabilitado");
        assertTrue(notify.isNotifySms(), "SMS deve estar habilitado");
        assertFalse(notify.isNotifyWhatsapp(), "WhatsApp deve estar desabilitado");
        assertTrue(notify.isNotifyPush(), "Push deve estar habilitado");
        assertEquals(now, notify.getCreatedAt(), "CreatedAt deve ser o mesmo");
        assertEquals(now, notify.getUpdatedAt(), "UpdatedAt deve ser o mesmo");
        assertEquals(1L, notify.getVersion(), "Version deve ser 1");
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando userId é null")
    void shouldThrowExceptionWhenUserIdIsNull() {
        // Given & When & Then
        assertThrows(NullPointerException.class, () -> {
            Notify.of(null, true, true, true, true, 
                     OffsetDateTime.now(), OffsetDateTime.now(), 0L);
        }, "Deve lançar NullPointerException quando userId é null");
    }
    
    @Test
    @DisplayName("Deve atualizar preferências de notificação")
    void shouldUpdateNotificationPreferences() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.createDefaults(userId);
        OffsetDateTime originalUpdatedAt = notify.getUpdatedAt();
        
        // When
        notify.updateNotificationPreferences(false, false, true, true);
        
        // Then
        assertFalse(notify.isNotifyEmail(), "Email deve estar desabilitado");
        assertFalse(notify.isNotifySms(), "SMS deve estar desabilitado");
        assertTrue(notify.isNotifyWhatsapp(), "WhatsApp deve estar habilitado");
        assertTrue(notify.isNotifyPush(), "Push deve estar habilitado");
        assertTrue(notify.getUpdatedAt().isAfter(originalUpdatedAt), "UpdatedAt deve ser atualizado");
    }
    
    @Test
    @DisplayName("Deve habilitar todas as notificações")
    void shouldEnableAllNotifications() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.of(userId, false, false, false, false, 
                                 OffsetDateTime.now(), OffsetDateTime.now(), 0L);
        OffsetDateTime originalUpdatedAt = notify.getUpdatedAt();
        
        // When
        notify.enableAllNotifications();
        
        // Then
        assertTrue(notify.isNotifyEmail(), "Email deve estar habilitado");
        assertTrue(notify.isNotifySms(), "SMS deve estar habilitado");
        assertTrue(notify.isNotifyWhatsapp(), "WhatsApp deve estar habilitado");
        assertTrue(notify.isNotifyPush(), "Push deve estar habilitado");
        assertTrue(notify.getUpdatedAt().isAfter(originalUpdatedAt), "UpdatedAt deve ser atualizado");
    }
    
    @Test
    @DisplayName("Deve desabilitar todas as notificações")
    void shouldDisableAllNotifications() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.createDefaults(userId);
        OffsetDateTime originalUpdatedAt = notify.getUpdatedAt();
        
        // When
        notify.disableAllNotifications();
        
        // Then
        assertFalse(notify.isNotifyEmail(), "Email deve estar desabilitado");
        assertFalse(notify.isNotifySms(), "SMS deve estar desabilitado");
        assertFalse(notify.isNotifyWhatsapp(), "WhatsApp deve estar desabilitado");
        assertFalse(notify.isNotifyPush(), "Push deve estar desabilitado");
        assertTrue(notify.getUpdatedAt().isAfter(originalUpdatedAt), "UpdatedAt deve ser atualizado");
    }
    
    @Test
    @DisplayName("Deve verificar se alguma notificação está habilitada")
    void shouldCheckIfAnyNotificationIsEnabled() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // When & Then - Todas habilitadas
        Notify notifyAll = Notify.createDefaults(userId);
        assertTrue(notifyAll.hasAnyNotificationEnabled(), "Deve retornar true quando todas estão habilitadas");
        
        // Quando apenas uma está habilitada
        Notify notifyOne = Notify.of(userId, false, false, true, false, 
                                   OffsetDateTime.now(), OffsetDateTime.now(), 0L);
        assertTrue(notifyOne.hasAnyNotificationEnabled(), "Deve retornar true quando uma está habilitada");
        
        // Quando nenhuma está habilitada
        Notify notifyNone = Notify.of(userId, false, false, false, false, 
                                     OffsetDateTime.now(), OffsetDateTime.now(), 0L);
        assertFalse(notifyNone.hasAnyNotificationEnabled(), "Deve retornar false quando nenhuma está habilitada");
    }
    
    @Test
    @DisplayName("Deve verificar se todas as notificações estão habilitadas")
    void shouldCheckIfAllNotificationsAreEnabled() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // When & Then - Todas habilitadas
        Notify notifyAll = Notify.createDefaults(userId);
        assertTrue(notifyAll.hasAllNotificationsEnabled(), "Deve retornar true quando todas estão habilitadas");
        
        // Quando apenas uma está desabilitada
        Notify notifyPartial = Notify.of(userId, true, true, false, true, 
                                        OffsetDateTime.now(), OffsetDateTime.now(), 0L);
        assertFalse(notifyPartial.hasAllNotificationsEnabled(), "Deve retornar false quando uma está desabilitada");
        
        // Quando nenhuma está habilitada
        Notify notifyNone = Notify.of(userId, false, false, false, false, 
                                     OffsetDateTime.now(), OffsetDateTime.now(), 0L);
        assertFalse(notifyNone.hasAllNotificationsEnabled(), "Deve retornar false quando nenhuma está habilitada");
    }
    
    @Test
    @DisplayName("Deve atualizar updatedAt ao modificar notificações individuais")
    void shouldUpdateUpdatedAtWhenModifyingIndividualNotifications() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.createDefaults(userId);
        OffsetDateTime originalUpdatedAt = notify.getUpdatedAt();
        
        // When
        notify.setNotifyEmail(false);
        
        // Then
        assertFalse(notify.isNotifyEmail(), "Email deve estar desabilitado");
        assertTrue(notify.getUpdatedAt().isAfter(originalUpdatedAt), "UpdatedAt deve ser atualizado");
    }
    
    @Test
    @DisplayName("Deve testar equals e hashCode baseado em userId")
    void shouldTestEqualsAndHashCodeBasedOnUserId() {
        // Given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        
        // When
        Notify notify1 = Notify.createDefaults(userId1);
        Notify notify2 = Notify.createDefaults(userId1);
        Notify notify3 = Notify.createDefaults(userId2);
        
        // Then
        assertEquals(notify1, notify2, "Notifies com mesmo userId devem ser iguais");
        assertNotEquals(notify1, notify3, "Notifies com userIds diferentes devem ser diferentes");
        assertEquals(notify1.hashCode(), notify2.hashCode(), "HashCodes devem ser iguais para mesmo userId");
        assertNotEquals(notify1.hashCode(), notify3.hashCode(), "HashCodes devem ser diferentes para userIds diferentes");
    }
    
    @Test
    @DisplayName("Deve testar toString")
    void shouldTestToString() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.createDefaults(userId);
        
        // When
        String toString = notify.toString();
        
        // Then
        assertNotNull(toString, "ToString não deve ser null");
        assertTrue(toString.contains("Notify{"), "Deve conter nome da classe");
        assertTrue(toString.contains("userId=" + userId), "Deve conter userId");
        assertTrue(toString.contains("notifyEmail=true"), "Deve conter notifyEmail");
        assertTrue(toString.contains("notifySms=true"), "Deve conter notifySms");
        assertTrue(toString.contains("notifyWhatsapp=true"), "Deve conter notifyWhatsapp");
        assertTrue(toString.contains("notifyPush=true"), "Deve conter notifyPush");
    }
    
    @Test
    @DisplayName("Deve testar setters e getters")
    void shouldTestSettersAndGetters() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.createDefaults(userId);
        OffsetDateTime testTime = OffsetDateTime.now();
        
        // When & Then
        notify.setCreatedAt(testTime);
        assertEquals(testTime, notify.getCreatedAt(), "CreatedAt deve ser definido");
        
        notify.setUpdatedAt(testTime);
        assertEquals(testTime, notify.getUpdatedAt(), "UpdatedAt deve ser definido");
        
        notify.setVersion(5L);
        assertEquals(5L, notify.getVersion(), "Version deve ser definido");
    }
}
