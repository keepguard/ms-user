package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.infrastructure.persistence.entity.PersonProfileJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PersonProfileJpaMapper")
class PersonProfileJpaMapperTest {

    private final PersonProfileJpaMapper mapper = new PersonProfileJpaMapper();

    @Test
    @DisplayName("Round-trip preserva id e userId")
    void roundTripPreservesId() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        PersonProfile domain = PersonProfile.of(
                id, userId, "Nome Completo", null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                false, null, null, null, null, now, now
        );

        PersonProfileJpaEntity entity = mapper.toEntity(domain);
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);

        PersonProfile back = mapper.toDomain(entity);
        assertThat(back.getId()).isEqualTo(id);
        assertThat(back.getUserId()).isEqualTo(userId);
        assertThat(back.getFullName()).isEqualTo("Nome Completo");
    }

    @Test
    @DisplayName("applyToExisting atualiza campos sem trocar id")
    void applyToExistingKeepsId() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PersonProfileJpaEntity existing = PersonProfileJpaEntity.builder()
                .id(id)
                .userId(userId)
                .fullName("Antigo")
                .build();
        PersonProfile domain = PersonProfile.of(
                id, userId, "Novo Nome", "39053344705",
                null, null, null, null, null, null, null, null, null, null, null, null,
                false, null, null, null, null, OffsetDateTime.now(), OffsetDateTime.now()
        );

        mapper.applyToExisting(domain, existing);

        assertThat(existing.getId()).isEqualTo(id);
        assertThat(existing.getFullName()).isEqualTo("Novo Nome");
        assertThat(existing.getCpf()).isEqualTo("39053344705");
    }
}
