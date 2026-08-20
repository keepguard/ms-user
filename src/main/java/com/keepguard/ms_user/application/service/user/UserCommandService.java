package com.keepguard.ms_user.application.service.user;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.dto.profile.ProfileCommandDTO;
import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.application.mapper.UserApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.service.user.strategy.profile.ProfileStrategyFactory;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.application.port.out.cache.UserCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {

    private final UserRepositoryPort userRepositoryPort;
    private final ProfileStrategyFactory profileStrategyFactory;
    private final UserCachePort userCachePort;
    private final UserApplicationMapper userApplicationMapper;
    private final MetricsPort metricsPort;
    private final com.keepguard.ms_user.application.port.out.persistence.PersonProfileRepositoryPort personProfileRepositoryPort;


    @LogOperation(
        operation = "CREATE_USER",
        description = "Criando novo usuário: {command.email}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO create(UserCreateCommandDTO command) {
        log.info("Criando usuário com email: {}, tipo: {}, companyId: {}, tenantId: {}, tem dados de perfil: {}", 
                command.email(), command.type(), command.companyId(), command.tenantId(), command.hasProfileData());

        // Validar se email já existe
        if (userRepositoryPort.existsByEmail(command.email())) {
            metricsPort.incrementCounter("user_business_errors_total",
                Map.of("error_code", "EMAIL_ALREADY_EXISTS", "operation", "create"));
            throw new AlreadyExistsException("Email já está em uso: " + command.email(), "EMAIL_ALREADY_EXISTS", Map.of("email", command.email()));
        }

        // Validar se CPF já existe para esta aplicação (se informado e for pessoa física)
        if (command.hasProfileData() && command.type() == com.keepguard.ms_user.domain.enums.UserTypeEnum.PERSON) {
            var personProfile = (com.keepguard.ms_user.domain.entity.PersonProfile) command.getProfileData();
            if (personProfile != null) {
                // Validar CPF
                if (personProfile.getCpf() != null && !personProfile.getCpf().trim().isEmpty()) {
                    String cleanedCpf = personProfile.getCpf().replaceAll("[^0-9]", "");
                    if (personProfileRepositoryPort.existsByCpfAndTenantId(cleanedCpf, command.tenantId())) {
                        metricsPort.incrementCounter("user_business_errors_total",
                            Map.of("error_code", "CPF_ALREADY_EXISTS", "operation", "create"));
                        throw new AlreadyExistsException("CPF já está em uso nesta aplicação", "CPF_ALREADY_EXISTS", Map.of("cpf", cleanedCpf));
                    }
                }
                
                // Validar display_handle (unicidade por company_id) - em users
                String displayHandle = command.displayHandle();
                if (displayHandle != null && !displayHandle.trim().isEmpty()) {
                    UUID tempExcludeUserId = UUID.randomUUID();
                    if (userRepositoryPort.existsByDisplayHandleAndCompanyId(
                            displayHandle,
                            command.companyId(),
                            tempExcludeUserId)) {
                        metricsPort.incrementCounter("user_business_errors_total",
                            Map.of("error_code", "DISPLAY_HANDLE_ALREADY_EXISTS", "operation", "create"));
                        throw new AlreadyExistsException(
                                "display_handle já está em uso nesta empresa",
                                "DISPLAY_HANDLE_ALREADY_EXISTS",
                                Map.of("displayHandle", displayHandle, "companyId", command.companyId().toString()));
                    }
                }
            }
        }

        // Criar usuário usando o mapper
        var user = userApplicationMapper.toDomain(command);
        if (command.displayHandle() != null && !command.displayHandle().trim().isEmpty()) {
            user.setDisplayHandle(command.displayHandle());
        }
        user.activate();

        var userSaved = userRepositoryPort.save(user);

        // Usar Strategy Pattern para criar perfil apropriado
        if (command.hasProfileData()) {
            createUserProfile(userSaved, command);
        }

        // Não cachear no create - deixar QueryService fazer o cache quando necessário
        var userView = userApplicationMapper.toDetailsView(userSaved);

        metricsPort.incrementCounter("user_created_total",
            Map.of("entity_id", userSaved.getId().toString(), "type", userSaved.getType().name()));

        log.info("Usuário criado com sucesso: {} - {}", userSaved.getId(), userSaved.getEmail());
        return userView;
    }

    @LogOperation(
        operation = "UPDATE_USER",
        description = "Atualizando usuário: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO update(UserUpdateCommandDTO command) {
        log.info("Atualizando usuário: {}, tenantId: {}", command.id(), command.tenantId());

        var before = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        // Validar campos imutáveis
        if (command.companyId().isPresent() || command.codeUser().isPresent() || command.type().isPresent()) {
            log.warn("Ignorando campos imutáveis companyId/codeUser/type na atualização");
        }

        // Validar email se fornecido
        if (command.email().isPresent()) {
            var newEmail = command.email().get();
            if (!Objects.equals(before.getEmail(), newEmail) && userRepositoryPort.existsByEmail(newEmail)) {
                throw new AlreadyExistsException("Email já está em uso: " + newEmail);
            }
        }

        // Validar display_handle se fornecido (unicidade em users)
        command.displayHandle().ifPresent(displayHandle -> {
            if (displayHandle != null && !displayHandle.trim().isEmpty()) {
                String currentDisplayHandle = before.getDisplayHandle();
                if (!Objects.equals(currentDisplayHandle, displayHandle)) {
                    if (userRepositoryPort.existsByDisplayHandleAndCompanyId(
                            displayHandle,
                            before.getCompanyId(),
                            before.getId())) {
                        metricsPort.incrementCounter("user_business_errors_total",
                            Map.of("error_code", "DISPLAY_HANDLE_ALREADY_EXISTS", "operation", "update"));
                        throw new AlreadyExistsException(
                                "display_handle já está em uso nesta empresa",
                                "DISPLAY_HANDLE_ALREADY_EXISTS",
                                Map.of("displayHandle", displayHandle, "companyId", before.getCompanyId().toString()));
                    }
                }
            }
        });

        // Aplicar mudanças
        User after;
        try {
            after = userApplicationMapper.applyChanges(before, command);
            command.displayHandle().filter(dh -> dh != null && !dh.trim().isEmpty()).ifPresent(after::setDisplayHandle);
            after.setUpdatedAt(OffsetDateTime.now());
        } catch (ValidationException ex) {
            throw new ValidationException("Dados inválidos: " + ex.getMessage());
        }

        // Salvar usuário
        var userSaved = userRepositoryPort.save(after);

        // Usar Strategy Pattern para atualizar perfil apropriado
        updateUserProfile(userSaved, command);

        // Limpar cache - QueryService fará novo cache quando necessário
        userCachePort.removeUserFromCache(userSaved);
        
        var userView = userApplicationMapper.toDetailsView(userSaved);

        metricsPort.incrementCounter("user_updated_total",
            Map.of("entity_id", userSaved.getId().toString()));

        log.info("Usuário atualizado com sucesso: {} - {}", userSaved.getId(), userSaved.getEmail());
        return userView;
    }

    @LogOperation(
        operation = "DELETE_USER",
        description = "Removendo usuário: {id}",
        audit = true,
        auditAction = "DELETE",
        auditEntityType = "USER"
    )
    @Transactional
    public void delete(UserDeleteCommandDTO command) {
        log.info("Deletando usuário: {}, tenantId: {}", command.id(), command.tenantId());

        var user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        // Usar Strategy Pattern para remover perfil apropriado
        deleteUserProfile(user);

        // Remover do cache
        userCachePort.removeUserFromCache(user);

        // Deletar do banco
        userRepositoryPort.deleteById(command.id());

        metricsPort.incrementCounter("user_deleted_total",
            Map.of("entity_id", command.id().toString()));

        log.info("Usuário deletado com sucesso: {}", command.id());
    }


    @LogOperation(
        operation = "ACTIVATE_USER",
        description = "Ativando usuário: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO activate(UserStatusChangeCommandDTO command) {
        log.info("Ativando usuário: {}, tenantId: {}, motivo: {}", command.id(), command.tenantId(), command.reason());

        var user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        var previousStatus = user.getStatus();
        user.activate();

        var userSaved = userRepositoryPort.save(user);

        // Limpar cache - QueryService fará novo cache quando necessário
        userCachePort.removeUserFromCache(userSaved);
        
        var userView = userApplicationMapper.toDetailsView(userSaved);

        log.info("Usuário ativado com sucesso: {} - Status anterior: {} -> Novo status: {}",
                command.id(), previousStatus, userSaved.getStatus());
        return userView;
    }

    @LogOperation(
        operation = "DEACTIVATE_USER",
        description = "Desativando usuário: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO deactivate(UserStatusChangeCommandDTO command) {
        log.info("Desativando usuário: {}, tenantId: {}, motivo: {}", command.id(), command.tenantId(), command.reason());

        var user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        var previousStatus = user.getStatus();
        user.deactivate();

        var userSaved = userRepositoryPort.save(user);

        // Limpar cache - QueryService fará novo cache quando necessário
        userCachePort.removeUserFromCache(userSaved);
        
        var userView = userApplicationMapper.toDetailsView(userSaved);

        log.info("Usuário desativado com sucesso: {} - Status anterior: {} -> Novo status: {}",
                command.id(), previousStatus, userSaved.getStatus());
        return userView;
    }

    @LogOperation(
        operation = "BLOCK_USER",
        description = "Bloqueando usuário: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO block(UserStatusChangeCommandDTO command) {
        log.info("Bloqueando usuário: {}, tenantId: {}, motivo: {}", command.id(), command.tenantId(), command.reason());

        var user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        var previousStatus = user.getStatus();
        user.block();

        var userSaved = userRepositoryPort.save(user);

        // Limpar cache - QueryService fará novo cache quando necessário
        userCachePort.removeUserFromCache(userSaved);
        
        var userView = userApplicationMapper.toDetailsView(userSaved);

        log.info("Usuário bloqueado com sucesso: {} - Status anterior: {} -> Novo status: {}",
                command.id(), previousStatus, userSaved.getStatus());
        return userView;
    }

    @LogOperation(
        operation = "UNBLOCK_USER",
        description = "Desbloqueando usuário: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO unblock(UserStatusChangeCommandDTO command) {
        log.info("Desbloqueando usuário: {}, tenantId: {}, motivo: {}", command.id(), command.tenantId(), command.reason());

        var user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        if (user.getStatus() != UserStatusEnum.BLOCKED) {
            throw new ValidationException("Usuário não está bloqueado: " + command.id());
        }

        var previousStatus = user.getStatus();
        user.setStatus(UserStatusEnum.ACTIVE); // Desbloqueio vai para ativo

        var userSaved = userRepositoryPort.save(user);

        // Limpar cache - QueryService fará novo cache quando necessário
        userCachePort.removeUserFromCache(userSaved);
        
        var userView = userApplicationMapper.toDetailsView(userSaved);

        log.info("Usuário desbloqueado com sucesso: {} - Status anterior: {} -> Novo status: {}",
                command.id(), previousStatus, userSaved.getStatus());
        return userView;
    }

    @LogOperation(
        operation = "SUSPEND_USER",
        description = "Suspendo usuário: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO suspend(UserStatusChangeCommandDTO command) {
        log.info("Suspendo usuário: {}, tenantId: {}, motivo: {}", command.id(), command.tenantId(), command.reason());

        var user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        var previousStatus = user.getStatus();
        user.suspend();

        var userSaved = userRepositoryPort.save(user);

        // Limpar cache - QueryService fará novo cache quando necessário
        userCachePort.removeUserFromCache(userSaved);
        
        var userView = userApplicationMapper.toDetailsView(userSaved);

        log.info("Usuário suspenso com sucesso: {} - Status anterior: {} -> Novo status: {}",
                command.id(), previousStatus, userSaved.getStatus());
        return userView;
    }

    @LogOperation(
        operation = "UNSUSPEND_USER",
        description = "Reativando usuário suspenso: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public UserDetailsViewDTO unsuspend(UserStatusChangeCommandDTO command) {
        log.info("Reativando usuário suspenso: {}, tenantId: {}, motivo: {}", command.id(), command.tenantId(), command.reason());

        var user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + command.id(), "USER_NOT_FOUND", Map.of("userId", command.id())));

        if (user.getStatus() != UserStatusEnum.SUSPENDED) {
            throw new ValidationException("Usuário não está suspenso: " + command.id());
        }

        var previousStatus = user.getStatus();
        user.setStatus(UserStatusEnum.ACTIVE); // Reativação vai para ativo

        var userSaved = userRepositoryPort.save(user);

        // Limpar cache - QueryService fará novo cache quando necessário
        userCachePort.removeUserFromCache(userSaved);
        
        var userView = userApplicationMapper.toDetailsView(userSaved);

        log.info("Usuário reativado com sucesso: {} - Status anterior: {} -> Novo status: {}",
                command.id(), previousStatus, userSaved.getStatus());
        return userView;
    }


    @LogOperation(
        operation = "ACTIVATE_USERS_BATCH",
        description = "Ativando usuários em lote: {userIds}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public List<UserDetailsViewDTO> activateBatch(UserBatchStatusCommandDTO command) {
        log.info("Ativando usuários em lote: {}, tenantId: {}, motivo: {}", command.userIds(), command.tenantId(), command.reason());

        var users = userRepositoryPort.findAllByIdIn(command.userIds());
        if (users.size() != command.userIds().size()) {
            throw new ValidationException("Alguns usuários não foram encontrados");
        }

        var activatedUsers = users.stream()
                .peek(User::activate)
                .map(userRepositoryPort::save)
                .peek(userCachePort::removeUserFromCache) // Limpar cache - QueryService fará novo cache quando necessário
                .map(userApplicationMapper::toDetailsView)
                .toList();

        log.info("Usuários ativados em lote com sucesso: {} usuários", activatedUsers.size());
        return activatedUsers;
    }

    @LogOperation(
        operation = "DEACTIVATE_USERS_BATCH",
        description = "Desativando usuários em lote: {userIds}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "USER"
    )
    @Transactional
    public List<UserDetailsViewDTO> deactivateBatch(UserBatchStatusCommandDTO command) {
        log.info("Desativando usuários em lote: {}, tenantId: {}, motivo: {}", command.userIds(), command.tenantId(), command.reason());

        var users = userRepositoryPort.findAllByIdIn(command.userIds());
        if (users.size() != command.userIds().size()) {
            throw new ValidationException("Alguns usuários não foram encontrados");
        }

        var deactivatedUsers = users.stream()
                .peek(User::deactivate)
                .map(userRepositoryPort::save)
                .peek(userCachePort::removeUserFromCache) // Limpar cache - QueryService fará novo cache quando necessário
                .map(userApplicationMapper::toDetailsView)
                .toList();

        log.info("Usuários desativados em lote com sucesso: {} usuários", deactivatedUsers.size());
        return deactivatedUsers;
    }


    private void createUserProfile(User user, UserCreateCommandDTO command) {
        log.info("Criando perfil para usuário: {}, tenantId: {}", user.getId(), command.tenantId());
        createProfileUsingStrategy(user, command, "criar");
    }

    private void updateUserProfile(User user, UserUpdateCommandDTO command) {
        log.info("Atualizando perfil para usuário: {}, tenantId: {}", user.getId(), command.tenantId());
        updateProfileUsingStrategy(user, command, "atualizar");
    }

    private void createProfileUsingStrategy(User user, ProfileCommandDTO command, String action) {
        log.info("Iniciando {} de perfil para usuário: {} tipo: {}", action, user.getId(), user.getType());

        var strategy = profileStrategyFactory.getStrategy(user.getType());

        if (command.hasProfileData()) {
            strategy.createProfile(user, command.getProfileData());
            log.info("Perfil {} com sucesso para usuário: {} tipo: {}", 
                    action, user.getId(), user.getType());
        }
    }

    private void updateProfileUsingStrategy(User user, ProfileCommandDTO command, String action) {
        var strategy = profileStrategyFactory.getStrategy(user.getType());

        if (command.hasProfileData()) {
            strategy.updateProfile(user.getId(), command.getProfileData());
            log.info("Perfil {} com sucesso para usuário: {} tipo: {}", 
                    action, user.getId(), user.getType());
        }
    }

    private void deleteUserProfile(User user) {
        log.info("Deletando perfil para usuário: {} tipo: {}", user.getId(), user.getType());
        
        var strategy = profileStrategyFactory.getStrategy(user.getType());

        strategy.deleteProfile(user.getId());
        log.info("Perfil deletado com sucesso para usuário: {} tipo: {}", 
                user.getId(), user.getType());
    }
}