package com.keepguard.ms_user.application.service.user.strategy.notification;

import com.keepguard.ms_user.domain.enums.UserTypeEnum;

import java.util.UUID;

public interface NotificationStrategy {

    boolean supports(UserTypeEnum userType);

    void sendNotification(UUID userId, String message);

    void configurePreferences(UUID userId, Object preferences);
}
