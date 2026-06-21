package com.keepguard.ms_user.application.service.user.strategy.profile;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.application.port.out.persistence.PersonProfileRepositoryPort;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Person Profile Strategy Tests")
class PersonProfileStrategyTest {

    @Mock
    private PersonProfileRepositoryPort personProfileRepositoryPort;

    @InjectMocks
    private PersonProfileStrategy strategy;

    private User user;
    private PersonProfile personProfile;
    private UUID userId;
    private UUID companyId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        user = User.of(
                userId,
                UUID.randomUUID(),
                companyId,
                UUID.randomUUID(),
                UserTypeEnum.PERSON,
                UserStatusEnum.ACTIVE,
                "test@example.com",
                null,
                null,
                null,
                null, // avatarUrl
                "test.user", // displayHandle (em User)
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

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
    }

    @Test
    @DisplayName("Deve suportar apenas UserTypeEnum.PERSON")
    void shouldSupportOnlyPersonUserType() {
        assertTrue(strategy.supports(UserTypeEnum.PERSON));
        assertFalse(strategy.supports(UserTypeEnum.COMPANY));
    }

    @Test
    @DisplayName("Deve criar PersonProfile")
    void shouldCreatePersonProfile() {
        // Given - display_handle é validado e persistido em User (UserCommandService)
        when(personProfileRepositoryPort.save(any(PersonProfile.class))).thenReturn(personProfile);

        // When
        assertDoesNotThrow(() -> strategy.createProfile(user, personProfile));

        // Then
        verify(personProfileRepositoryPort).save(any(PersonProfile.class));
    }

    @Test
    @DisplayName("Deve aceitar PersonProfile sem displayHandle (opcional, em User)")
    void shouldAcceptPersonProfileWithoutDisplayHandle() {
        // Given
        PersonProfile profileWithoutHandle = PersonProfile.of(
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
        when(personProfileRepositoryPort.save(any(PersonProfile.class))).thenReturn(profileWithoutHandle);

        // When
        assertDoesNotThrow(() -> strategy.createProfile(user, profileWithoutHandle));

        // Then
        verify(personProfileRepositoryPort).save(any(PersonProfile.class));
    }

    @Test
    @DisplayName("Deve lançar ValidationException quando PersonProfile não encontrado no update")
    void shouldThrowValidationExceptionWhenPersonProfileNotFoundOnUpdate() {
        // Given
        when(personProfileRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            strategy.updateProfile(userId, personProfile);
        });

        assertTrue(ex.getMessage().contains("não encontrado"));
        verify(personProfileRepositoryPort, never()).save(any(PersonProfile.class));
    }
}
