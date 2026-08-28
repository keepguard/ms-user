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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
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
    private PersonProfile personProfile;
    private PersonProfileJpaEntity personProfileEntity;
    private UserJpaEntity userEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        personProfile = PersonProfile.of(
                userId,
                "Rafael Soares",
                null, // cpf
                null, // rg
                null, // rgIssuer
                null, // rgState
                null, // dateOfBirth
                null, // gender
                null, // maritalStatus
                null, // nationality
                null, // birthCountry
                null, // birthState
                null, // birthCity
                null, // motherName
                null, // fatherName
                false, // pep
                null, // kycStatus
                null, // kycLevel
                null, // occupation
                null, // incomeRange
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        userEntity = UserJpaEntity.builder()
                .id(userId)
                .companyId(companyId)
                .build();

        personProfileEntity = PersonProfileJpaEntity.builder()
                .userId(userId)
                .fullName("Rafael Soares")
                .user(userEntity)
                .build();
    }

    @Test
    @DisplayName("Deve salvar PersonProfile")
    void shouldSavePersonProfile() {
        // Given
        when(userSpringRepository.getReferenceById(userId)).thenReturn(userEntity);
        when(mapper.toEntity(personProfile)).thenReturn(personProfileEntity);
        when(springRepository.save(any(PersonProfileJpaEntity.class))).thenReturn(personProfileEntity);
        when(mapper.toDomain(personProfileEntity)).thenReturn(personProfile);

        // When
        PersonProfile saved = adapter.save(personProfile);

        // Then
        assertThat(saved).isNotNull();
        verify(springRepository).save(any(PersonProfileJpaEntity.class));
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
