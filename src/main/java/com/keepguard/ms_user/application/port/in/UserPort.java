package com.keepguard.ms_user.application.port.in;

import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;

import java.util.List;
import java.util.UUID;

public interface UserPort {

    // === Commands ===
    UserDetailsViewDTO create(UserCreateCommandDTO command);

    UserDetailsViewDTO update(UserUpdateCommandDTO command);

    void delete(UserDeleteCommandDTO command);

    UserDetailsViewDTO activate(UserStatusChangeCommandDTO command);

    UserDetailsViewDTO deactivate(UserStatusChangeCommandDTO command);

    UserDetailsViewDTO block(UserStatusChangeCommandDTO command);

    UserDetailsViewDTO unblock(UserStatusChangeCommandDTO command);

    UserDetailsViewDTO suspend(UserStatusChangeCommandDTO command);

    UserDetailsViewDTO unsuspend(UserStatusChangeCommandDTO command);

    List<UserDetailsViewDTO> activateBatch(UserBatchStatusCommandDTO command);

    List<UserDetailsViewDTO> deactivateBatch(UserBatchStatusCommandDTO command);

    // === Queries ===
    UserDetailsViewDTO getById(UserGetByIdQueryDTO query);

    UserDetailsViewDTO getByCodeUser(UserGetByCodeUserQueryDTO query);

    UserDetailsViewDTO getByCodeUserForTenant(UUID codeUser, UUID companyId);

    UserDetailsViewDTO getByEmail(UserGetByEmailQueryDTO query);

    PageResultDTO<UserSearchViewDTO> search(UserSearchQueryDTO query);

}
