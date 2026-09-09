package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.infrastructure.persistence.entity.CompanyProfileJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.CompanyProfileJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.CompanyProfileSpringRepository;
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
@DisplayName("Company Profile Repository Adapter Tests")
class CompanyProfileRepositoryAdapterTest {

    @Mock
    private CompanyProfileSpringRepository springRepository;

    @Mock
    private CompanyProfileJpaMapper mapper;

    @Mock
    private UserSpringRepository userSpringRepository;

    @InjectMocks
    private CompanyProfileRepositoryAdapter adapter;

    private UUID userId;
    private UUID companyId;
    private UUID profileId;
    private CompanyProfile companyProfile;
    private UserJpaEntity userEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        companyProfile = CompanyProfile.of(
                null, userId, companyId, "ACME LTDA", null, null, null, null, null,
                OffsetDateTime.now(), OffsetDateTime.now()
        );
        userEntity = UserJpaEntity.builder().id(userId).companyId(companyId).build();
    }

    @Test
    @DisplayName("Deve inserir CompanyProfile quando não existe linha para o user")
    void shouldInsertWhenNoExisting() {
        CompanyProfileJpaEntity entity = CompanyProfileJpaEntity.builder()
                .id(profileId).userId(userId).companyId(companyId).user(userEntity).build();
        when(userSpringRepository.getReferenceById(userId)).thenReturn(userEntity);
        when(springRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(mapper.toEntity(companyProfile)).thenReturn(entity);
        when(springRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(companyProfile);

        assertThat(adapter.save(companyProfile)).isNotNull();
        verify(mapper).toEntity(companyProfile);
        verify(mapper, never()).applyToExisting(any(), any());
    }

    @Test
    @DisplayName("Deve atualizar CompanyProfile existente quando id está carregado")
    void shouldUpdateWhenIdPresent() {
        CompanyProfile withId = CompanyProfile.of(
                profileId, userId, companyId, "ACME ATDA", null, null, null, null, null,
                OffsetDateTime.now(), OffsetDateTime.now()
        );
        CompanyProfileJpaEntity managed = CompanyProfileJpaEntity.builder()
                .id(profileId).userId(userId).companyId(companyId).user(userEntity).build();

        when(userSpringRepository.getReferenceById(userId)).thenReturn(userEntity);
        when(springRepository.findById(profileId)).thenReturn(Optional.of(managed));
        when(springRepository.save(managed)).thenReturn(managed);
        when(mapper.toDomain(managed)).thenReturn(withId);

        CompanyProfile saved = adapter.save(withId);

        assertThat(saved.getId()).isEqualTo(profileId);
        verify(mapper).applyToExisting(withId, managed);
        verify(mapper, never()).toEntity(any());
        ArgumentCaptor<CompanyProfileJpaEntity> captor = ArgumentCaptor.forClass(CompanyProfileJpaEntity.class);
        verify(springRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(profileId);
    }

    @Test
    @DisplayName("Deve fazer merge por user_id quando id é null mas perfil já existe")
    void shouldMergeByUserIdWhenIdNull() {
        CompanyProfileJpaEntity managed = CompanyProfileJpaEntity.builder()
                .id(profileId).userId(userId).companyId(companyId).user(userEntity).build();
        CompanyProfile merged = CompanyProfile.of(
                profileId, userId, companyId, "ACME LTDA", null, null, null, null, null,
                OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userSpringRepository.getReferenceById(userId)).thenReturn(userEntity);
        when(springRepository.findByUserId(userId)).thenReturn(Optional.of(managed));
        when(springRepository.save(managed)).thenReturn(managed);
        when(mapper.toDomain(managed)).thenReturn(merged);

        CompanyProfile saved = adapter.save(companyProfile);

        assertThat(saved.getId()).isEqualTo(profileId);
        verify(mapper).applyToExisting(companyProfile, managed);
        verify(mapper, never()).toEntity(any());
    }
}
