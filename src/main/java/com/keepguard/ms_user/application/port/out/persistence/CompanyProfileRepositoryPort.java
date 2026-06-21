package com.keepguard.ms_user.application.port.out.persistence;

import com.keepguard.ms_user.domain.entity.CompanyProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyProfileRepositoryPort {

    CompanyProfile save(CompanyProfile companyProfile);

    Optional<CompanyProfile> findByUserId(UUID userId);

    List<CompanyProfile> findByCompanyId(UUID companyId);

    Optional<CompanyProfile> findByCnpjSnapshot(String cnpjSnapshot);

    boolean existsByCnpjSnapshot(String cnpjSnapshot);

    List<CompanyProfile> findByLegalNameSnapshotContainingIgnoreCase(String name);

    Optional<CompanyProfile> findByStateRegistrationSnapshot(String stateRegistrationSnapshot);

    Optional<CompanyProfile> findByMunicipalRegistrationSnapshot(String municipalRegistrationSnapshot);

    List<CompanyProfile> findByRepresentativeNameContainingIgnoreCase(String name);

    Optional<CompanyProfile> findByRepresentativeCpf(String representativeCpf);

    boolean existsByRepresentativeCpf(String representativeCpf);

    void deleteByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}

