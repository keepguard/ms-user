package com.keepguard.ms_user.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_company_profile")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyProfileJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserJpaEntity user;

    // opcional: espelho legível do FK; não mexer para evitar duplicidade de fonte da verdade
    @Column(name = "user_id", insertable = false, updatable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    @Column(name = "legal_name_snapshot", length = 200)
    private String legalNameSnapshot;

    @Column(name = "cnpj_snapshot", length = 18)
    private String cnpjSnapshot;

    @Column(name = "state_registration_snapshot", length = 20)
    private String stateRegistrationSnapshot;

    @Column(name = "municipal_registration_snapshot", length = 20)
    private String municipalRegistrationSnapshot;

    @Column(name = "representative_name", length = 200)
    private String representativeName;

    @Column(name = "representative_cpf", length = 14)
    private String representativeCpf;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyProfileJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
