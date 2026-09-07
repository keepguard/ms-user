package com.keepguard.ms_user.application.dto.profile;

import com.keepguard.ms_user.domain.enums.GenderEnum;
import com.keepguard.ms_user.domain.enums.IncomeRangeEnum;
import com.keepguard.ms_user.domain.enums.KycLevelEnum;
import com.keepguard.ms_user.domain.enums.KycStatusEnum;
import com.keepguard.ms_user.domain.enums.MaritalStatusEnum;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PersonProfileViewDTO(
    UUID userId,
    String fullName,
    String cpf,
    String rg,
    String rgIssuer,
    String rgState,
    LocalDate dateOfBirth,
    GenderEnum gender,
    MaritalStatusEnum maritalStatus,
    String nationality,
    String birthCountry,
    String birthState,
    String birthCity,
    String motherName,
    String fatherName,
    boolean pep,
    KycStatusEnum kycStatus,
    KycLevelEnum kycLevel,
    String occupation,
    IncomeRangeEnum incomeRange,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
