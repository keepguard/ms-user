package com.keepguard.ms_user.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para NotifyJpaEntity
 */
@DisplayName("Notify JPA Entity Tests")
class NotifyJpaEntityTest {
    
    @Test
    @DisplayName("Deve criar NotifyJpaEntity com builder")
    void shouldCreateNotifyJpaEntityWithBuilder() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        // When
        NotifyJpaEntity entity = NotifyJpaEntity.builder()
                .userId(userId)
                .notifyEmail(true)
                .notifySms(false)
                .notifyWhatsapp(true)
                .notifyPush(false)
                .createdAt(now)
                .updatedAt(now)
                .version(1L)
                .build();
        
        // Then
        assertNotNull(entity, "Entity deve ser criada");
        assertEquals(userId, entity.getUserId(), "UserId deve ser o mesmo");
        assertTrue(entity.isNotifyEmail(), "Email deve estar habilitado");
        assertFalse(entity.isNotifySms(), "SMS deve estar desabilitado");
        assertTrue(entity.isNotifyWhatsapp(), "WhatsApp deve estar habilitado");
        assertFalse(entity.isNotifyPush(), "Push deve estar desabilitado");
        assertEquals(now, entity.getCreatedAt(), "CreatedAt deve ser o mesmo");
        assertEquals(now, entity.getUpdatedAt(), "UpdatedAt deve ser o mesmo");
        assertEquals(1L, entity.getVersion(), "Version deve ser 1");
    }
    
    @Test
    @DisplayName("Deve criar NotifyJpaEntity com construtor padrão")
    void shouldCreateNotifyJpaEntityWithDefaultConstructor() {
        // Given & When
        NotifyJpaEntity entity = new NotifyJpaEntity();
        
        // Then
        assertNotNull(entity, "Entity deve ser criada");
        assertTrue(entity.isNotifyEmail(), "Email deve estar habilitado por padrão");
        assertTrue(entity.isNotifySms(), "SMS deve estar habilitado por padrão");
        assertTrue(entity.isNotifyWhatsapp(), "WhatsApp deve estar habilitado por padrão");
        assertTrue(entity.isNotifyPush(), "Push deve estar habilitado por padrão");
    }
    
    @Test
    @DisplayName("Deve criar NotifyJpaEntity com construtor com parâmetros")
    void shouldCreateNotifyJpaEntityWithParameterizedConstructor() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        
        // When
        NotifyJpaEntity entity = new NotifyJpaEntity(
                userId, true, false, true, false, now, now, 1L
        );
        
        // Then
        assertNotNull(entity, "Entity deve ser criada");
        assertEquals(userId, entity.getUserId(), "UserId deve ser o mesmo");
        assertTrue(entity.isNotifyEmail(), "Email deve estar habilitado");
        assertFalse(entity.isNotifySms(), "SMS deve estar desabilitado");
        assertTrue(entity.isNotifyWhatsapp(), "WhatsApp deve estar habilitado");
        assertFalse(entity.isNotifyPush(), "Push deve estar desabilitado");
        assertEquals(now, entity.getCreatedAt(), "CreatedAt deve ser o mesmo");
        assertEquals(now, entity.getUpdatedAt(), "UpdatedAt deve ser o mesmo");
        assertEquals(1L, entity.getVersion(), "Version deve ser 1");
    }
    
    @Test
    @DisplayName("Deve testar setters e getters")
    void shouldTestSettersAndGetters() {
        // Given
        NotifyJpaEntity entity = new NotifyJpaEntity();
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        // When
        entity.setUserId(userId);
        entity.setNotifyEmail(false);
        entity.setNotifySms(true);
        entity.setNotifyWhatsapp(false);
        entity.setNotifyPush(true);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setVersion(2L);
        
        // Then
        assertEquals(userId, entity.getUserId(), "UserId deve ser definido");
        assertFalse(entity.isNotifyEmail(), "Email deve estar desabilitado");
        assertTrue(entity.isNotifySms(), "SMS deve estar habilitado");
        assertFalse(entity.isNotifyWhatsapp(), "WhatsApp deve estar desabilitado");
        assertTrue(entity.isNotifyPush(), "Push deve estar habilitado");
        assertEquals(now, entity.getCreatedAt(), "CreatedAt deve ser definido");
        assertEquals(now, entity.getUpdatedAt(), "UpdatedAt deve ser definido");
        assertEquals(2L, entity.getVersion(), "Version deve ser definido");
    }
    
    @Test
    @DisplayName("Deve testar equals e hashCode")
    void shouldTestEqualsAndHashCode() {
        // Given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        
        // When
        NotifyJpaEntity entity1 = NotifyJpaEntity.builder().userId(userId1).build();
        NotifyJpaEntity entity2 = NotifyJpaEntity.builder().userId(userId1).build();
        NotifyJpaEntity entity3 = NotifyJpaEntity.builder().userId(userId2).build();
        
        // Then
        assertEquals(entity1, entity2, "Entities com mesmo userId devem ser iguais");
        assertNotEquals(entity1, entity3, "Entities com userIds diferentes devem ser diferentes");
        assertEquals(entity1.hashCode(), entity2.hashCode(), "HashCodes devem ser iguais para mesmo userId");
        assertNotEquals(entity1.hashCode(), entity3.hashCode(), "HashCodes devem ser diferentes para userIds diferentes");
    }
    
    @Test
    @DisplayName("Deve testar toString")
    void shouldTestToString() {
        // Given
        UUID userId = UUID.randomUUID();
        NotifyJpaEntity entity = NotifyJpaEntity.builder()
                .userId(userId)
                .notifyEmail(true)
                .notifySms(false)
                .notifyWhatsapp(true)
                .notifyPush(false)
                .build();
        
        // When
        String toString = entity.toString();
        
        // Then
        assertNotNull(toString, "ToString não deve ser null");
        assertTrue(toString.contains("NotifyJpaEntity"), "Deve conter nome da classe");
        assertTrue(toString.contains("userId=" + userId), "Deve conter userId");
        assertTrue(toString.contains("notifyEmail=true"), "Deve conter notifyEmail");
        assertTrue(toString.contains("notifySms=false"), "Deve conter notifySms");
        assertTrue(toString.contains("notifyWhatsapp=true"), "Deve conter notifyWhatsapp");
        assertTrue(toString.contains("notifyPush=false"), "Deve conter notifyPush");
    }
    
    @Test
    @DisplayName("Deve verificar anotações JPA")
    void shouldVerifyJpaAnnotations() {
        // Given
        Class<?> entityClass = NotifyJpaEntity.class;
        
        // When & Then
        assertTrue(entityClass.isAnnotationPresent(jakarta.persistence.Entity.class), 
            "Deve ter anotação @Entity");
        assertTrue(entityClass.isAnnotationPresent(jakarta.persistence.Table.class), 
            "Deve ter anotação @Table");
        // Anotações do Lombok não são visíveis em tempo de execução
        // Verificamos se a classe tem os métodos gerados pelo Lombok
        assertTrue(hasMethod(entityClass, "getUserId"), "Deve ter método getUserId");
        assertTrue(hasMethod(entityClass, "setUserId", UUID.class), "Deve ter método setUserId");
        assertTrue(hasMethod(entityClass, "equals", Object.class), "Deve ter método equals");
        assertTrue(hasMethod(entityClass, "hashCode"), "Deve ter método hashCode");
        assertTrue(hasMethod(entityClass, "toString"), "Deve ter método toString");
    }
    
    private boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            clazz.getMethod(methodName, paramTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    @Test
    @DisplayName("Deve verificar configuração da tabela")
    void shouldVerifyTableConfiguration() {
        // Given
        jakarta.persistence.Table tableAnnotation = NotifyJpaEntity.class.getAnnotation(jakarta.persistence.Table.class);
        
        // When & Then
        assertNotNull(tableAnnotation, "Anotação @Table deve existir");
        assertEquals("user_notify", tableAnnotation.name(), "Nome da tabela deve ser user_notify");
    }
    
    @Test
    @DisplayName("Deve testar prePersist")
    void shouldTestPrePersist() {
        // Given
        NotifyJpaEntity entity = new NotifyJpaEntity();
        entity.setUserId(UUID.randomUUID());
        entity.setVersion(null);
        
        // When
        entity.prePersist();
        
        // Then
        assertNotNull(entity.getCreatedAt(), "CreatedAt deve ser definido no prePersist");
        assertNotNull(entity.getUpdatedAt(), "UpdatedAt deve ser definido no prePersist");
        assertEquals(0L, entity.getVersion(), "Version deve ser 0 quando null");
    }
    
    // Teste removido - método preUpdate() foi removido da entidade
    // @Test
    // @DisplayName("Deve testar preUpdate")
    // void shouldTestPreUpdate() {
    //     // Given
    //     NotifyJpaEntity entity = new NotifyJpaEntity();
    //     entity.setUserId(UUID.randomUUID());
    //     OffsetDateTime originalUpdatedAt = OffsetDateTime.now().minusHours(1);
    //     entity.setUpdatedAt(originalUpdatedAt);
    //     
    //     // When
    //     entity.preUpdate();
    //     
    //     // Then
    //     assertTrue(entity.getUpdatedAt().isAfter(originalUpdatedAt), 
    //         "UpdatedAt deve ser atualizado no preUpdate");
    // }
}
