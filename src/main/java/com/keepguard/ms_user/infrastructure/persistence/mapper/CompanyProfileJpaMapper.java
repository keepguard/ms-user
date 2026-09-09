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
            entity.getId(),
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

        return CompanyProfileJpaEntity.builder()
            .id(domain.getId())
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

    public void applyToExisting(CompanyProfile domain, CompanyProfileJpaEntity existing) {
        existing.setCompanyId(domain.getCompanyId());
        existing.setLegalNameSnapshot(domain.getLegalNameSnapshot());
        existing.setCnpjSnapshot(domain.getCnpjSnapshot());
        existing.setStateRegistrationSnapshot(domain.getStateRegistrationSnapshot());
        existing.setMunicipalRegistrationSnapshot(domain.getMunicipalRegistrationSnapshot());
        existing.setRepresentativeName(domain.getRepresentativeName());
        existing.setRepresentativeCpf(domain.getRepresentativeCpf());
        existing.setUpdatedAt(domain.getUpdatedAt());
    }
}
