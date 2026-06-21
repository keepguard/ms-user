package com.keepguard.ms_user.infrastructure.persistence.spring;

import com.keepguard.ms_user.infrastructure.persistence.entity.AddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressSpringRepository extends JpaRepository<AddressJpaEntity, UUID>, JpaSpecificationExecutor<AddressJpaEntity> {

    List<AddressJpaEntity> findByUserId(UUID userId);

    List<AddressJpaEntity> findByUserIdAndActiveTrue(UUID userId);

    Optional<AddressJpaEntity> findByUserIdAndPrimaryTrue(UUID userId);

    Optional<AddressJpaEntity> findByUserIdAndPrimaryTrueAndActiveTrue(UUID userId);

    List<AddressJpaEntity> findByUserIdAndType(UUID userId, String type);

    @Query("SELECT a FROM AddressJpaEntity a WHERE LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<AddressJpaEntity> findByCityContainingIgnoreCase(@Param("city") String city);

    List<AddressJpaEntity> findByState(String state);

    List<AddressJpaEntity> findByZipCode(String zipCode);

    @Query("SELECT a FROM AddressJpaEntity a WHERE LOWER(a.neighborhood) LIKE LOWER(CONCAT('%', :neighborhood, '%'))")
    List<AddressJpaEntity> findByNeighborhoodContainingIgnoreCase(@Param("neighborhood") String neighborhood);

    List<AddressJpaEntity> findByCountry(String country);

    long countByUserId(UUID userId);

    long countByUserIdAndActiveTrue(UUID userId);
}
