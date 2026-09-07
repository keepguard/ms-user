package com.keepguard.ms_user.application.dto.profile;

import java.util.UUID;

public record CompanyProfileCommandDTO(
    UUID companyId,
    String legalNameSnapshot,
    String cnpjSnapshot,
    String stateRegistrationSnapshot,
    String municipalRegistrationSnapshot,
    String representativeName,
    String representativeCpf
) {}
