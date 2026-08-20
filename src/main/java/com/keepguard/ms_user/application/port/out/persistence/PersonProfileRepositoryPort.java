package com.keepguard.ms_user.application.port.out.persistence;

import com.keepguard.ms_user.domain.entity.PersonProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonProfileRepositoryPort {

    PersonProfile save(PersonProfile personProfile);

    Optional<PersonProfile> findByUserId(UUID userId);

    Optional<PersonProfile> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    List<PersonProfile> findByFullNameContainingIgnoreCase(String name);

    Optional<PersonProfile> findByRg(String rg);

    boolean existsByRg(String rg);

    List<PersonProfile> findByKycStatus(String kycStatus);

    List<PersonProfile> findByKycLevel(String kycLevel);

    List<PersonProfile> findByPepTrue();

    List<PersonProfile> findByIncomeRange(String incomeRange);

    List<PersonProfile> findByOccupationContainingIgnoreCase(String occupation);

    void deleteByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByCpfAndTenantId(String cpf, UUID tenantId);

    Optional<PersonProfile> findByCpfAndTenantId(String cpf, UUID tenantId);
}

