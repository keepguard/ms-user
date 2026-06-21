package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UserSearchQueryDTO(
    @NotNull(message = "xApplication é obrigatório")
    UUID xApplication,
    
    String email,
    UUID companyId,
    UserTypeEnum type,
    UserStatusEnum status,
    int page,
    int size,
    List<String> sortFields,
    String sortDirection
) {
}
