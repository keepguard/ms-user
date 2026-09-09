package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.infrastructure.persistence.entity.PersonProfileJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PersonProfileJpaMapper {

    public PersonProfile toDomain(PersonProfileJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return PersonProfile.of(
            entity.getId(),
            entity.getUserId(),
            entity.getFullName(),
            entity.getCpf(),
            entity.getRg(),
            entity.getRgIssuer(),
            entity.getRgState(),
            entity.getDateOfBirth(),
            entity.getGender(),
            entity.getMaritalStatus(),
            entity.getNationality(),
            entity.getBirthCountry(),
            entity.getBirthState(),
            entity.getBirthCity(),
            entity.getMotherName(),
            entity.getFatherName(),
            entity.isPep(),
            entity.getKycStatus(),
            entity.getKycLevel(),
            entity.getOccupation(),
            entity.getIncomeRange(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public PersonProfileJpaEntity toEntity(PersonProfile domain) {
        if (domain == null) {
            return null;
        }

        return PersonProfileJpaEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .fullName(domain.getFullName())
            .cpf(domain.getCpf())
            .rg(domain.getRg())
            .rgIssuer(domain.getRgIssuer())
            .rgState(domain.getRgState())
            .dateOfBirth(domain.getDateOfBirth())
            .gender(domain.getGender())
            .maritalStatus(domain.getMaritalStatus())
            .nationality(domain.getNationality())
            .birthCountry(domain.getBirthCountry())
            .birthState(domain.getBirthState())
            .birthCity(domain.getBirthCity())
            .motherName(domain.getMotherName())
            .fatherName(domain.getFatherName())
            .pep(domain.isPep())
            .kycStatus(domain.getKycStatus())
            .kycLevel(domain.getKycLevel())
            .occupation(domain.getOccupation())
            .incomeRange(domain.getIncomeRange())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    public void applyToExisting(PersonProfile domain, PersonProfileJpaEntity existing) {
        existing.setFullName(domain.getFullName());
        existing.setCpf(domain.getCpf());
        existing.setRg(domain.getRg());
        existing.setRgIssuer(domain.getRgIssuer());
        existing.setRgState(domain.getRgState());
        existing.setDateOfBirth(domain.getDateOfBirth());
        existing.setGender(domain.getGender());
        existing.setMaritalStatus(domain.getMaritalStatus());
        existing.setNationality(domain.getNationality());
        existing.setBirthCountry(domain.getBirthCountry());
        existing.setBirthState(domain.getBirthState());
        existing.setBirthCity(domain.getBirthCity());
        existing.setMotherName(domain.getMotherName());
        existing.setFatherName(domain.getFatherName());
        existing.setPep(domain.isPep());
        existing.setKycStatus(domain.getKycStatus());
        existing.setKycLevel(domain.getKycLevel());
        existing.setOccupation(domain.getOccupation());
        existing.setIncomeRange(domain.getIncomeRange());
        existing.setUpdatedAt(domain.getUpdatedAt());
    }
}
