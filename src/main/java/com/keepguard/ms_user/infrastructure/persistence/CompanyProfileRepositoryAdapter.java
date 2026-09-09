package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.port.out.persistence.CompanyProfileRepositoryPort;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.infrastructure.persistence.entity.CompanyProfileJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.CompanyProfileJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.CompanyProfileSpringRepository;
import com.keepguard.ms_user.infrastructure.persistence.spring.UserSpringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;

@Repository
@RequiredArgsConstructor
@Retry(name = "databaseOperation")
@Bulkhead(name = "databaseOperation")
public class CompanyProfileRepositoryAdapter implements CompanyProfileRepositoryPort {

    private final CompanyProfileSpringRepository springRepository;
    private final CompanyProfileJpaMapper mapper;
    private final UserSpringRepository userSpringRepository;

    @Override
    public CompanyProfile save(CompanyProfile companyProfile) {
        if (companyProfile.getUserId() == null) {
            throw new IllegalArgumentException("userId não pode ser null ao salvar CompanyProfile");
        }

        var userRef = userSpringRepository.getReferenceById(companyProfile.getUserId());
        var existing = findExisting(companyProfile);
        CompanyProfileJpaEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            mapper.applyToExisting(companyProfile, entity);
        } else {
            entity = mapper.toEntity(companyProfile);
        }
        entity.setUser(userRef);
        if (entity.getUser() == null) {
            throw new IllegalStateException("CompanyProfile deve ter um User associado");
        }

        return mapper.toDomain(springRepository.save(entity));
    }

    private Optional<CompanyProfileJpaEntity> findExisting(CompanyProfile companyProfile) {
        if (companyProfile.getId() != null) {
            var byId = springRepository.findById(companyProfile.getId());
            if (byId.isPresent()) {
                return byId;
            }
        }
        return springRepository.findByUserId(companyProfile.getUserId());
    }

    @Override
    public Optional<CompanyProfile> findByUserId(UUID userId) {
        return springRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public List<CompanyProfile> findByCompanyId(UUID companyId) {
        return springRepository.findByCompanyId(companyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CompanyProfile> findByCnpjSnapshot(String cnpjSnapshot) {
        return springRepository.findByCnpjSnapshot(cnpjSnapshot)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByCnpjSnapshot(String cnpjSnapshot) {
        return springRepository.existsByCnpjSnapshot(cnpjSnapshot);
    }

    @Override
    public List<CompanyProfile> findByLegalNameSnapshotContainingIgnoreCase(String name) {
        return springRepository.findByLegalNameSnapshotContainingIgnoreCase(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CompanyProfile> findByStateRegistrationSnapshot(String stateRegistrationSnapshot) {
        return springRepository.findByStateRegistrationSnapshot(stateRegistrationSnapshot)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<CompanyProfile> findByMunicipalRegistrationSnapshot(String municipalRegistrationSnapshot) {
        return springRepository.findByMunicipalRegistrationSnapshot(municipalRegistrationSnapshot)
                .map(mapper::toDomain);
    }

    @Override
    public List<CompanyProfile> findByRepresentativeNameContainingIgnoreCase(String name) {
        return springRepository.findByRepresentativeNameContainingIgnoreCase(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CompanyProfile> findByRepresentativeCpf(String representativeCpf) {
        return springRepository.findByRepresentativeCpf(representativeCpf)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByRepresentativeCpf(String representativeCpf) {
        return springRepository.existsByRepresentativeCpf(representativeCpf);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springRepository.deleteByUserId(userId);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return springRepository.existsById(userId);
    }
}
