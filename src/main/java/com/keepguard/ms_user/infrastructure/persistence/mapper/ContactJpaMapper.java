package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.Contact;
import com.keepguard.ms_user.infrastructure.persistence.entity.ContactJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactJpaMapper {

    public Contact toDomain(ContactJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Contact.of(
            jpaEntity.getId(),
            jpaEntity.getUserId(),
            jpaEntity.getValue(),
            jpaEntity.getType(),
            jpaEntity.isPrimary(),
            jpaEntity.isActive(),
            jpaEntity.getDescription(),
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt()
        );
    }

    public ContactJpaEntity toJpa(Contact domain) {
        if (domain == null) {
            return null;
        }

        return ContactJpaEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .value(domain.getValue())
            .type(domain.getType())
            .primary(domain.isPrimary())
            .active(domain.isActive())
            .description(domain.getDescription())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    public List<Contact> toDomainList(List<ContactJpaEntity> jpaEntities) {
        if (jpaEntities == null) {
            return List.of();
        }

        return jpaEntities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public List<ContactJpaEntity> toJpaList(List<Contact> domains) {
        if (domains == null) {
            return List.of();
        }

        return domains.stream()
            .map(this::toJpa)
            .collect(Collectors.toList());
    }

    public void updateJpa(ContactJpaEntity jpaEntity, Contact domain) {
        if (jpaEntity == null || domain == null) {
            return;
        }

        jpaEntity.setValue(domain.getValue());
        jpaEntity.setType(domain.getType());
        jpaEntity.setPrimary(domain.isPrimary());
        jpaEntity.setActive(domain.isActive());
        jpaEntity.setDescription(domain.getDescription());
        jpaEntity.setUpdatedAt(domain.getUpdatedAt());
    }
}