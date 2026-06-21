package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.port.out.persistence.UserNotifyRepositoryPort;
import com.keepguard.ms_user.domain.entity.Notify;
import com.keepguard.ms_user.infrastructure.persistence.entity.NotifyJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.NotifyJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.NotifySpringRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;

@Slf4j
@Repository
@RequiredArgsConstructor
@Retry(name = "databaseOperation")
@Bulkhead(name = "databaseOperation")
public class UserNotifyRepositoryAdapter implements UserNotifyRepositoryPort {

    private final NotifySpringRepository springRepository;
    private final NotifyJpaMapper mapper;

    @Override
    public Notify save(Notify notify) {
        log.info("🔍 REPOSITORY - Salvando Notify");
        try {
            log.info("🔍 REPOSITORY - Passo 1: Convertendo para entity");
            var entity = mapper.toEntity(notify);
            log.info("🔍 REPOSITORY - Passo 1 OK: Entity criada");

            log.info("🔍 REPOSITORY - Passo 2: Salvando no banco");
            var savedEntity = springRepository.save(entity);
            log.info("🔍 REPOSITORY - Passo 2 OK: Salvo no banco");

            log.info("🔍 REPOSITORY - Passo 3: Convertendo para domain");
            var domain = mapper.toDomain(savedEntity);
            log.info("🔍 REPOSITORY - Passo 3 OK: Domain criado");

            log.info("🔍 REPOSITORY - SUCESSO: Notify salvo");
            return domain;
        } catch (Exception e) {
            log.error("🔍 REPOSITORY - ERRO ao salvar: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Notify> findByUserId(UUID userId) {
        return springRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Notify> findAllByUserIdIn(List<UUID> userIds) {
        return springRepository.findAllByUserIdIn(userIds).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springRepository.deleteByUserId(userId);
    }

    @Override
    public void delete(Notify notify) {
        NotifyJpaEntity entity = mapper.toEntity(notify);
        springRepository.delete(entity);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return springRepository.existsByUserId(userId);
    }

    @Override
    public List<Notify> findAllByNotifyEmail(boolean notifyEmail) {
        return springRepository.findAllByNotifyEmail(notifyEmail).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notify> findAllByNotifySms(boolean notifySms) {
        return springRepository.findAllByNotifySms(notifySms).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notify> findAllByNotifyWhatsapp(boolean notifyWhatsapp) {
        return springRepository.findAllByNotifyWhatsapp(notifyWhatsapp).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notify> findAllByNotifyPush(boolean notifyPush) {
        return springRepository.findAllByNotifyPush(notifyPush).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
