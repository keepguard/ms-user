package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.domain.entity.Notify;
import com.keepguard.ms_user.infrastructure.persistence.entity.NotifyJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.NotifyJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.NotifySpringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes para UserNotifyRepositoryAdapter
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Notify Repository Adapter Tests")
class UserNotifyRepositoryAdapterTest {
    
    @Mock
    private NotifySpringRepository springRepository;
    
    @Mock
    private NotifyJpaMapper mapper;
    
    private UserNotifyRepositoryAdapter adapter;
    
    @BeforeEach
    void setUp() {
        adapter = new UserNotifyRepositoryAdapter(springRepository, mapper);
    }
    
    @Test
    @DisplayName("Deve salvar Notify")
    void shouldSaveNotify() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.createDefaults(userId);
        NotifyJpaEntity entity = NotifyJpaEntity.builder().userId(userId).build();
        NotifyJpaEntity savedEntity = NotifyJpaEntity.builder().userId(userId).build();
        Notify savedNotify = Notify.createDefaults(userId);
        
        when(mapper.toEntity(notify)).thenReturn(entity);
        when(springRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedNotify);
        
        // When
        Notify result = adapter.save(notify);
        
        // Then
        assertNotNull(result, "Resultado deve ser não nulo");
        assertEquals(savedNotify, result, "Resultado deve ser o mesmo");
        verify(mapper).toEntity(notify);
        verify(springRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }
    
    @Test
    @DisplayName("Deve buscar Notify por userId")
    void shouldFindNotifyByUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        NotifyJpaEntity entity = NotifyJpaEntity.builder().userId(userId).build();
        Notify notify = Notify.createDefaults(userId);
        
        when(springRepository.findByUserId(userId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(notify);
        
        // When
        Optional<Notify> result = adapter.findByUserId(userId);
        
        // Then
        assertTrue(result.isPresent(), "Resultado deve estar presente");
        assertEquals(notify, result.get(), "Resultado deve ser o mesmo");
        verify(springRepository).findByUserId(userId);
        verify(mapper).toDomain(entity);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando Notify não encontrado")
    void shouldReturnEmptyOptionalWhenNotifyNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        
        when(springRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // When
        Optional<Notify> result = adapter.findByUserId(userId);
        
        // Then
        assertTrue(result.isEmpty(), "Resultado deve estar vazio");
        verify(springRepository).findByUserId(userId);
        verify(mapper, never()).toDomain(any());
    }
    
    @Test
    @DisplayName("Deve buscar Notifies por lista de userIds")
    void shouldFindNotifiesByUserIds() {
        // Given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        List<UUID> userIds = List.of(userId1, userId2);
        
        NotifyJpaEntity entity1 = NotifyJpaEntity.builder().userId(userId1).build();
        NotifyJpaEntity entity2 = NotifyJpaEntity.builder().userId(userId2).build();
        List<NotifyJpaEntity> entities = List.of(entity1, entity2);
        
        Notify notify1 = Notify.createDefaults(userId1);
        Notify notify2 = Notify.createDefaults(userId2);
        
        when(springRepository.findAllByUserIdIn(userIds)).thenReturn(entities);
        when(mapper.toDomain(any(NotifyJpaEntity.class))).thenAnswer(invocation -> {
            NotifyJpaEntity entity = invocation.getArgument(0);
            if (entity.getUserId().equals(userId1)) {
                return notify1;
            } else if (entity.getUserId().equals(userId2)) {
                return notify2;
            }
            return null;
        });
        
        // When
        List<Notify> result = adapter.findAllByUserIdIn(userIds);
        
        // Then
        assertNotNull(result, "Resultado deve ser não nulo");
        assertEquals(2, result.size(), "Resultado deve ter 2 elementos");
        
        // Verificar se os userIds estão presentes no resultado
        List<UUID> resultUserIds = result.stream()
                .map(Notify::getUserId)
                .collect(Collectors.toList());
        
        assertTrue(resultUserIds.contains(userId1), "Resultado deve conter userId1");
        assertTrue(resultUserIds.contains(userId2), "Resultado deve conter userId2");
        
        verify(springRepository).findAllByUserIdIn(userIds);
        verify(mapper, times(2)).toDomain(any(NotifyJpaEntity.class));
    }
    
    @Test
    @DisplayName("Deve deletar Notify por userId")
    void shouldDeleteNotifyByUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // When
        adapter.deleteByUserId(userId);
        
        // Then
        verify(springRepository).deleteByUserId(userId);
    }
    
    @Test
    @DisplayName("Deve deletar Notify")
    void shouldDeleteNotify() {
        // Given
        UUID userId = UUID.randomUUID();
        Notify notify = Notify.createDefaults(userId);
        NotifyJpaEntity entity = NotifyJpaEntity.builder().userId(userId).build();
        
        when(mapper.toEntity(notify)).thenReturn(entity);
        
        // When
        adapter.delete(notify);
        
        // Then
        verify(mapper).toEntity(notify);
        verify(springRepository).delete(entity);
    }
    
    @Test
    @DisplayName("Deve verificar se Notify existe por userId")
    void shouldCheckIfNotifyExistsByUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        
        when(springRepository.existsByUserId(userId)).thenReturn(true);
        
        // When
        boolean result = adapter.existsByUserId(userId);
        
        // Then
        assertTrue(result, "Resultado deve ser true");
        verify(springRepository).existsByUserId(userId);
    }
    
    @Test
    @DisplayName("Deve buscar Notifies por notifyEmail")
    void shouldFindNotifiesByNotifyEmail() {
        // Given
        boolean notifyEmail = true;
        UUID userId = UUID.randomUUID();
        NotifyJpaEntity entity = NotifyJpaEntity.builder().userId(userId).notifyEmail(notifyEmail).build();
        List<NotifyJpaEntity> entities = List.of(entity);
        Notify notify = Notify.createDefaults(userId);
        
        when(springRepository.findAllByNotifyEmail(notifyEmail)).thenReturn(entities);
        when(mapper.toDomain(entity)).thenReturn(notify);
        
        // When
        List<Notify> result = adapter.findAllByNotifyEmail(notifyEmail);
        
        // Then
        assertNotNull(result, "Resultado deve ser não nulo");
        assertEquals(1, result.size(), "Resultado deve ter 1 elemento");
        assertEquals(notify, result.get(0), "Resultado deve ser o mesmo");
        verify(springRepository).findAllByNotifyEmail(notifyEmail);
        verify(mapper).toDomain(entity);
    }
    
    @Test
    @DisplayName("Deve buscar Notifies por notifySms")
    void shouldFindNotifiesByNotifySms() {
        // Given
        boolean notifySms = false;
        UUID userId = UUID.randomUUID();
        NotifyJpaEntity entity = NotifyJpaEntity.builder().userId(userId).notifySms(notifySms).build();
        List<NotifyJpaEntity> entities = List.of(entity);
        Notify notify = Notify.createDefaults(userId);
        
        when(springRepository.findAllByNotifySms(notifySms)).thenReturn(entities);
        when(mapper.toDomain(entity)).thenReturn(notify);
        
        // When
        List<Notify> result = adapter.findAllByNotifySms(notifySms);
        
        // Then
        assertNotNull(result, "Resultado deve ser não nulo");
        assertEquals(1, result.size(), "Resultado deve ter 1 elemento");
        assertEquals(notify, result.get(0), "Resultado deve ser o mesmo");
        verify(springRepository).findAllByNotifySms(notifySms);
        verify(mapper).toDomain(entity);
    }
    
    @Test
    @DisplayName("Deve buscar Notifies por notifyWhatsapp")
    void shouldFindNotifiesByNotifyWhatsapp() {
        // Given
        boolean notifyWhatsapp = true;
        UUID userId = UUID.randomUUID();
        NotifyJpaEntity entity = NotifyJpaEntity.builder().userId(userId).notifyWhatsapp(notifyWhatsapp).build();
        List<NotifyJpaEntity> entities = List.of(entity);
        Notify notify = Notify.createDefaults(userId);
        
        when(springRepository.findAllByNotifyWhatsapp(notifyWhatsapp)).thenReturn(entities);
        when(mapper.toDomain(entity)).thenReturn(notify);
        
        // When
        List<Notify> result = adapter.findAllByNotifyWhatsapp(notifyWhatsapp);
        
        // Then
        assertNotNull(result, "Resultado deve ser não nulo");
        assertEquals(1, result.size(), "Resultado deve ter 1 elemento");
        assertEquals(notify, result.get(0), "Resultado deve ser o mesmo");
        verify(springRepository).findAllByNotifyWhatsapp(notifyWhatsapp);
        verify(mapper).toDomain(entity);
    }
    
    @Test
    @DisplayName("Deve buscar Notifies por notifyPush")
    void shouldFindNotifiesByNotifyPush() {
        // Given
        boolean notifyPush = false;
        UUID userId = UUID.randomUUID();
        NotifyJpaEntity entity = NotifyJpaEntity.builder().userId(userId).notifyPush(notifyPush).build();
        List<NotifyJpaEntity> entities = List.of(entity);
        Notify notify = Notify.createDefaults(userId);
        
        when(springRepository.findAllByNotifyPush(notifyPush)).thenReturn(entities);
        when(mapper.toDomain(entity)).thenReturn(notify);
        
        // When
        List<Notify> result = adapter.findAllByNotifyPush(notifyPush);
        
        // Then
        assertNotNull(result, "Resultado deve ser não nulo");
        assertEquals(1, result.size(), "Resultado deve ter 1 elemento");
        assertEquals(notify, result.get(0), "Resultado deve ser o mesmo");
        verify(springRepository).findAllByNotifyPush(notifyPush);
        verify(mapper).toDomain(entity);
    }
    
    @Test
    @DisplayName("Deve verificar se a classe pode ser instanciada")
    void shouldVerifyClassCanBeInstantiated() {
        // Given
        NotifySpringRepository springRepo = mock(NotifySpringRepository.class);
        NotifyJpaMapper mapper = mock(NotifyJpaMapper.class);
        
        // When
        UserNotifyRepositoryAdapter adapter = new UserNotifyRepositoryAdapter(springRepo, mapper);
        
        // Then
        assertNotNull(adapter, "Adapter deve ser instanciável");
        assertEquals(UserNotifyRepositoryAdapter.class, adapter.getClass(), "Tipo da classe deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar anotações da classe")
    void shouldVerifyClassAnnotations() {
        // Given
        Class<?> adapterClass = UserNotifyRepositoryAdapter.class;
        
        // When & Then
        assertTrue(adapterClass.isAnnotationPresent(org.springframework.stereotype.Repository.class), 
            "Classe deve ter anotação @Repository");
        // Anotações do Lombok não são visíveis em tempo de execução
        // Verificamos se a classe tem construtor com parâmetros (gerado pelo @RequiredArgsConstructor)
        assertTrue(hasConstructorWithParameters(adapterClass, 2), 
            "Deve ter construtor com 2 parâmetros (gerado pelo @RequiredArgsConstructor)");
    }
    
    private boolean hasConstructorWithParameters(Class<?> clazz, int parameterCount) {
        try {
            var constructors = clazz.getDeclaredConstructors();
            for (var constructor : constructors) {
                if (constructor.getParameterCount() == parameterCount) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
