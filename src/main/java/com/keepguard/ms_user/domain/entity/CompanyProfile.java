package com.keepguard.ms_user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.lib_common.utils.BrazilianValidationUtils;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class CompanyProfile implements UserProfile {

    private final UUID userId;
    private UUID companyId; // Referência lógica ao ms-company
    private String legalNameSnapshot;
    private String cnpjSnapshot;
    private String stateRegistrationSnapshot;
    private String municipalRegistrationSnapshot;
    private String representativeName;
    private String representativeCpf;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private CompanyProfile(UUID userId, UUID companyId, String legalNameSnapshot, String cnpjSnapshot,
                         String stateRegistrationSnapshot, String municipalRegistrationSnapshot,
                         String representativeName, String representativeCpf,
                         OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.userId = userId; // Será definido posteriormente pelas strategies
        this.companyId = Objects.requireNonNull(companyId, "companyId é obrigatório");
        this.legalNameSnapshot = legalNameSnapshot;
        this.cnpjSnapshot = validateCnpj(cnpjSnapshot);
        this.stateRegistrationSnapshot = stateRegistrationSnapshot;
        this.municipalRegistrationSnapshot = municipalRegistrationSnapshot;
        this.representativeName = representativeName;
        this.representativeCpf = validateRepresentativeCpf(representativeCpf);
        this.createdAt = Objects.requireNonNullElse(createdAt, OffsetDateTime.now());
        this.updatedAt = Objects.requireNonNullElse(updatedAt, OffsetDateTime.now());
    }

    // Construtor para desserialização Jackson
    @JsonCreator
    private CompanyProfile(
            @JsonProperty("userId") UUID userId,
            @JsonProperty("companyId") UUID companyId,
            @JsonProperty("legalNameSnapshot") String legalNameSnapshot,
            @JsonProperty("cnpjSnapshot") String cnpjSnapshot,
            @JsonProperty("stateRegistrationSnapshot") String stateRegistrationSnapshot,
            @JsonProperty("municipalRegistrationSnapshot") String municipalRegistrationSnapshot,
            @JsonProperty("representativeName") String representativeName,
            @JsonProperty("representativeCpf") String representativeCpf,
            @JsonProperty("createdAt") OffsetDateTime createdAt,
            @JsonProperty("updatedAt") OffsetDateTime updatedAt,
            @JsonProperty("formattedCnpj") String ignoredFormattedCnpj,
            @JsonProperty("formattedRepresentativeCpf") String ignoredFormattedRepresentativeCpf
    ) {
        this.userId = userId;
        this.companyId = companyId;
        this.legalNameSnapshot = legalNameSnapshot;
        this.cnpjSnapshot = cnpjSnapshot;
        this.stateRegistrationSnapshot = stateRegistrationSnapshot;
        this.municipalRegistrationSnapshot = municipalRegistrationSnapshot;
        this.representativeName = representativeName;
        this.representativeCpf = representativeCpf;
        this.createdAt = Objects.requireNonNullElse(createdAt, OffsetDateTime.now());
        this.updatedAt = Objects.requireNonNullElse(updatedAt, OffsetDateTime.now());
    }

    public static CompanyProfile create(UUID userId, UUID companyId) {
        return new CompanyProfile(userId, companyId, null, null, null, null, null, null,
                OffsetDateTime.now(), OffsetDateTime.now());
    }

    public static CompanyProfile of(UUID userId, UUID companyId, String legalNameSnapshot, String cnpjSnapshot,
                                  String stateRegistrationSnapshot, String municipalRegistrationSnapshot,
                                  String representativeName, String representativeCpf,
                                  OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new CompanyProfile(userId, companyId, legalNameSnapshot, cnpjSnapshot,
                stateRegistrationSnapshot, municipalRegistrationSnapshot, representativeName, representativeCpf,
                createdAt, updatedAt);
    }

    private String validateCnpj(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return null; // CNPJ é opcional no snapshot
        }
        String cleanedCnpj = cnpj.replaceAll("[^0-9]", "");
        if (!BrazilianValidationUtils.isValidCnpj(cleanedCnpj)) {
            throw new ValidationException("CNPJ inválido");
        }
        return cleanedCnpj;
    }

    private String validateRepresentativeCpf(String representativeCpf) {
        if (representativeCpf == null || representativeCpf.trim().isEmpty()) {
            return null; // CPF do representante é opcional
        }
        String cleanedCpf = representativeCpf.replaceAll("[^0-9]", "");
        if (!BrazilianValidationUtils.isValidCpf(cleanedCpf)) {
            throw new ValidationException("CPF do representante inválido");
        }
        return cleanedCpf;
    }

    // Getters
    public UUID getUserId() { return userId; }
    public UUID getCompanyId() { return companyId; }
    public String getLegalNameSnapshot() { return legalNameSnapshot; }
    public String getCnpjSnapshot() { return cnpjSnapshot; }
    public String getStateRegistrationSnapshot() { return stateRegistrationSnapshot; }
    public String getMunicipalRegistrationSnapshot() { return municipalRegistrationSnapshot; }
    public String getRepresentativeName() { return representativeName; }
    public String getRepresentativeCpf() { return representativeCpf; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    // Setters para campos mutáveis
    public void setCompanyId(UUID companyId) {
        this.companyId = Objects.requireNonNull(companyId, "companyId é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setLegalNameSnapshot(String legalNameSnapshot) {
        this.legalNameSnapshot = legalNameSnapshot;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCnpjSnapshot(String cnpjSnapshot) {
        this.cnpjSnapshot = validateCnpj(cnpjSnapshot);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setStateRegistrationSnapshot(String stateRegistrationSnapshot) {
        this.stateRegistrationSnapshot = stateRegistrationSnapshot;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setMunicipalRegistrationSnapshot(String municipalRegistrationSnapshot) {
        this.municipalRegistrationSnapshot = municipalRegistrationSnapshot;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setRepresentativeCpf(String representativeCpf) {
        this.representativeCpf = validateRepresentativeCpf(representativeCpf);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public String getFormattedCnpj() {
        if (cnpjSnapshot == null || cnpjSnapshot.length() != 14) return cnpjSnapshot;
        return cnpjSnapshot.substring(0, 2) + "." + cnpjSnapshot.substring(2, 5) + "." +
               cnpjSnapshot.substring(5, 8) + "/" + cnpjSnapshot.substring(8, 12) + "-" +
               cnpjSnapshot.substring(12, 14);
    }

    public String getFormattedRepresentativeCpf() {
        if (representativeCpf == null || representativeCpf.length() != 11) return representativeCpf;
        return representativeCpf.substring(0, 3) + "." + representativeCpf.substring(3, 6) + "." +
               representativeCpf.substring(6, 9) + "-" + representativeCpf.substring(9, 11);
    }

    public boolean hasCnpjSnapshot() {
        return cnpjSnapshot != null && !cnpjSnapshot.trim().isEmpty();
    }

    public boolean hasRepresentativeInfo() {
        return representativeName != null && !representativeName.trim().isEmpty() &&
               representativeCpf != null && !representativeCpf.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyProfile that = (CompanyProfile) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "CompanyProfile{" +
                "userId=" + userId +
                ", companyId=" + companyId +
                ", legalNameSnapshot='" + legalNameSnapshot + '\'' +
                ", cnpjSnapshot='" + cnpjSnapshot + '\'' +
                '}';
    }
}
