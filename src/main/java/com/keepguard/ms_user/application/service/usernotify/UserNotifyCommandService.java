package com.keepguard.ms_user.application.service.usernotify;

import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.dto.notify.NotifyDetailsViewDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyCreateCommandDTO;
import com.keepguard.ms_user.application.dto.notify.UserNotifyPatchCommandDTO;
import com.keepguard.ms_user.application.mapper.NotifyApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.UserNotifyRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.exception.CommandOperationException;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.domain.entity.Notify;
import com.keepguard.ms_user.application.port.out.cache.NotifyCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotifyCommandService {

    private final UserNotifyRepositoryPort userNotifyRepositoryPort;
    private final NotifyCachePort notifyCachePort;
    private final NotifyApplicationMapper notifyApplicationMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final MetricsPort metricsPort;

    @LogOperation(
        operation = "CREATE_USER_NOTIFY",
        description = "Criando preferências de notificação para usuário: {command.userId()}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "USER_NOTIFY"
    )
    @Transactional
    public NotifyDetailsViewDTO create(UserNotifyCreateCommandDTO command) {
        UUID userId = command.userId();
        log.info("Criando preferências de notificação para usuário: {}", userId);

        // Verificar se o usuário existe primeiro
        if (!userRepositoryPort.existsById(userId)) {
            throw new NotFoundException("Usuário não encontrado com ID: " + userId, "USER_NOT_FOUND", Map.of("userId", userId));
        }

        // Verificar se já existem preferências para este usuário
        if (userNotifyRepositoryPort.findByUserId(userId).isPresent()) {
            throw new AlreadyExistsException("Preferências de notificação já existem para este usuário", "NOTIFY_ALREADY_EXISTS", Map.of("userId", userId));
        }

        // Criar preferências usando o mapper
        var notify = notifyApplicationMapper.toCreateCommand(command);
        var saved = userNotifyRepositoryPort.save(notify);

        // Não cachear no create - deixar QueryService fazer o cache quando necessário
        var notifyDetailsView = notifyApplicationMapper.toDetailsView(saved);

        metricsPort.incrementCounter("user_notify_created_total",
            Map.of("entity_id", userId.toString()));

        log.info("Preferências de notificação criadas com sucesso para usuário: {}", userId);
        return notifyDetailsView;
    }

    @LogOperation(
        operation = "CREATE_USER_NOTIFY_DEFAULTS",
        description = "Criando preferências de notificação padrão para usuário: {userId}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "USER_NOTIFY"
    )
    @Transactional
    public NotifyDetailsViewDTO createDefaults(UUID userId) {
        log.info("Criando preferências de notificação padrão para usuário: {}", userId);

        var notify = Notify.createDefaults(userId);
        var saved = userNotifyRepositoryPort.save(notify);

        // Não cachear no create - deixar QueryService fazer o cache quando necessário
        var notifyDetailsView = notifyApplicationMapper.toDetailsView(saved);

        log.info("Preferências de notificação criadas com sucesso para usuário: {}", userId);
        return notifyDetailsView;
    }

    @LogOperation(
        operation = "UPDATE_USER_NOTIFY",
        description = "Atualizando preferências de notificação para usuário: {userId}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER_NOTIFY"
    )
    @Transactional
    public NotifyDetailsViewDTO patchByUserId(UserNotifyPatchCommandDTO command) {
        UUID userId = command.userId();
        
        // Verificar se o usuário existe primeiro
        if (!userRepositoryPort.existsById(userId)) {
            throw new NotFoundException("Usuário não encontrado com ID: " + userId, "USER_NOT_FOUND", Map.of("userId", userId));
        }

        // Buscar preferências existentes ou criar padrões
        var notify = userNotifyRepositoryPort.findByUserId(userId)
                .orElseGet(() -> {
                    var defaultNotify = Notify.createDefaults(userId);
                    return userNotifyRepositoryPort.save(defaultNotify);
                });

        // Aplicar mudanças
        notifyApplicationMapper.applyChanges(notify, command);

        // Verificar se houve mudanças
        boolean hasChanges = command.notifyEmail() != null ||
                           command.notifySms() != null ||
                           command.notifyWhatsapp() != null ||
                           command.notifyPush() != null;

        if (!hasChanges) {
            // Retornar as preferências atuais sem fazer alterações
            return notifyApplicationMapper.toDetailsView(notify);
        }

        // Atualizar timestamp
        notify.setUpdatedAt(OffsetDateTime.now());

        // Salvar
        var saved = userNotifyRepositoryPort.save(notify);

        // Limpar cache - QueryService fará novo cache quando necessário
        var notifyDetailsView = notifyApplicationMapper.toDetailsView(saved);
        notifyCachePort.removeNotifyFromCacheByUserId(userId.toString());

        metricsPort.incrementCounter("user_notify_updated_total",
            Map.of("entity_id", userId.toString()));

        return notifyDetailsView;
    }

    @LogOperation(
        operation = "DELETE_USER_NOTIFY",
        description = "Removendo preferências de notificação para usuário: {userId}",
        audit = true,
        auditAction = "DELETE",
        auditEntityType = "USER_NOTIFY"
    )
    @Transactional
    public void deleteByUserId(UUID userId) {
        log.info("Deletando preferências de notificação para usuário: {}", userId);

        userNotifyRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Preferências de notificação não encontradas para usuário: " + userId, "NOTIFY_NOT_FOUND", Map.of("userId", userId)));

        // Remover do cache
        notifyCachePort.removeNotifyFromCacheByUserId(userId.toString());

        // Deletar do banco
        userNotifyRepositoryPort.deleteByUserId(userId);

        metricsPort.incrementCounter("user_notify_deleted_total",
            Map.of("entity_id", userId.toString()));

        log.info("Preferências de notificação deletadas com sucesso para usuário: {}", userId);
    }

    @LogOperation(
        operation = "UPDATE_USER_NOTIFY_BY_CODE",
        description = "Atualizando preferências de notificação para codeUser: {codeUser}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER_NOTIFY"
    )
    @Transactional
    public NotifyDetailsViewDTO patchByCodeUser(UserNotifyPatchCommandDTO command) {
        UUID codeUser = command.codeUser();
        log.info("Atualizando preferências de notificação para codeUser: {}", codeUser);

        try {
            // Primeiro buscar o usuário pelo codeUser para obter o userId
            var user = userRepositoryPort.findByCodeUser(codeUser)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado com codeUser: " + codeUser, "USER_NOT_FOUND", Map.of("codeUser", codeUser)));
            UUID userId = user.getId();

            // Criar um novo command com o userId correto
            var updatedCommand = new UserNotifyPatchCommandDTO(
                userId,
                codeUser,
                command.companyId(),
                command.notifyEmail(),
                command.notifySms(),
                command.notifyWhatsapp(),
                command.notifyPush()
            );

            return patchByUserId(updatedCommand);
        } catch (NotFoundException e) {
            throw new NotFoundException("Usuário não encontrado com codeUser: " + codeUser, "USER_NOT_FOUND", Map.of("codeUser", codeUser));
        } catch (Exception e) {
            log.error("Erro ao atualizar preferências para codeUser: {} - Erro: {}", codeUser, e.getMessage(), e);
            throw new CommandOperationException("Falha ao atualizar preferências por codeUser", "patchByCodeUser", "NOTIFY_COMMAND_ERROR", Map.of("codeUser", codeUser), e);
        }
    }
}
