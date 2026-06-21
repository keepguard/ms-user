package com.keepguard.ms_user.domain.strategy;

import com.keepguard.ms_user.domain.entity.User;

public interface UserProfileStrategy {

    void validateOnCreate(User user);

    void validateOnUpdate(User user);

    void validateForActivation(User user);

    com.keepguard.ms_user.domain.enums.UserTypeEnum getSupportedType();
}
