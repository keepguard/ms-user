package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.infrastructure.persistence.entity.CompanyProfileJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CompanyProfileJpaMapper {

    public CompanyProfile toDomain(CompanyProfileJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return CompanyProfile.of(
            entity.getUserId(),
            entity.getCompanyId(),
            entity.getLegalNameSnapshot(),
            entity.getCnpjSnapshot(),
            entity.getStateRegistrationSnapshot(),
            entity.getMunicipalRegistrationSnapshot(),
            entity.getRepresentativeName(),
            entity.getRepresentativeCpf(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public CompanyProfileJpaEntity toEntity(CompanyProfile domain) {
        if (domain == null) {
            return null;
        }

        // Mapper não seta relacionamento - responsabilidade do Adapter
        return CompanyProfileJpaEntity.builder()
            .userId(domain.getUserId())
            .companyId(domain.getCompanyId())
            .legalNameSnapshot(domain.getLegalNameSnapshot())
            .cnpjSnapshot(domain.getCnpjSnapshot())
            .stateRegistrationSnapshot(domain.getStateRegistrationSnapshot())
            .municipalRegistrationSnapshot(domain.getMunicipalRegistrationSnapshot())
            .representativeName(domain.getRepresentativeName())
            .representativeCpf(domain.getRepresentativeCpf())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}
