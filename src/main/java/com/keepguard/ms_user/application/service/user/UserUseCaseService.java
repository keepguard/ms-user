package com.keepguard.ms_user.application.service.user;

import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.in.UserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUseCaseService implements UserPort {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    // === Commands ===

    @Override
    public UserDetailsViewDTO create(UserCreateCommandDTO command) {
        return userCommandService.create(command);
    }

    @Override
    public UserDetailsViewDTO update(UserUpdateCommandDTO command) {
        return userCommandService.update(command);
    }

    @Override
    public void delete(UserDeleteCommandDTO command) {
        userCommandService.delete(command);
    }

    @Override
    public UserDetailsViewDTO activate(UserStatusChangeCommandDTO command) {
        return userCommandService.activate(command);
    }

    @Override
    public UserDetailsViewDTO deactivate(UserStatusChangeCommandDTO command) {
        return userCommandService.deactivate(command);
    }

    @Override
    public UserDetailsViewDTO block(UserStatusChangeCommandDTO command) {
        return userCommandService.block(command);
    }

    @Override
    public UserDetailsViewDTO unblock(UserStatusChangeCommandDTO command) {
        return userCommandService.unblock(command);
    }

    @Override
    public UserDetailsViewDTO suspend(UserStatusChangeCommandDTO command) {
        return userCommandService.suspend(command);
    }

    @Override
    public UserDetailsViewDTO unsuspend(UserStatusChangeCommandDTO command) {
        return userCommandService.unsuspend(command);
    }

    @Override
    public List<UserDetailsViewDTO> activateBatch(UserBatchStatusCommandDTO command) {
        return userCommandService.activateBatch(command);
    }

    @Override
    public List<UserDetailsViewDTO> deactivateBatch(UserBatchStatusCommandDTO command) {
        return userCommandService.deactivateBatch(command);
    }

    // === Queries ===

    @Override
    public UserDetailsViewDTO getById(UserGetByIdQueryDTO query) {
        return userQueryService.getById(query);
    }

    @Override
    public UserDetailsViewDTO getByCodeUser(UserGetByCodeUserQueryDTO query) {
        return userQueryService.getByCodeUser(query);
    }

    @Override
    public UserDetailsViewDTO getByEmail(UserGetByEmailQueryDTO query) {
        return userQueryService.getByEmail(query);
    }

    @Override
    public PageResultDTO<UserSearchViewDTO> search(UserSearchQueryDTO query) {
        return userQueryService.search(query);
    }

}
