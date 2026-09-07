package com.keepguard.ms_user.application.dto.profile;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CompanyProfileViewDTO(
    UUID userId,
    UUID companyId,
    String legalNameSnapshot,
    String cnpjSnapshot,
    String stateRegistrationSnapshot,
    String municipalRegistrationSnapshot,
    String representativeName,
    String representativeCpf,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
