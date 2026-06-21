package com.keepguard.ms_user.infrastructure.persistence.mapper;

import com.keepguard.ms_user.domain.entity.Address;
import com.keepguard.ms_user.infrastructure.persistence.entity.AddressJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressJpaMapper {

    public Address toDomain(AddressJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Address.of(
            jpaEntity.getId(),
            jpaEntity.getUserId(),
            jpaEntity.getStreet(),
            jpaEntity.getNumber(),
            jpaEntity.getComplement(),
            jpaEntity.getNeighborhood(),
            jpaEntity.getCity(),
            jpaEntity.getState(),
            jpaEntity.getZipCode(),
            jpaEntity.getCountry(),
            jpaEntity.getType(),
            jpaEntity.isPrimary(),
            jpaEntity.isActive(),
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt()
        );
    }

    public AddressJpaEntity toJpa(Address domain) {
        if (domain == null) {
            return null;
        }

        return AddressJpaEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .street(domain.getStreet())
            .number(domain.getNumber())
            .complement(domain.getComplement())
            .neighborhood(domain.getNeighborhood())
            .city(domain.getCity())
            .state(domain.getState())
            .zipCode(domain.getZipCode())
            .country(domain.getCountry())
            .type(domain.getType())
            .primary(domain.isPrimary())
            .active(domain.isActive())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    public List<Address> toDomainList(List<AddressJpaEntity> jpaEntities) {
        if (jpaEntities == null) {
            return List.of();
        }

        return jpaEntities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public List<AddressJpaEntity> toJpaList(List<Address> domains) {
        if (domains == null) {
            return List.of();
        }

        return domains.stream()
            .map(this::toJpa)
            .collect(Collectors.toList());
    }

    public void updateJpa(AddressJpaEntity jpaEntity, Address domain) {
        if (jpaEntity == null || domain == null) {
            return;
        }

        jpaEntity.setStreet(domain.getStreet());
        jpaEntity.setNumber(domain.getNumber());
        jpaEntity.setComplement(domain.getComplement());
        jpaEntity.setNeighborhood(domain.getNeighborhood());
        jpaEntity.setCity(domain.getCity());
        jpaEntity.setState(domain.getState());
        jpaEntity.setZipCode(domain.getZipCode());
        jpaEntity.setCountry(domain.getCountry());
        jpaEntity.setType(domain.getType());
        jpaEntity.setPrimary(domain.isPrimary());
        jpaEntity.setActive(domain.isActive());
        jpaEntity.setUpdatedAt(domain.getUpdatedAt());
    }
}

