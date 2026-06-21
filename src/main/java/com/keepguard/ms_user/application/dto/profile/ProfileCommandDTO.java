package com.keepguard.ms_user.application.dto.profile;

import com.keepguard.ms_user.domain.enums.UserTypeEnum;

public interface ProfileCommandDTO {

    UserTypeEnum getUserType();

    Object getProfileData();

    boolean hasProfileData();
}
