package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserJpaMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.of(
            entity.getId(),
            entity.getCodeUser(),
            entity.getCompanyId(),
            entity.getTenantId(),
            entity.getType(),
            entity.getStatus(),
            entity.getEmail(),
            entity.getPhoneE164(),
            entity.getPreferredLocale(),
            entity.getTimezone(),
            entity.getAvatarUrl(),
            entity.getDisplayHandle(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserJpaEntity.builder()
            .id(domain.getId())
            .codeUser(domain.getCodeUser())
            .companyId(domain.getCompanyId())
            .tenantId(domain.getTenantId())
            .type(domain.getType())
            .status(domain.getStatus())
            .email(domain.getEmail())
            .phoneE164(domain.getPhoneE164())
            .preferredLocale(domain.getPreferredLocale())
            .timezone(domain.getTimezone())
            .avatarUrl(domain.getAvatarUrl())
            .displayHandle(domain.getDisplayHandle())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}
