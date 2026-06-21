package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchCriteriaDTO;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.UserJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.UserSpringRepository;
import com.keepguard.ms_user.test.builder.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para UserRepositoryAdapter
 * Testa operações de persistência com mocks JPA
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Repository Adapter Tests")
class UserRepositoryAdapterTest {
    
    @Mock
    private UserSpringRepository springRepository;
    
    @Mock
    private UserJpaMapper mapper;
    
    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;
    
    private User user;
    private UserJpaEntity userJpaEntity;
    private UserSearchCriteriaDTO searchCriteria;
    private Page<UserJpaEntity> pageResult;
    private UUID userId;
    private UUID codeUser;
    private UUID companyId;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        codeUser = UUID.randomUUID();
        companyId = UUID.randomUUID();
        
        // Criar objetos de teste usando builder
        user = UserTestBuilder.builder()
            .withId(userId)
            .withCodeUser(codeUser)
            .withCompanyId(companyId)
            .asPerson()
            .asActive()
            .buildDomain();
        
        userJpaEntity = UserTestBuilder.builder()
            .withId(userId)
            .withCodeUser(codeUser)
            .withCompanyId(companyId)
            .asPerson()
            .asActive()
            .buildJpaEntity();
        
        searchCriteria = new UserSearchCriteriaDTO(
            "test@example.com",
            companyId,
            UserTypeEnum.PERSON,
            UserStatusEnum.ACTIVE,
            0,
            10,
            List.of("email"),
            "ASC"
        );
        
        pageResult = new PageImpl<>(
            List.of(userJpaEntity),
            PageRequest.of(0, 10),
            1L
        );
    }
    
    // === TESTES DE SAVE ===
    
    @Test
    @DisplayName("Deve salvar usuário com sucesso")
    void shouldSaveUserSuccessfully() {
        // Given
        when(mapper.toEntity(user)).thenReturn(userJpaEntity);
        when(springRepository.save(userJpaEntity)).thenReturn(userJpaEntity);
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        User result = userRepositoryAdapter.save(user);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        
        verify(mapper).toEntity(user);
        verify(springRepository).save(userJpaEntity);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    @Test
    @DisplayName("Deve salvar usuário com dados diferentes")
    void shouldSaveUserWithDifferentData() {
        // Given
        User newUser = UserTestBuilder.builder()
            .withId(UUID.randomUUID())
            .withCodeUser(UUID.randomUUID())
            .withCompanyId(UUID.randomUUID())
            .asCompany()
            .asPending()
            .buildDomain();
        
        UserJpaEntity newEntity = UserTestBuilder.builder()
            .withId(newUser.getId())
            .withCodeUser(newUser.getCodeUser())
            .withCompanyId(newUser.getCompanyId())
            .asCompany()
            .asPending()
            .buildJpaEntity();
        
        when(mapper.toEntity(newUser)).thenReturn(newEntity);
        when(springRepository.save(newEntity)).thenReturn(newEntity);
        when(mapper.toDomain(newEntity)).thenReturn(newUser);
        
        // When
        User result = userRepositoryAdapter.save(newUser);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(newUser.getId());
        assertThat(result.getType()).isEqualTo(UserTypeEnum.COMPANY);
        assertThat(result.getStatus()).isEqualTo(UserStatusEnum.PENDING);
        
        verify(mapper).toEntity(newUser);
        verify(springRepository).save(newEntity);
        verify(mapper).toDomain(newEntity);
    }
    
    // === TESTES DE FIND BY ID ===
    
    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void shouldFindUserByIdSuccessfully() {
        // Given
        when(springRepository.findByIdWithRelations(userId)).thenReturn(Optional.of(userJpaEntity));
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        Optional<User> result = userRepositoryAdapter.findById(userId);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        
        verify(springRepository).findByIdWithRelations(userId);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não existe por ID")
    void shouldReturnEmptyOptionalWhenUserNotFoundById() {
        // Given
        when(springRepository.findByIdWithRelations(userId)).thenReturn(Optional.empty());
        
        // When
        Optional<User> result = userRepositoryAdapter.findById(userId);
        
        // Then
        assertThat(result).isEmpty();
        
        verify(springRepository).findByIdWithRelations(userId);
        verify(mapper, never()).toDomain(any(UserJpaEntity.class));
    }
    
    // === TESTES DE FIND ALL ===
    
    @Test
    @DisplayName("Deve buscar todos os usuários com sucesso")
    void shouldFindAllUsersSuccessfully() {
        // Given
        List<UserJpaEntity> entities = List.of(userJpaEntity, UserTestBuilder.builder().buildJpaEntity());
        List<User> users = List.of(user, UserTestBuilder.builder().buildDomain());
        
        when(springRepository.findAllWithRelations()).thenReturn(entities);
        when(mapper.toDomain(any(UserJpaEntity.class))).thenReturn(user, UserTestBuilder.builder().buildDomain());
        
        // When
        List<User> result = userRepositoryAdapter.findAll();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        verify(springRepository).findAllWithRelations();
        verify(mapper, times(2)).toDomain(any(UserJpaEntity.class));
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void shouldReturnEmptyListWhenNoUsers() {
        // Given
        when(springRepository.findAllWithRelations()).thenReturn(List.of());
        
        // When
        List<User> result = userRepositoryAdapter.findAll();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(springRepository).findAllWithRelations();
        verify(mapper, never()).toDomain(any(UserJpaEntity.class));
    }
    
    // === TESTES DE DELETE ===
    
    @Test
    @DisplayName("Deve deletar usuário por ID com sucesso")
    void shouldDeleteUserByIdSuccessfully() {
        // When
        userRepositoryAdapter.deleteById(userId);
        
        // Then
        verify(springRepository).deleteById(userId);
    }
    
    @Test
    @DisplayName("Deve deletar usuário por entidade com sucesso")
    void shouldDeleteUserByEntitySuccessfully() {
        // Given
        when(mapper.toEntity(user)).thenReturn(userJpaEntity);
        
        // When
        userRepositoryAdapter.delete(user);
        
        // Then
        verify(mapper).toEntity(user);
        verify(springRepository).delete(userJpaEntity);
    }
    
    // === TESTES DE FIND BY CODE USER ===
    
    @Test
    @DisplayName("Deve buscar usuário por codeUser com sucesso")
    void shouldFindUserByCodeUserSuccessfully() {
        // Given
        when(springRepository.findByCodeUser(codeUser)).thenReturn(Optional.of(userJpaEntity));
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        Optional<User> result = userRepositoryAdapter.findByCodeUser(codeUser);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCodeUser()).isEqualTo(codeUser);
        
        verify(springRepository).findByCodeUser(codeUser);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não existe por codeUser")
    void shouldReturnEmptyOptionalWhenUserNotFoundByCodeUser() {
        // Given
        when(springRepository.findByCodeUser(codeUser)).thenReturn(Optional.empty());
        
        // When
        Optional<User> result = userRepositoryAdapter.findByCodeUser(codeUser);
        
        // Then
        assertThat(result).isEmpty();
        
        verify(springRepository).findByCodeUser(codeUser);
        verify(mapper, never()).toDomain(any(UserJpaEntity.class));
    }
    
    // === TESTES DE FIND BY EMAIL ===
    
    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void shouldFindUserByEmailSuccessfully() {
        // Given
        String email = "test@example.com";
        when(springRepository.findByEmail(email)).thenReturn(Optional.of(userJpaEntity));
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        Optional<User> result = userRepositoryAdapter.findByEmail(email);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        
        verify(springRepository).findByEmail(email);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não existe por email")
    void shouldReturnEmptyOptionalWhenUserNotFoundByEmail() {
        // Given
        String email = "test@example.com";
        when(springRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // When
        Optional<User> result = userRepositoryAdapter.findByEmail(email);
        
        // Then
        assertThat(result).isEmpty();
        
        verify(springRepository).findByEmail(email);
        verify(mapper, never()).toDomain(any(UserJpaEntity.class));
    }
    
    // === TESTES DE FIND BY COMPANY ID ===
    
    @Test
    @DisplayName("Deve buscar usuários por companyId com sucesso")
    void shouldFindUsersByCompanyIdSuccessfully() {
        // Given
        List<UserJpaEntity> entities = List.of(userJpaEntity);
        when(springRepository.findAllByCompanyId(companyId)).thenReturn(entities);
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        List<User> result = userRepositoryAdapter.findAllByCompanyId(companyId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompanyId()).isEqualTo(companyId);
        
        verify(springRepository).findAllByCompanyId(companyId);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    // === TESTES DE FIND BY STATUS ===
    
    @Test
    @DisplayName("Deve buscar usuários por status com sucesso")
    void shouldFindUsersByStatusSuccessfully() {
        // Given
        UserStatusEnum status = UserStatusEnum.ACTIVE;
        List<UserJpaEntity> entities = List.of(userJpaEntity);
        when(springRepository.findAllByStatus(status)).thenReturn(entities);
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        List<User> result = userRepositoryAdapter.findAllByStatus(status);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(user.getStatus());
        
        verify(springRepository).findAllByStatus(status);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    // === TESTES DE FIND BY TYPE ===
    
    @Test
    @DisplayName("Deve buscar usuários por tipo com sucesso")
    void shouldFindUsersByTypeSuccessfully() {
        // Given
        UserTypeEnum type = UserTypeEnum.PERSON;
        List<UserJpaEntity> entities = List.of(userJpaEntity);
        when(springRepository.findAllByType(type)).thenReturn(entities);
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        List<User> result = userRepositoryAdapter.findAllByType(type);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(type);
        
        verify(springRepository).findAllByType(type);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    // === TESTES DE EXISTS ===
    
    @Test
    @DisplayName("Deve verificar se usuário existe por email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        String email = "test@example.com";
        when(springRepository.existsByEmail(email)).thenReturn(true);
        
        // When
        boolean result = userRepositoryAdapter.existsByEmail(email);
        
        // Then
        assertThat(result).isTrue();
        verify(springRepository).existsByEmail(email);
    }
    
    @Test
    @DisplayName("Deve verificar se usuário não existe por email")
    void shouldCheckIfUserNotExistsByEmail() {
        // Given
        String email = "nonexistent@example.com";
        when(springRepository.existsByEmail(email)).thenReturn(false);
        
        // When
        boolean result = userRepositoryAdapter.existsByEmail(email);
        
        // Then
        assertThat(result).isFalse();
        verify(springRepository).existsByEmail(email);
    }
    
    @Test
    @DisplayName("Deve verificar se usuário existe por codeUser")
    void shouldCheckIfUserExistsByCodeUser() {
        // Given
        when(springRepository.existsByCodeUser(codeUser)).thenReturn(true);
        
        // When
        boolean result = userRepositoryAdapter.existsByCodeUser(codeUser);
        
        // Then
        assertThat(result).isTrue();
        verify(springRepository).existsByCodeUser(codeUser);
    }
    
    @Test
    @DisplayName("Deve verificar se usuário existe por ID")
    void shouldCheckIfUserExistsById() {
        // Given
        when(springRepository.existsById(userId)).thenReturn(true);
        
        // When
        boolean result = userRepositoryAdapter.existsById(userId);
        
        // Then
        assertThat(result).isTrue();
        verify(springRepository).existsById(userId);
    }
    
    // === TESTES DE FIND BY EMAIL CONTAINING ===
    
    @Test
    @DisplayName("Deve buscar usuários por email contendo texto com sucesso")
    void shouldFindUsersByEmailContainingSuccessfully() {
        // Given
        String email = "test";
        List<UserJpaEntity> entities = List.of(userJpaEntity);
        when(springRepository.findByEmailContainingIgnoreCase(email)).thenReturn(entities);
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        List<User> result = userRepositoryAdapter.findByEmailContainingIgnoreCase(email);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        
        verify(springRepository).findByEmailContainingIgnoreCase(email);
        verify(mapper).toDomain(userJpaEntity);
    }
    
    // === TESTES DE SEARCH ===
    
    @Test
    @DisplayName("Deve buscar usuários com critérios com sucesso")
    void shouldSearchUsersWithCriteriaSuccessfully() {
        // Given
        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        PageResultDTO<User> result = userRepositoryAdapter.search(searchCriteria);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
        
        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper).toDomain(userJpaEntity);
    }
    
    @Test
    @DisplayName("Deve buscar usuários com critérios vazios")
    void shouldSearchUsersWithEmptyCriteria() {
        // Given
        UserSearchCriteriaDTO emptyCriteria = new UserSearchCriteriaDTO(
            null, null, null, null, 0, 10, null, null
        );
        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(mapper.toDomain(userJpaEntity)).thenReturn(user);
        
        // When
        PageResultDTO<User> result = userRepositoryAdapter.search(emptyCriteria);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        
        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
    }
    
    // === TESTES DE COUNT ===
    
    @Test
    @DisplayName("Deve contar total de usuários com sucesso")
    void shouldCountAllUsersSuccessfully() {
        // Given
        when(springRepository.count()).thenReturn(5L);
        
        // When
        long result = userRepositoryAdapter.count();
        
        // Then
        assertThat(result).isEqualTo(5L);
        verify(springRepository).count();
    }
    
    
    @Test
    @DisplayName("Deve contar usuários por companyId com sucesso")
    void shouldCountUsersByCompanyIdSuccessfully() {
        // Given
        when(springRepository.countByCompanyId(companyId)).thenReturn(2L);
        
        // When
        long result = userRepositoryAdapter.countByCompanyId(companyId);
        
        // Then
        assertThat(result).isEqualTo(2L);
        verify(springRepository).countByCompanyId(companyId);
    }
    
    
    // === TESTES DE FIND ALL BY ID IN ===
    
    @Test
    @DisplayName("Deve buscar usuários por lista de IDs com sucesso")
    void shouldFindUsersByIdsSuccessfully() {
        // Given
        List<UUID> userIds = List.of(userId, UUID.randomUUID());
        List<UserJpaEntity> entities = List.of(userJpaEntity, UserTestBuilder.builder().buildJpaEntity());
        List<User> users = List.of(user, UserTestBuilder.builder().buildDomain());
        
        when(springRepository.findAllById(userIds)).thenReturn(entities);
        when(mapper.toDomain(any(UserJpaEntity.class))).thenReturn(user, UserTestBuilder.builder().buildDomain());
        
        // When
        List<User> result = userRepositoryAdapter.findAllByIdIn(userIds);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        verify(springRepository).findAllById(userIds);
        verify(mapper, times(2)).toDomain(any(UserJpaEntity.class));
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum usuário encontrado por IDs")
    void shouldReturnEmptyListWhenNoUsersFoundByIds() {
        // Given
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(springRepository.findAllById(userIds)).thenReturn(List.of());
        
        // When
        List<User> result = userRepositoryAdapter.findAllByIdIn(userIds);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(springRepository).findAllById(userIds);
        verify(mapper, never()).toDomain(any(UserJpaEntity.class));
    }
}
