package com.keepguard.ms_user.application.service.user.strategy.profile;

import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;

import java.util.UUID;

public interface ProfileStrategy {

    boolean supports(UserTypeEnum userType);

    void createProfile(User user, Object profileData);

    void updateProfile(UUID userId, Object profileData);

    void deleteProfile(UUID userId);

    Object getProfile(UUID userId);
}
