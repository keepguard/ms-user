package com.keepguard.ms_user.application.service.usernotify;

import com.keepguard.ms_user.application.dto.notify.NotifySimpleViewDTO;
import com.keepguard.ms_user.application.dto.notify.NotifyDetailsViewDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByUserIdQueryDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyGetByCodeUserQueryDTO;
import com.keepguard.ms_user.application.mapper.NotifyApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.UserNotifyRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.port.out.cache.NotifyCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotifyQueryService {

    private final UserNotifyRepositoryPort userNotifyRepositoryPort;
    private final NotifyCachePort notifyCachePort;
    private final NotifyApplicationMapper notifyApplicationMapper;
    private final UserRepositoryPort userRepositoryPort;

    @Transactional(readOnly = true)
    public NotifyDetailsViewDTO getByUserId(UserNotifyGetByUserIdQueryDTO query) {
        UUID userId = query.userId();
        
        // Verificar se o usuário existe primeiro
        if (!userRepositoryPort.existsById(userId)) {
            throw new NotFoundException("Usuário não encontrado com ID: " + userId, "USER_NOT_FOUND", Map.of("userId", userId));
        }

        // Tentar cache primeiro
        var cachedNotify = notifyCachePort.getNotifyByUserIdFromCache(userId.toString());
        if (cachedNotify != null) {
            return notifyApplicationMapper.toDetailsView(cachedNotify);
        }

        // Buscar no banco - se não existir, criar preferências padrão
        var notify = userNotifyRepositoryPort.findByUserId(userId);

        if (notify.isEmpty()) {
            // Criar preferências padrão
            var defaultNotify = com.keepguard.ms_user.domain.entity.Notify.createDefaults(userId);
            var saved = userNotifyRepositoryPort.save(defaultNotify);
            var notifyView = notifyApplicationMapper.toView(saved);
            var notifyDetailsView = notifyApplicationMapper.toDetailsView(saved);

            // Cachear resultado (usando NotifyViewDTO para compatibilidade com cache)
            notifyCachePort.cacheNotifyByUserId(userId.toString(), notifyView);

            return notifyDetailsView;
        }

        var notifyView = notifyApplicationMapper.toView(notify.get());
        var notifyDetailsView = notifyApplicationMapper.toDetailsView(notify.get());

        // Cachear resultado (usando NotifyViewDTO para compatibilidade com cache)
        notifyCachePort.cacheNotifyByUserId(userId.toString(), notifyView);

        return notifyDetailsView;
    }

    @Transactional(readOnly = true)
    public List<NotifySimpleViewDTO> getAllByUserIdIn(List<UUID> userIds) {
        var notifies = userNotifyRepositoryPort.findAllByUserIdIn(userIds);

        return notifies.stream()
                .map(notifyApplicationMapper::toSimpleView)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotifySimpleViewDTO> getAllByNotifyEmail(boolean notifyEmail) {
        var notifies = userNotifyRepositoryPort.findAllByNotifyEmail(notifyEmail);

        return notifies.stream()
                .map(notifyApplicationMapper::toSimpleView)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotifySimpleViewDTO> getAllByNotifySms(boolean notifySms) {
        var notifies = userNotifyRepositoryPort.findAllByNotifySms(notifySms);

        return notifies.stream()
                .map(notifyApplicationMapper::toSimpleView)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotifySimpleViewDTO> getAllByNotifyWhatsapp(boolean notifyWhatsapp) {
        var notifies = userNotifyRepositoryPort.findAllByNotifyWhatsapp(notifyWhatsapp);

        return notifies.stream()
                .map(notifyApplicationMapper::toSimpleView)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotifySimpleViewDTO> getAllByNotifyPush(boolean notifyPush) {
        var notifies = userNotifyRepositoryPort.findAllByNotifyPush(notifyPush);

        return notifies.stream()
                .map(notifyApplicationMapper::toSimpleView)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotifyDetailsViewDTO getByCodeUser(UserNotifyGetByCodeUserQueryDTO query) {
        UUID codeUser = query.codeUser();
        
        // Primeiro buscar o usuário pelo codeUser para obter o userId
        var user = userRepositoryPort.findByCodeUser(codeUser)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com codeUser: " + codeUser, "USER_NOT_FOUND", Map.of("codeUser", codeUser)));
        UUID userId = user.getId();

        // Tentar cache primeiro
        var cachedNotify = notifyCachePort.getNotifyByUserIdFromCache(userId.toString());
        if (cachedNotify != null) {
            return notifyApplicationMapper.toDetailsView(cachedNotify);
        }

        // Buscar no banco - se não existir, criar preferências padrão
        var notify = userNotifyRepositoryPort.findByUserId(userId);

        if (notify.isEmpty()) {
            // Criar preferências padrão
            var defaultNotify = com.keepguard.ms_user.domain.entity.Notify.createDefaults(userId);
            var saved = userNotifyRepositoryPort.save(defaultNotify);
            var notifyView = notifyApplicationMapper.toView(saved);
            var notifyDetailsView = notifyApplicationMapper.toDetailsView(saved);

            // Cachear resultado (usando NotifyViewDTO para compatibilidade com cache)
            notifyCachePort.cacheNotifyByUserId(userId.toString(), notifyView);

            return notifyDetailsView;
        }

        var notifyView = notifyApplicationMapper.toView(notify.get());
        var notifyDetailsView = notifyApplicationMapper.toDetailsView(notify.get());

        // Cachear resultado (usando NotifyViewDTO para compatibilidade com cache)
        notifyCachePort.cacheNotifyByUserId(userId.toString(), notifyView);

        return notifyDetailsView;
    }
}
