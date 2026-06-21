package com.keepguard.ms_user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.GenderEnum;
import com.keepguard.ms_user.domain.enums.IncomeRangeEnum;
import com.keepguard.ms_user.domain.enums.KycLevelEnum;
import com.keepguard.ms_user.domain.enums.KycStatusEnum;
import com.keepguard.ms_user.domain.enums.MaritalStatusEnum;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class PersonProfile implements UserProfile {

    private final UUID userId;
    private String fullName;
    private String cpf;
    private String rg;
    private String rgIssuer;
    private String rgState;
    private LocalDate dateOfBirth;
    private GenderEnum gender;
    private MaritalStatusEnum maritalStatus;
    private String nationality;
    private String birthCountry;
    private String birthState;
    private String birthCity;
    private String motherName;
    private String fatherName;
    private boolean pep; // Pessoa Politicamente Exposta
    private KycStatusEnum kycStatus;
    private KycLevelEnum kycLevel;
    private String occupation;
    private IncomeRangeEnum incomeRange;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private PersonProfile(UUID userId, String fullName, String cpf, String rg, String rgIssuer, String rgState,
                        LocalDate dateOfBirth, GenderEnum gender, MaritalStatusEnum maritalStatus,
                        String nationality, String birthCountry, String birthState, String birthCity,
                        String motherName, String fatherName, boolean pep, KycStatusEnum kycStatus,
                        KycLevelEnum kycLevel, String occupation, IncomeRangeEnum incomeRange,
                        OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.userId = userId;
        this.fullName = validateFullName(fullName);
        this.cpf = validateCpf(cpf);
        this.rg = rg;
        this.rgIssuer = rgIssuer;
        this.rgState = rgState;
        this.dateOfBirth = validateDateOfBirth(dateOfBirth);
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.nationality = nationality;
        this.birthCountry = birthCountry;
        this.birthState = birthState;
        this.birthCity = birthCity;
        this.motherName = motherName;
        this.fatherName = fatherName;
        this.pep = pep;
        this.kycStatus = Objects.requireNonNullElse(kycStatus, KycStatusEnum.NOT_STARTED);
        this.kycLevel = Objects.requireNonNullElse(kycLevel, KycLevelEnum.BASIC);
        this.occupation = occupation;
        this.incomeRange = incomeRange;
        this.createdAt = Objects.requireNonNullElse(createdAt, OffsetDateTime.now());
        this.updatedAt = Objects.requireNonNullElse(updatedAt, OffsetDateTime.now());
    }

    // Construtor para desserialização Jackson
    @JsonCreator
    private PersonProfile(
            @JsonProperty("userId") UUID userId,
            @JsonProperty("fullName") String fullName,
            @JsonProperty("cpf") String cpf,
            @JsonProperty("rg") String rg,
            @JsonProperty("rgIssuer") String rgIssuer,
            @JsonProperty("rgState") String rgState,
            @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
            @JsonProperty("gender") GenderEnum gender,
            @JsonProperty("maritalStatus") MaritalStatusEnum maritalStatus,
            @JsonProperty("nationality") String nationality,
            @JsonProperty("birthCountry") String birthCountry,
            @JsonProperty("birthState") String birthState,
            @JsonProperty("birthCity") String birthCity,
            @JsonProperty("motherName") String motherName,
            @JsonProperty("fatherName") String fatherName,
            @JsonProperty("pep") boolean pep,
            @JsonProperty("kycStatus") KycStatusEnum kycStatus,
            @JsonProperty("kycLevel") KycLevelEnum kycLevel,
            @JsonProperty("occupation") String occupation,
            @JsonProperty("incomeRange") IncomeRangeEnum incomeRange,
            @JsonProperty("createdAt") OffsetDateTime createdAt,
            @JsonProperty("updatedAt") OffsetDateTime updatedAt,
            @JsonProperty("age") Integer ignoredAge,
            @JsonProperty("ofLegalAge") Boolean ignoredOfLegalAge,
            @JsonProperty("formattedCpf") String ignoredFormattedCpf
    ) {
        this.userId = userId;
        this.fullName = fullName;
        this.cpf = cpf;
        this.rg = rg;
        this.rgIssuer = rgIssuer;
        this.rgState = rgState;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.nationality = nationality;
        this.birthCountry = birthCountry;
        this.birthState = birthState;
        this.birthCity = birthCity;
        this.motherName = motherName;
        this.fatherName = fatherName;
        this.pep = pep;
        this.kycStatus = Objects.requireNonNullElse(kycStatus, KycStatusEnum.NOT_STARTED);
        this.kycLevel = Objects.requireNonNullElse(kycLevel, KycLevelEnum.BASIC);
        this.occupation = occupation;
        this.incomeRange = incomeRange;
        this.createdAt = Objects.requireNonNullElse(createdAt, OffsetDateTime.now());
        this.updatedAt = Objects.requireNonNullElse(updatedAt, OffsetDateTime.now());
    }

    public static PersonProfile create(UUID userId, String fullName, String cpf, LocalDate dateOfBirth) {
        return new PersonProfile(userId, fullName, cpf, null, null, null, dateOfBirth,
                null, null, null, null, null, null, null, null, false,
                KycStatusEnum.NOT_STARTED, KycLevelEnum.BASIC, null, null,
                OffsetDateTime.now(), OffsetDateTime.now());
    }

    public static PersonProfile of(UUID userId, String fullName, String cpf, String rg, String rgIssuer, String rgState,
                                 LocalDate dateOfBirth, GenderEnum gender, MaritalStatusEnum maritalStatus,
                                 String nationality, String birthCountry, String birthState, String birthCity,
                                 String motherName, String fatherName, boolean pep, KycStatusEnum kycStatus,
                                 KycLevelEnum kycLevel, String occupation, IncomeRangeEnum incomeRange,
                                 OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new PersonProfile(userId, fullName, cpf, rg, rgIssuer, rgState, dateOfBirth,
                gender, maritalStatus, nationality, birthCountry, birthState, birthCity,
                motherName, fatherName, pep, kycStatus, kycLevel, occupation, incomeRange,
                createdAt, updatedAt);
    }

    private String validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Nome completo é obrigatório");
        }
        String trimmed = fullName.trim();
        if (trimmed.length() < 2) {
            throw new ValidationException("Nome completo deve ter pelo menos 2 caracteres");
        }
        if (trimmed.length() > 200) {
            throw new ValidationException("Nome completo deve ter no máximo 200 caracteres");
        }
        return trimmed;
    }

    private String validateCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return null; // CPF opcional
        }
        String cleanedCpf = cpf.replaceAll("[^0-9]", "");
        
        // Se CPF foi informado, validar se está correto
        if (cleanedCpf.length() > 0) {
            com.keepguard.lib_common.utils.BrazilianValidationUtils.validateCpf(cleanedCpf);
        }
        
        return cleanedCpf;
    }

    private LocalDate validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null; // Data de nascimento opcional
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("Data de nascimento não pode ser futura");
        }
        // Regra: idade mínima 13 anos (apenas se data informada)
        LocalDate minDate = LocalDate.now().minusYears(13);
        if (dateOfBirth.isAfter(minDate)) {
            throw new ValidationException("Usuário deve ter pelo menos 13 anos");
        }
        return dateOfBirth;
    }

    // Getters
    public UUID getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getCpf() { return cpf; }
    public String getRg() { return rg; }
    public String getRgIssuer() { return rgIssuer; }
    public String getRgState() { return rgState; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public GenderEnum getGender() { return gender; }
    public MaritalStatusEnum getMaritalStatus() { return maritalStatus; }
    public String getNationality() { return nationality; }
    public String getBirthCountry() { return birthCountry; }
    public String getBirthState() { return birthState; }
    public String getBirthCity() { return birthCity; }
    public String getMotherName() { return motherName; }
    public String getFatherName() { return fatherName; }
    public boolean isPep() { return pep; }
    public KycStatusEnum getKycStatus() { return kycStatus; }
    public KycLevelEnum getKycLevel() { return kycLevel; }
    public String getOccupation() { return occupation; }
    public IncomeRangeEnum getIncomeRange() { return incomeRange; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    // Setters para campos mutáveis
    public void setFullName(String fullName) {
        this.fullName = validateFullName(fullName);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCpf(String cpf) {
        this.cpf = validateCpf(cpf);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setRg(String rg) {
        this.rg = rg;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setRgIssuer(String rgIssuer) {
        this.rgIssuer = rgIssuer;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setRgState(String rgState) {
        this.rgState = rgState;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = validateDateOfBirth(dateOfBirth);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setMaritalStatus(MaritalStatusEnum maritalStatus) {
        this.maritalStatus = maritalStatus;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setBirthState(String birthState) {
        this.birthState = birthState;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setPep(boolean pep) {
        this.pep = pep;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setKycStatus(KycStatusEnum kycStatus) {
        this.kycStatus = Objects.requireNonNull(kycStatus, "Status KYC é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setKycLevel(KycLevelEnum kycLevel) {
        this.kycLevel = Objects.requireNonNull(kycLevel, "Nível KYC é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setIncomeRange(IncomeRangeEnum incomeRange) {
        this.incomeRange = incomeRange;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public boolean isOfLegalAge() {
        return getAge() >= 18;
    }

    public String getFormattedCpf() {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
               cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonProfile that = (PersonProfile) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "PersonProfile{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
