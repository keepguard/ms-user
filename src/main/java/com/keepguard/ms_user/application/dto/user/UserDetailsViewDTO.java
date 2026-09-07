package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.application.dto.profile.CompanyProfileViewDTO;
import com.keepguard.ms_user.application.dto.profile.PersonProfileViewDTO;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDetailsViewDTO(
    UUID id,
    UUID codeUser,
    UUID companyId,
    UserTypeEnum type,
    UserStatusEnum status,
    String email,
    String phoneE164,
    String preferredLocale,
    String timezone,
    String avatarUrl,
    String displayHandle,
    PersonProfileViewDTO personProfile,
    CompanyProfileViewDTO companyProfile,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
