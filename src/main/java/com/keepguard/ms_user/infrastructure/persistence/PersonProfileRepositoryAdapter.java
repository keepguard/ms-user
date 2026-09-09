package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.port.out.persistence.PersonProfileRepositoryPort;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.infrastructure.persistence.entity.PersonProfileJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.PersonProfileJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.PersonProfileSpringRepository;
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
public class PersonProfileRepositoryAdapter implements PersonProfileRepositoryPort {

    private final PersonProfileSpringRepository springRepository;
    private final PersonProfileJpaMapper mapper;
    private final UserSpringRepository userSpringRepository;

    @Override
    public PersonProfile save(PersonProfile personProfile) {
        if (personProfile.getUserId() == null) {
            throw new IllegalArgumentException("userId não pode ser null ao salvar PersonProfile");
        }

        var userRef = userSpringRepository.getReferenceById(personProfile.getUserId());
        var existing = findExisting(personProfile);
        PersonProfileJpaEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            mapper.applyToExisting(personProfile, entity);
        } else {
            entity = mapper.toEntity(personProfile);
        }
        entity.setUser(userRef);
        if (entity.getUser() == null) {
            throw new IllegalStateException("User não foi definido na entidade PersonProfile");
        }

        return mapper.toDomain(springRepository.save(entity));
    }

    private Optional<PersonProfileJpaEntity> findExisting(PersonProfile personProfile) {
        if (personProfile.getId() != null) {
            var byId = springRepository.findById(personProfile.getId());
            if (byId.isPresent()) {
                return byId;
            }
        }
        return springRepository.findByUserId(personProfile.getUserId());
    }

    @Override
    public Optional<PersonProfile> findByUserId(UUID userId) {
        return springRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<PersonProfile> findByCpf(String cpf) {
        return springRepository.findByCpf(cpf)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return springRepository.existsByCpf(cpf);
    }

    @Override
    public List<PersonProfile> findByFullNameContainingIgnoreCase(String name) {
        return springRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PersonProfile> findByRg(String rg) {
        return springRepository.findByRg(rg)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByRg(String rg) {
        return springRepository.existsByRg(rg);
    }

    @Override
    public List<PersonProfile> findByKycStatus(String kycStatus) {
        return springRepository.findByKycStatus(kycStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonProfile> findByKycLevel(String kycLevel) {
        return springRepository.findByKycLevel(kycLevel).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonProfile> findByPepTrue() {
        return springRepository.findByPepTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonProfile> findByIncomeRange(String incomeRange) {
        return springRepository.findByIncomeRange(incomeRange).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonProfile> findByOccupationContainingIgnoreCase(String occupation) {
        return springRepository.findByOccupationContainingIgnoreCase(occupation).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springRepository.deleteByUserId(userId);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return springRepository.existsById(userId);
    }

    @Override
    public boolean existsByCpfAndTenantId(String cpf, UUID companyId) {
        if (cpf == null || cpf.trim().isEmpty() || companyId == null) {
            return false;
        }
        return springRepository.existsByCpfAndTenantId(cpf, companyId);
    }

    @Override
    public boolean existsByCpfAndCompanyId(String cpf, UUID companyId, UUID excludeUserId) {
        if (cpf == null || cpf.trim().isEmpty() || companyId == null) {
            return false;
        }
        return springRepository.existsByCpfAndCompanyId(cpf, companyId, excludeUserId);
    }

    @Override
    public Optional<PersonProfile> findByCpfAndTenantId(String cpf, UUID companyId) {
        if (cpf == null || cpf.trim().isEmpty() || companyId == null) {
            return Optional.empty();
        }
        return springRepository.findByCpfAndTenantId(cpf, companyId)
                .map(mapper::toDomain);
    }
}
