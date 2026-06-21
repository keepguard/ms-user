package com.keepguard.ms_user.application.port.in;

import com.keepguard.ms_user.application.dto.notify.NotifyDetailsViewDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyCreateCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyPatchCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByUserIdQueryDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByCodeUserQueryDTO;

public interface UserNotifyPort {

    NotifyDetailsViewDTO create(UserNotifyCreateCommandDTO command);

    NotifyDetailsViewDTO getByUserId(UserNotifyGetByUserIdQueryDTO query);

    NotifyDetailsViewDTO patchByUserId(UserNotifyPatchCommandDTO command);

    NotifyDetailsViewDTO getByCodeUser(UserNotifyGetByCodeUserQueryDTO query);

    NotifyDetailsViewDTO patchByCodeUser(UserNotifyPatchCommandDTO command);
}
