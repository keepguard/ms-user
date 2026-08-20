package com.keepguard.ms_user.infrastructure.persistence.spring;

import com.keepguard.ms_user.infrastructure.persistence.entity.PersonProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonProfileSpringRepository extends JpaRepository<PersonProfileJpaEntity, UUID> {

    Optional<PersonProfileJpaEntity> findByUserId(UUID userId);
    
    void deleteByUserId(UUID userId);

    Optional<PersonProfileJpaEntity> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    @Query("SELECT p FROM PersonProfileJpaEntity p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PersonProfileJpaEntity> findByFullNameContainingIgnoreCase(@Param("name") String name);

    Optional<PersonProfileJpaEntity> findByRg(String rg);

    boolean existsByRg(String rg);

    List<PersonProfileJpaEntity> findByKycStatus(String kycStatus);

    List<PersonProfileJpaEntity> findByKycLevel(String kycLevel);

    List<PersonProfileJpaEntity> findByPepTrue();

    List<PersonProfileJpaEntity> findByIncomeRange(String incomeRange);

    @Query("SELECT p FROM PersonProfileJpaEntity p WHERE LOWER(p.occupation) LIKE LOWER(CONCAT('%', :occupation, '%'))")
    List<PersonProfileJpaEntity> findByOccupationContainingIgnoreCase(@Param("occupation") String occupation);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PersonProfileJpaEntity p JOIN p.user u WHERE p.cpf = :cpf AND u.tenantId = :tenantId")
    boolean existsByCpfAndTenantId(@Param("cpf") String cpf, @Param("tenantId") UUID tenantId);

    @Query("SELECT p FROM PersonProfileJpaEntity p JOIN p.user u WHERE p.cpf = :cpf AND u.tenantId = :tenantId")
    Optional<PersonProfileJpaEntity> findByCpfAndTenantId(@Param("cpf") String cpf, @Param("tenantId") UUID tenantId);
}
