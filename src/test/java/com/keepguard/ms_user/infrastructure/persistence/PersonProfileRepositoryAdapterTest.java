package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.infrastructure.persistence.entity.PersonProfileJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.PersonProfileJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.PersonProfileSpringRepository;
import com.keepguard.ms_user.infrastructure.persistence.spring.UserSpringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Person Profile Repository Adapter Tests")
class PersonProfileRepositoryAdapterTest {

    @Mock
    private PersonProfileSpringRepository springRepository;

    @Mock
    private PersonProfileJpaMapper mapper;

    @Mock
    private UserSpringRepository userSpringRepository;

    @InjectMocks
    private PersonProfileRepositoryAdapter adapter;

    private UUID userId;
    private UUID companyId;
    private UUID profileId;
    private PersonProfile personProfile;
    private PersonProfileJpaEntity personProfileEntity;
    private UserJpaEntity userEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        personProfile = PersonProfile.of(
                null,
                userId,
                "Rafael Soares",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        userEntity = UserJpaEntity.builder()
                .id(userId)
                .companyId(companyId)
                .build();

        personProfileEntity = PersonProfileJpaEntity.builder()
                .id(profileId)
                .userId(userId)
                .fullName("Rafael Soares")
                .user(userEntity)
                .build();
    }

    @Test
    @DisplayName("Deve inserir PersonProfile quando não existe linha para o user")
    void shouldInsertWhenNoExistingProfile() {
        when(userSpringRepository.getReferenceById(userId)).thenReturn(userEntity);
        when(springRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(mapper.toEntity(personProfile)).thenReturn(personProfileEntity);
        when(springRepository.save(any(PersonProfileJpaEntity.class))).thenReturn(personProfileEntity);
        when(mapper.toDomain(personProfileEntity)).thenReturn(personProfile);

        PersonProfile saved = adapter.save(personProfile);

        assertThat(saved).isNotNull();
        verify(mapper).toEntity(personProfile);
        verify(mapper, never()).applyToExisting(any(), any());
        verify(springRepository).save(personProfileEntity);
    }

    @Test
    @DisplayName("Deve atualizar PersonProfile existente quando id está carregado")
    void shouldUpdateWhenIdPresent() {
        PersonProfile withId = PersonProfile.of(
                profileId,
                userId,
                "Rafael Atualizado",
                "39053344705",
                null, null, null, null, null, null, null, null, null, null, null, null,
                false, null, null, null, null,
                OffsetDateTime.now(), OffsetDateTime.now()
        );
        PersonProfileJpaEntity managed = PersonProfileJpaEntity.builder()
                .id(profileId)
                .userId(userId)
                .fullName("Rafael Soares")
                .user(userEntity)
                .build();

        when(userSpringRepository.getReferenceById(userId)).thenReturn(userEntity);
        when(springRepository.findById(profileId)).thenReturn(Optional.of(managed));
        when(springRepository.save(managed)).thenReturn(managed);
        when(mapper.toDomain(managed)).thenReturn(withId);

        PersonProfile saved = adapter.save(withId);

        assertThat(saved.getId()).isEqualTo(profileId);
        verify(mapper).applyToExisting(withId, managed);
        verify(mapper, never()).toEntity(any());
        ArgumentCaptor<PersonProfileJpaEntity> captor = ArgumentCaptor.forClass(PersonProfileJpaEntity.class);
        verify(springRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(profileId);
        verify(springRepository, never()).findByUserId(any());
    }

    @Test
    @DisplayName("Deve fazer merge por user_id quando id é null mas perfil já existe")
    void shouldMergeByUserIdWhenIdNull() {
        PersonProfileJpaEntity managed = PersonProfileJpaEntity.builder()
                .id(profileId)
                .userId(userId)
                .fullName("Rafael Soares")
                .user(userEntity)
                .build();
        PersonProfile mergedDomain = PersonProfile.of(
                profileId, userId, "Rafael Soares", "39053344705",
                null, null, null, null, null, null, null, null, null, null, null, null,
                false, null, null, null, null,
                OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userSpringRepository.getReferenceById(userId)).thenReturn(userEntity);
        when(springRepository.findByUserId(userId)).thenReturn(Optional.of(managed));
        when(springRepository.save(managed)).thenReturn(managed);
        when(mapper.toDomain(managed)).thenReturn(mergedDomain);

        PersonProfile saved = adapter.save(personProfile);

        assertThat(saved.getId()).isEqualTo(profileId);
        verify(mapper).applyToExisting(personProfile, managed);
        verify(mapper, never()).toEntity(any());
        ArgumentCaptor<PersonProfileJpaEntity> captor = ArgumentCaptor.forClass(PersonProfileJpaEntity.class);
        verify(springRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(profileId);
    }

    @Test
    @DisplayName("Deve verificar se CPF existe na company")
    void shouldCheckIfCpfExistsByCompanyId() {
        String cpf = "12345678909";
        when(springRepository.existsByCpfAndCompanyId(cpf, companyId, userId)).thenReturn(true);

        boolean result = adapter.existsByCpfAndCompanyId(cpf, companyId, userId);

        assertThat(result).isTrue();
        verify(springRepository).existsByCpfAndCompanyId(cpf, companyId, userId);
    }

    @Test
    @DisplayName("Deve retornar false para CPF em branco na checagem por company")
    void shouldReturnFalseWhenCpfIsBlankForCompanyCheck() {
        boolean result = adapter.existsByCpfAndCompanyId("  ", companyId, null);

        assertThat(result).isFalse();
        verify(springRepository, never()).existsByCpfAndCompanyId(any(), any(), any());
    }
}
