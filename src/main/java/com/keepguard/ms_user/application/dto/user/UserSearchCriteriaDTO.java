package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;

import java.util.List;
import java.util.UUID;

public record UserSearchCriteriaDTO(
    String email,
    UUID companyId,
    UserTypeEnum type,
    UserStatusEnum status,
    int page,
    int size,
    List<String> sortFields,
    String sortDirection
) {
    public UserSearchCriteriaDTO {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        if (sortDirection == null) sortDirection = "ASC";
    }
}
