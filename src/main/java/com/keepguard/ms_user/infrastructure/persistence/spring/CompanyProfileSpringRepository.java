package com.keepguard.ms_user.infrastructure.persistence.spring;

import com.keepguard.ms_user.infrastructure.persistence.entity.CompanyProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyProfileSpringRepository extends JpaRepository<CompanyProfileJpaEntity, UUID> {

    Optional<CompanyProfileJpaEntity> findByUserId(UUID userId);
    
    void deleteByUserId(UUID userId);

    List<CompanyProfileJpaEntity> findByCompanyId(UUID companyId);

    Optional<CompanyProfileJpaEntity> findByCnpjSnapshot(String cnpjSnapshot);

    boolean existsByCnpjSnapshot(String cnpjSnapshot);

    @Query("SELECT c FROM CompanyProfileJpaEntity c WHERE LOWER(c.legalNameSnapshot) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<CompanyProfileJpaEntity> findByLegalNameSnapshotContainingIgnoreCase(@Param("name") String name);

    Optional<CompanyProfileJpaEntity> findByStateRegistrationSnapshot(String stateRegistrationSnapshot);

    Optional<CompanyProfileJpaEntity> findByMunicipalRegistrationSnapshot(String municipalRegistrationSnapshot);

    @Query("SELECT c FROM CompanyProfileJpaEntity c WHERE LOWER(c.representativeName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<CompanyProfileJpaEntity> findByRepresentativeNameContainingIgnoreCase(@Param("name") String name);

    Optional<CompanyProfileJpaEntity> findByRepresentativeCpf(String representativeCpf);

    boolean existsByRepresentativeCpf(String representativeCpf);
}
