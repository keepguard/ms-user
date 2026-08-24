package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.Notify;
import com.keepguard.ms_user.infrastructure.persistence.entity.NotifyJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifyJpaMapper {

    public Notify toDomain(NotifyJpaEntity entity) {
        log.info("🔍 JPA MAPPER - Convertendo NotifyJpaEntity para Notify");
        if (entity == null) {
            log.info("🔍 JPA MAPPER - Entity é null, retornando null");
            return null;
        }

        try {
            var domain = Notify.of(
                entity.getUserId(),
                entity.isNotifyEmail(),
                entity.isNotifySms(),
                entity.isNotifyWhatsapp(),
                entity.isNotifyPush(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
            );
            log.info("🔍 JPA MAPPER - Domain criado com sucesso");
            return domain;
        } catch (Exception e) {
            log.error("🔍 JPA MAPPER - ERRO ao criar domain: {}", e.getMessage(), e);
            throw e;
        }
    }

    public NotifyJpaEntity toEntity(Notify domain) {
        log.info("🔍 JPA MAPPER - Convertendo Notify para NotifyJpaEntity");
        if (domain == null) {
            log.info("🔍 JPA MAPPER - Domain é null, retornando null");
            return null;
        }

        try {
            boolean isNew = domain.getCreatedAt() == null || domain.getVersion() == null || domain.getVersion() == 0L;
            var entity = NotifyJpaEntity.builder()
                .userId(domain.getUserId())
                .isNew(isNew)
                .notifyEmail(domain.isNotifyEmail())
                .notifySms(domain.isNotifySms())
                .notifyWhatsapp(domain.isNotifyWhatsapp())
                .notifyPush(domain.isNotifyPush())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(domain.getVersion())
                .build();
            log.info("🔍 JPA MAPPER - Entity criada com sucesso (isNew={})", isNew);
            return entity;
        } catch (Exception e) {
            log.error("🔍 JPA MAPPER - ERRO ao criar entity: {}", e.getMessage(), e);
            throw e;
        }
    }
}
