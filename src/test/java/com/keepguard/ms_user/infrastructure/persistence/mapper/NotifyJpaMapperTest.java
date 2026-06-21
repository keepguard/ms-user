package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.Notify;
import com.keepguard.ms_user.infrastructure.persistence.entity.NotifyJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para NotifyJpaMapper
 */
@DisplayName("Notify JPA Mapper Tests")
class NotifyJpaMapperTest {
    
    private NotifyJpaMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new NotifyJpaMapper();
    }
    
    @Test
    @DisplayName("Deve converter NotifyJpaEntity para Notify")
    void shouldConvertNotifyJpaEntityToNotify() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
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
        
        // When
        Notify domain = mapper.toDomain(entity);
        
        // Then
        assertNotNull(domain, "Domain deve ser criado");
        assertEquals(entity.getUserId(), domain.getUserId(), "UserId deve ser o mesmo");
        assertEquals(entity.isNotifyEmail(), domain.isNotifyEmail(), "NotifyEmail deve ser o mesmo");
        assertEquals(entity.isNotifySms(), domain.isNotifySms(), "NotifySms deve ser o mesmo");
        assertEquals(entity.isNotifyWhatsapp(), domain.isNotifyWhatsapp(), "NotifyWhatsapp deve ser o mesmo");
        assertEquals(entity.isNotifyPush(), domain.isNotifyPush(), "NotifyPush deve ser o mesmo");
        assertEquals(entity.getCreatedAt(), domain.getCreatedAt(), "CreatedAt deve ser o mesmo");
        assertEquals(entity.getUpdatedAt(), domain.getUpdatedAt(), "UpdatedAt deve ser o mesmo");
        assertEquals(entity.getVersion(), domain.getVersion(), "Version deve ser o mesmo");
    }
    
    @Test
    @DisplayName("Deve converter Notify para NotifyJpaEntity")
    void shouldConvertNotifyToNotifyJpaEntity() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        Notify domain = Notify.of(userId, true, false, true, false, now, now, 1L);
        
        // When
        NotifyJpaEntity entity = mapper.toEntity(domain);
        
        // Then
        assertNotNull(entity, "Entity deve ser criada");
        assertEquals(domain.getUserId(), entity.getUserId(), "UserId deve ser o mesmo");
        assertEquals(domain.isNotifyEmail(), entity.isNotifyEmail(), "NotifyEmail deve ser o mesmo");
        assertEquals(domain.isNotifySms(), entity.isNotifySms(), "NotifySms deve ser o mesmo");
        assertEquals(domain.isNotifyWhatsapp(), entity.isNotifyWhatsapp(), "NotifyWhatsapp deve ser o mesmo");
        assertEquals(domain.isNotifyPush(), entity.isNotifyPush(), "NotifyPush deve ser o mesmo");
        assertEquals(domain.getCreatedAt(), entity.getCreatedAt(), "CreatedAt deve ser o mesmo");
        assertEquals(domain.getUpdatedAt(), entity.getUpdatedAt(), "UpdatedAt deve ser o mesmo");
        assertEquals(domain.getVersion(), entity.getVersion(), "Version deve ser o mesmo");
    }
    
    @Test
    @DisplayName("Deve retornar null quando NotifyJpaEntity é null")
    void shouldReturnNullWhenNotifyJpaEntityIsNull() {
        // Given
        NotifyJpaEntity entity = null;
        
        // When
        Notify domain = mapper.toDomain(entity);
        
        // Then
        assertNull(domain, "Domain deve ser null");
    }
    
    @Test
    @DisplayName("Deve retornar null quando Notify é null")
    void shouldReturnNullWhenNotifyIsNull() {
        // Given
        Notify domain = null;
        
        // When
        NotifyJpaEntity entity = mapper.toEntity(domain);
        
        // Then
        assertNull(entity, "Entity deve ser null");
    }
    
    @Test
    @DisplayName("Deve converter com valores padrão")
    void shouldConvertWithDefaultValues() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify domain = Notify.createDefaults(userId);
        
        // When
        NotifyJpaEntity entity = mapper.toEntity(domain);
        Notify convertedBack = mapper.toDomain(entity);
        
        // Then
        assertNotNull(entity, "Entity deve ser criada");
        assertNotNull(convertedBack, "Converted back deve ser criado");
        assertEquals(domain.getUserId(), convertedBack.getUserId(), "UserId deve ser o mesmo");
        assertEquals(domain.isNotifyEmail(), convertedBack.isNotifyEmail(), "NotifyEmail deve ser o mesmo");
        assertEquals(domain.isNotifySms(), convertedBack.isNotifySms(), "NotifySms deve ser o mesmo");
        assertEquals(domain.isNotifyWhatsapp(), convertedBack.isNotifyWhatsapp(), "NotifyWhatsapp deve ser o mesmo");
        assertEquals(domain.isNotifyPush(), convertedBack.isNotifyPush(), "NotifyPush deve ser o mesmo");
    }
    
    @Test
    @DisplayName("Deve converter com valores customizados")
    void shouldConvertWithCustomValues() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        Notify domain = Notify.of(userId, false, true, false, true, now, now, 5L);
        
        // When
        NotifyJpaEntity entity = mapper.toEntity(domain);
        Notify convertedBack = mapper.toDomain(entity);
        
        // Then
        assertNotNull(entity, "Entity deve ser criada");
        assertNotNull(convertedBack, "Converted back deve ser criado");
        assertEquals(domain.getUserId(), convertedBack.getUserId(), "UserId deve ser o mesmo");
        assertEquals(domain.isNotifyEmail(), convertedBack.isNotifyEmail(), "NotifyEmail deve ser o mesmo");
        assertEquals(domain.isNotifySms(), convertedBack.isNotifySms(), "NotifySms deve ser o mesmo");
        assertEquals(domain.isNotifyWhatsapp(), convertedBack.isNotifyWhatsapp(), "NotifyWhatsapp deve ser o mesmo");
        assertEquals(domain.isNotifyPush(), convertedBack.isNotifyPush(), "NotifyPush deve ser o mesmo");
        assertEquals(domain.getCreatedAt(), convertedBack.getCreatedAt(), "CreatedAt deve ser o mesmo");
        assertEquals(domain.getUpdatedAt(), convertedBack.getUpdatedAt(), "UpdatedAt deve ser o mesmo");
        assertEquals(domain.getVersion(), convertedBack.getVersion(), "Version deve ser o mesmo");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe pode ser instanciada")
    void shouldVerifyClassCanBeInstantiated() {
        // Given & When
        NotifyJpaMapper mapper = new NotifyJpaMapper();
        
        // Then
        assertNotNull(mapper, "Mapper deve ser instanciável");
        assertEquals(NotifyJpaMapper.class, mapper.getClass(), "Tipo da classe deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar anotações da classe")
    void shouldVerifyClassAnnotations() {
        // Given
        Class<?> mapperClass = NotifyJpaMapper.class;
        
        // When & Then
        assertTrue(mapperClass.isAnnotationPresent(org.springframework.stereotype.Component.class), 
            "Classe deve ter anotação @Component");
    }
}
