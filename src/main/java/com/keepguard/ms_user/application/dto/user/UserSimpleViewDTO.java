package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSimpleViewDTO(
    UUID id,
    UUID codeUser,
    String email,
    UserStatusEnum status,
    UserTypeEnum type,
    OffsetDateTime createdAt
) {}
