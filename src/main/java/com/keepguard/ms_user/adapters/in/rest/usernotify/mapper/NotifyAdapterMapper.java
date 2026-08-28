package com.keepguard.ms_user.adapters.in.rest.usernotify.mapper;

import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyPatchRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.response.UserNotifyResponseDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyPatchCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyCreateCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByUserIdQueryDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByCodeUserQueryDTO;
import com.keepguard.ms_user.application.dto.notify.NotifyViewDTO;
import com.keepguard.ms_user.application.dto.notify.NotifyDetailsViewDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class NotifyAdapterMapper {

    public UserNotifyCreateCommandDTO toCreateCommand(UUID companyId, UserNotifyCreateRequestDTO request) {
        if (request == null) {
            return null;
        }

        return new UserNotifyCreateCommandDTO(
            request.userId(),
            companyId,
            request.notifyEmail(),
            request.notifySms(),
            request.notifyWhatsapp(),
            request.notifyPush()
        );
    }

    public UserNotifyPatchCommandDTO toPatchCommand(UUID userId, UUID codeUser, UUID companyId, UserNotifyPatchRequestDTO request) {
        if (request == null) {
            return null;
        }

        return new UserNotifyPatchCommandDTO(
            userId,
            codeUser,
            companyId,
            request.notifyEmail(),
            request.notifySms(),
            request.notifyWhatsapp(),
            request.notifyPush()
        );
    }

    public UserNotifyGetByUserIdQueryDTO toGetByUserIdQuery(UUID userId, UUID companyId) {
        return new UserNotifyGetByUserIdQueryDTO(userId, companyId);
    }

    public UserNotifyGetByCodeUserQueryDTO toGetByCodeUserQuery(UUID codeUser, UUID companyId) {
        return new UserNotifyGetByCodeUserQueryDTO(codeUser, companyId);
    }

    public UserNotifyResponseDTO toResponseDTO(NotifyViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserNotifyResponseDTO();
        dto.setId(view.id());
        dto.setUserId(view.userId());
        dto.setNotifyEmail(view.notifyEmail());
        dto.setNotifySms(view.notifySms());
        dto.setNotifyWhatsapp(view.notifyWhatsapp());
        dto.setNotifyPush(view.notifyPush());
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        return dto;
    }

    public UserNotifyResponseDTO toResponseDTO(NotifyDetailsViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserNotifyResponseDTO();
        dto.setId(view.id());
        dto.setUserId(view.userId());
        dto.setNotifyEmail(view.notifyEmail());
        dto.setNotifySms(view.notifySms());
        dto.setNotifyWhatsapp(view.notifyWhatsapp());
        dto.setNotifyPush(view.notifyPush());
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        return dto;
    }
}
