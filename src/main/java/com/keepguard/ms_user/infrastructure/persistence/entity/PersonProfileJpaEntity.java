package com.keepguard.ms_user.infrastructure.persistence.entity;

import com.keepguard.ms_user.domain.enums.GenderEnum;
import com.keepguard.ms_user.domain.enums.IncomeRangeEnum;
import com.keepguard.ms_user.domain.enums.KycLevelEnum;
import com.keepguard.ms_user.domain.enums.KycStatusEnum;
import com.keepguard.ms_user.domain.enums.MaritalStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_person_profile")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonProfileJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserJpaEntity user;

    @Column(name = "user_id", insertable = false, updatable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "cpf", nullable = true, length = 14)
    private String cpf;

    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "rg_issuer", length = 20)
    private String rgIssuer;

    @Column(name = "rg_state", length = 2)
    private String rgState;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private GenderEnum gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatusEnum maritalStatus;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "birth_country", length = 50)
    private String birthCountry;

    @Column(name = "birth_state", length = 2)
    private String birthState;

    @Column(name = "birth_city", length = 100)
    private String birthCity;

    @Column(name = "mother_name", length = 200)
    private String motherName;

    @Column(name = "father_name", length = 200)
    private String fatherName;

    @Column(name = "pep", nullable = false)
    @Builder.Default
    private boolean pep = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false, length = 20)
    @Builder.Default
    private KycStatusEnum kycStatus = KycStatusEnum.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_level", nullable = false, length = 20)
    @Builder.Default
    private KycLevelEnum kycLevel = KycLevelEnum.BASIC;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Enumerated(EnumType.STRING)
    @Column(name = "income_range", length = 50)
    private IncomeRangeEnum incomeRange;

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
        if (!(o instanceof PersonProfileJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
