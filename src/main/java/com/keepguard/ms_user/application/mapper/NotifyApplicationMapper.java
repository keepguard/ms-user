package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.application.dto.notify.NotifyViewDTO;
import com.keepguard.ms_user.application.dto.notify.NotifySimpleViewDTO;
import com.keepguard.ms_user.application.dto.notify.NotifyDetailsViewDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyPatchCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyCreateCommandDTO;
import com.keepguard.ms_user.domain.entity.Notify;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifyApplicationMapper {

    public Notify toCreateCommand(UserNotifyCreateCommandDTO command) {
        if (command == null) {
            return null;
        }

        return Notify.of(
            command.userId(),
            command.notifyEmail() != null ? command.notifyEmail() : true,
            command.notifySms() != null ? command.notifySms() : true,
            command.notifyWhatsapp() != null ? command.notifyWhatsapp() : true,
            command.notifyPush() != null ? command.notifyPush() : true,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            0L
        );
    }

    public NotifyViewDTO toView(Notify notify) {
        if (notify == null) {
            return null;
        }

        return new NotifyViewDTO(
            notify.getUserId(), // userId é usado como id
            notify.getUserId(),
            notify.isNotifyEmail(),
            notify.isNotifySms(),
            notify.isNotifyWhatsapp(),
            notify.isNotifyPush(),
            notify.getCreatedAt(),
            notify.getUpdatedAt(),
            notify.getVersion()
        );
    }

    public Notify applyChanges(Notify notify, UserNotifyPatchCommandDTO command) {
        // Aplicar mudanças apenas nos campos fornecidos (não nulos)
        if (command.notifyEmail() != null) {
            notify.setNotifyEmail(command.notifyEmail());
        }
        if (command.notifySms() != null) {
            notify.setNotifySms(command.notifySms());
        }
        if (command.notifyWhatsapp() != null) {
            notify.setNotifyWhatsapp(command.notifyWhatsapp());
        }
        if (command.notifyPush() != null) {
            notify.setNotifyPush(command.notifyPush());
        }

        return notify;
    }

    public NotifySimpleViewDTO toSimpleView(Notify notify) {
        if (notify == null) {
            return null;
        }

        return new NotifySimpleViewDTO(
            notify.getUserId(),
            notify.isNotifyEmail(),
            notify.isNotifySms(),
            notify.isNotifyWhatsapp(),
            notify.isNotifyPush()
        );
    }

    public NotifyDetailsViewDTO toDetailsView(Notify notify) {
        if (notify == null) {
            return null;
        }

        return new NotifyDetailsViewDTO(
            notify.getUserId(), // userId é usado como id
            notify.getUserId(),
            notify.isNotifyEmail(),
            notify.isNotifySms(),
            notify.isNotifyWhatsapp(),
            notify.isNotifyPush(),
            notify.getCreatedAt(),
            notify.getUpdatedAt(),
            notify.getVersion()
        );
    }

    public NotifyDetailsViewDTO toDetailsView(NotifyViewDTO view) {
        if (view == null) {
            return null;
        }

        return new NotifyDetailsViewDTO(
            view.id(),
            view.userId(),
            view.notifyEmail(),
            view.notifySms(),
            view.notifyWhatsapp(),
            view.notifyPush(),
            view.createdAt(),
            view.updatedAt(),
            view.version()
        );
    }
}
