package com.keepguard.ms_user.application.service.usernotify;

import com.keepguard.ms_user.application.dto.notify.NotifyDetailsViewDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyCreateCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyPatchCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByUserIdQueryDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByCodeUserQueryDTO;
import com.keepguard.ms_user.application.port.in.UserNotifyPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotifyUseCaseService implements UserNotifyPort {

    private final UserNotifyCommandService userNotifyCommandService;
    private final UserNotifyQueryService userNotifyQueryService;

    @Override
    public NotifyDetailsViewDTO create(UserNotifyCreateCommandDTO command) {
        UUID userId = command.userId();
        log.info("🔍 USECASE - create chamado para usuário: {}", userId);
        try {
            var result = userNotifyCommandService.create(command);
            log.info("🔍 USECASE - create sucesso para usuário: {}", userId);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no create para usuário: {} - {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public NotifyDetailsViewDTO getByUserId(UserNotifyGetByUserIdQueryDTO query) {
        UUID userId = query.userId();
        log.info("🔍 USECASE - getByUserId chamado para usuário: {}", userId);
        try {
            var result = userNotifyQueryService.getByUserId(query);
            log.info("🔍 USECASE - getByUserId sucesso para usuário: {}", userId);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no getByUserId para usuário: {} - {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public NotifyDetailsViewDTO patchByUserId(UserNotifyPatchCommandDTO command) {
        UUID userId = command.userId();
        log.info("🔍 USECASE - patchByUserId chamado para usuário: {}", userId);
        try {
            var result = userNotifyCommandService.patchByUserId(command);
            log.info("🔍 USECASE - patchByUserId sucesso para usuário: {}", userId);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no patchByUserId para usuário: {} - {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public NotifyDetailsViewDTO getByCodeUser(UserNotifyGetByCodeUserQueryDTO query) {
        UUID codeUser = query.codeUser();
        log.info("🔍 USECASE - getByCodeUser chamado para codeUser: {}", codeUser);
        try {
            var result = userNotifyQueryService.getByCodeUser(query);
            log.info("🔍 USECASE - getByCodeUser sucesso para codeUser: {}", codeUser);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no getByCodeUser para codeUser: {} - {}", codeUser, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public NotifyDetailsViewDTO patchByCodeUser(UserNotifyPatchCommandDTO command) {
        UUID codeUser = command.codeUser();
        log.info("🔍 USECASE - patchByCodeUser chamado para codeUser: {}", codeUser);
        try {
            var result = userNotifyCommandService.patchByCodeUser(command);
            log.info("🔍 USECASE - patchByCodeUser sucesso para codeUser: {}", codeUser);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no patchByCodeUser para codeUser: {} - {}", codeUser, e.getMessage(), e);
            throw e;
        }
    }
}
