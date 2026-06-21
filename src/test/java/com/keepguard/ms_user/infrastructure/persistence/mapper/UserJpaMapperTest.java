package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para UserJpaMapper
 */
@DisplayName("User JPA Mapper Tests")
class UserJpaMapperTest {

    private UserJpaMapper mapper;
    private UUID userId;
    private UUID codeUser;
    private UUID companyId;
    private UUID xApplication;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @BeforeEach
    void setUp() {
        mapper = new UserJpaMapper();
        userId = UUID.randomUUID();
        codeUser = UUID.randomUUID();
        companyId = UUID.randomUUID();
        xApplication = UUID.randomUUID();
        createdAt = OffsetDateTime.now().minusDays(1);
        updatedAt = OffsetDateTime.now();
    }

    @Test
    @DisplayName("Deve converter UserJpaEntity para User corretamente")
    void shouldConvertUserJpaEntityToUserCorrectly() {
        // Given
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(userId)
                .codeUser(codeUser)
                .companyId(companyId)
                .xApplication(xApplication)
                .type(UserTypeEnum.PERSON)
                .status(UserStatusEnum.ACTIVE)
                .email("test@example.com")
                .phoneE164("+5511999999999")
                .preferredLocale("pt-BR")
                .timezone("America/Sao_Paulo")
                .avatarUrl("https://example.com/avatar.jpg")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // When
        User user = mapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals(codeUser, user.getCodeUser());
        assertEquals(companyId, user.getCompanyId());
        assertEquals(UserTypeEnum.PERSON, user.getType());
        assertEquals(UserStatusEnum.ACTIVE, user.getStatus());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("+5511999999999", user.getPhoneE164());
        assertEquals("pt-BR", user.getPreferredLocale());
        assertEquals("America/Sao_Paulo", user.getTimezone());
        assertEquals("https://example.com/avatar.jpg", user.getAvatarUrl());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve converter UserJpaEntity com valores nulos para campos opcionais")
    void shouldConvertUserJpaEntityWithNullOptionalFields() {
        // Given
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(userId)
                .codeUser(codeUser)
                .companyId(companyId)
                .xApplication(xApplication)
                .type(UserTypeEnum.COMPANY)
                .status(UserStatusEnum.PENDING)
                .email("company@example.com")
                .phoneE164(null)
                .preferredLocale(null)
                .timezone(null)
                .avatarUrl(null)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // When
        User user = mapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals(codeUser, user.getCodeUser());
        assertEquals(companyId, user.getCompanyId());
        assertEquals(UserTypeEnum.COMPANY, user.getType());
        assertEquals(UserStatusEnum.PENDING, user.getStatus());
        assertEquals("company@example.com", user.getEmail());
        assertNull(user.getPhoneE164());
        assertNull(user.getPreferredLocale());
        assertNull(user.getTimezone());
        assertNull(user.getAvatarUrl());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve retornar null quando UserJpaEntity for null")
    void shouldReturnNullWhenUserJpaEntityIsNull() {
        // When
        User user = mapper.toDomain(null);

        // Then
        assertNull(user);
    }

    @Test
    @DisplayName("Deve converter User para UserJpaEntity corretamente")
    void shouldConvertUserToUserJpaEntityCorrectly() {
        // Given
        User user = User.of(
                userId,
                codeUser,
                companyId,
                xApplication,
                UserTypeEnum.PERSON,
                UserStatusEnum.ACTIVE,
                "test@example.com",
                "+5511999999999",
                "pt-BR",
                "America/Sao_Paulo",
                "https://example.com/avatar.jpg",
                null, // displayHandle
                createdAt,
                updatedAt
        );

        // When
        UserJpaEntity entity = mapper.toEntity(user);

        // Then
        assertNotNull(entity);
        assertEquals(userId, entity.getId());
        assertEquals(codeUser, entity.getCodeUser());
        assertEquals(companyId, entity.getCompanyId());
        assertEquals(UserTypeEnum.PERSON, entity.getType());
        assertEquals(UserStatusEnum.ACTIVE, entity.getStatus());
        assertEquals("test@example.com", entity.getEmail());
        assertEquals("+5511999999999", entity.getPhoneE164());
        assertEquals("pt-BR", entity.getPreferredLocale());
        assertEquals("America/Sao_Paulo", entity.getTimezone());
        assertEquals("https://example.com/avatar.jpg", entity.getAvatarUrl());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve converter User com valores nulos para campos opcionais")
    void shouldConvertUserWithNullOptionalFields() {
        // Given
        User user = User.of(
                userId,
                codeUser,
                companyId,
                xApplication,
                UserTypeEnum.COMPANY,
                UserStatusEnum.INACTIVE,
                "company@example.com",
                null, // phoneE164
                null, // preferredLocale
                null, // timezone
                null, // avatarUrl
                null, // displayHandle
                createdAt,
                updatedAt
        );

        // When
        UserJpaEntity entity = mapper.toEntity(user);

        // Then
        assertNotNull(entity);
        assertEquals(userId, entity.getId());
        assertEquals(codeUser, entity.getCodeUser());
        assertEquals(companyId, entity.getCompanyId());
        assertEquals(UserTypeEnum.COMPANY, entity.getType());
        assertEquals(UserStatusEnum.INACTIVE, entity.getStatus());
        assertEquals("company@example.com", entity.getEmail());
        assertNull(entity.getPhoneE164());
        assertNull(entity.getPreferredLocale());
        assertNull(entity.getTimezone());
        assertNull(entity.getAvatarUrl());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve retornar null quando User for null")
    void shouldReturnNullWhenUserIsNull() {
        // When
        UserJpaEntity entity = mapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    @DisplayName("Deve converter User com todos os status possíveis")
    void shouldConvertUserWithAllPossibleStatuses() {
        // Given
        UserStatusEnum[] statuses = {
                UserStatusEnum.ACTIVE,
                UserStatusEnum.PENDING,
                UserStatusEnum.INACTIVE,
                UserStatusEnum.BLOCKED,
                UserStatusEnum.SUSPENDED
        };

        for (UserStatusEnum status : statuses) {
            // Given
            User user = User.of(
                    userId,
                    codeUser,
                    companyId,
                    xApplication,
                    UserTypeEnum.PERSON,
                    status,
                    "test@example.com",
                    "+5511999999999",
                    "pt-BR",
                    "America/Sao_Paulo",
                    "https://example.com/avatar.jpg",
                    null, // displayHandle
                    createdAt,
                    updatedAt
            );

            // When
            UserJpaEntity entity = mapper.toEntity(user);

            // Then
            assertNotNull(entity, "Entity não deve ser null para status: " + status);
            assertEquals(status, entity.getStatus(), "Status deve ser preservado: " + status);
        }
    }

    @Test
    @DisplayName("Deve converter User com todos os tipos possíveis")
    void shouldConvertUserWithAllPossibleTypes() {
        // Given
        UserTypeEnum[] types = {
                UserTypeEnum.PERSON,
                UserTypeEnum.COMPANY
        };

        for (UserTypeEnum type : types) {
            // Given
            User user = User.of(
                    userId,
                    codeUser,
                    companyId,
                    xApplication,
                    type,
                    UserStatusEnum.ACTIVE,
                    "test@example.com",
                    "+5511999999999",
                    "pt-BR",
                    "America/Sao_Paulo",
                    "https://example.com/avatar.jpg",
                    null, // displayHandle
                    createdAt,
                    updatedAt
            );

            // When
            UserJpaEntity entity = mapper.toEntity(user);

            // Then
            assertNotNull(entity, "Entity não deve ser null para tipo: " + type);
            assertEquals(type, entity.getType(), "Tipo deve ser preservado: " + type);
        }
    }

    @Test
    @DisplayName("Deve converter UserJpaEntity com todos os status possíveis")
    void shouldConvertUserJpaEntityWithAllPossibleStatuses() {
        // Given
        UserStatusEnum[] statuses = {
                UserStatusEnum.ACTIVE,
                UserStatusEnum.PENDING,
                UserStatusEnum.INACTIVE,
                UserStatusEnum.BLOCKED,
                UserStatusEnum.SUSPENDED
        };

        for (UserStatusEnum status : statuses) {
            // Given
            UserJpaEntity entity = UserJpaEntity.builder()
                    .id(userId)
                    .codeUser(codeUser)
                    .companyId(companyId)
                    .xApplication(xApplication)
                    .type(UserTypeEnum.PERSON)
                    .status(status)
                    .email("test@example.com")
                    .phoneE164("+5511999999999")
                    .preferredLocale("pt-BR")
                    .timezone("America/Sao_Paulo")
                    .avatarUrl("https://example.com/avatar.jpg")
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertNotNull(user, "User não deve ser null para status: " + status);
            assertEquals(status, user.getStatus(), "Status deve ser preservado: " + status);
        }
    }

    @Test
    @DisplayName("Deve converter UserJpaEntity com todos os tipos possíveis")
    void shouldConvertUserJpaEntityWithAllPossibleTypes() {
        // Given
        UserTypeEnum[] types = {
                UserTypeEnum.PERSON,
                UserTypeEnum.COMPANY
        };

        for (UserTypeEnum type : types) {
            // Given
            UserJpaEntity entity = UserJpaEntity.builder()
                    .id(userId)
                    .codeUser(codeUser)
                    .companyId(companyId)
                    .xApplication(xApplication)
                    .type(type)
                    .status(UserStatusEnum.ACTIVE)
                    .email("test@example.com")
                    .phoneE164("+5511999999999")
                    .preferredLocale("pt-BR")
                    .timezone("America/Sao_Paulo")
                    .avatarUrl("https://example.com/avatar.jpg")
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertNotNull(user, "User não deve ser null para tipo: " + type);
            assertEquals(type, user.getType(), "Tipo deve ser preservado: " + type);
        }
    }

    @Test
    @DisplayName("Deve converter User criado com create")
    void shouldConvertUserCreatedWithCreate() {
        // Given
        User user = User.create(
                codeUser,
                companyId,
                xApplication,
                UserTypeEnum.PERSON,
                "test@example.com",
                "+5511999999999",
                "pt-BR",
                "America/Sao_Paulo",
                "https://example.com/avatar.jpg"
        );

        // When
        UserJpaEntity entity = mapper.toEntity(user);

        // Then
        assertNotNull(entity);
        assertNotNull(entity.getId()); // ID deve ser gerado
        assertEquals(codeUser, entity.getCodeUser());
        assertEquals(companyId, entity.getCompanyId());
        assertEquals(UserTypeEnum.PERSON, entity.getType());
        assertEquals(UserStatusEnum.PENDING, entity.getStatus()); // Status padrão
        assertEquals("test@example.com", entity.getEmail());
        assertEquals("+5511999999999", entity.getPhoneE164());
        assertEquals("pt-BR", entity.getPreferredLocale());
        assertEquals("America/Sao_Paulo", entity.getTimezone());
        assertEquals("https://example.com/avatar.jpg", entity.getAvatarUrl());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve verificar se o mapper é um componente Spring")
    void shouldVerifyMapperIsSpringComponent() {
        // Given
        Class<?> mapperClass = UserJpaMapper.class;

        // When & Then
        assertTrue(mapperClass.isAnnotationPresent(org.springframework.stereotype.Component.class),
                "UserJpaMapper deve ter anotação @Component");
    }

    @Test
    @DisplayName("Deve converter User com email em maiúsculas")
    void shouldConvertUserWithUppercaseEmail() {
        // Given
        User user = User.of(
                userId,
                codeUser,
                companyId,
                xApplication,
                UserTypeEnum.PERSON,
                UserStatusEnum.ACTIVE,
                "TEST@EXAMPLE.COM", // Email em maiúsculas
                "+5511999999999",
                "pt-BR",
                "America/Sao_Paulo",
                "https://example.com/avatar.jpg",
                null, // displayHandle
                createdAt,
                updatedAt
        );

        // When
        UserJpaEntity entity = mapper.toEntity(user);

        // Then
        assertNotNull(entity);
        assertEquals("test@example.com", entity.getEmail()); // Deve estar em minúsculas
    }

    @Test
    @DisplayName("Deve converter UserJpaEntity com email em maiúsculas")
    void shouldConvertUserJpaEntityWithUppercaseEmail() {
        // Given
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(userId)
                .codeUser(codeUser)
                .companyId(companyId)
                .xApplication(xApplication)
                .type(UserTypeEnum.PERSON)
                .status(UserStatusEnum.ACTIVE)
                .email("TEST@EXAMPLE.COM") // Email em maiúsculas
                .phoneE164("+5511999999999")
                .preferredLocale("pt-BR")
                .timezone("America/Sao_Paulo")
                .avatarUrl("https://example.com/avatar.jpg")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // When
        User user = mapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail()); // Deve converter para minúsculas
    }
}
