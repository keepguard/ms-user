package com.keepguard.ms_user.infrastructure.persistence.spring;

import com.keepguard.ms_user.infrastructure.persistence.entity.ContactJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactSpringRepository extends JpaRepository<ContactJpaEntity, UUID>, JpaSpecificationExecutor<ContactJpaEntity> {

    List<ContactJpaEntity> findByUserId(UUID userId);

    List<ContactJpaEntity> findByUserIdAndActiveTrue(UUID userId);

    Optional<ContactJpaEntity> findByUserIdAndPrimaryTrueAndActiveTrue(UUID userId);

    List<ContactJpaEntity> findByUserIdAndType(UUID userId, String type);

    List<ContactJpaEntity> findByValue(String value);

    @Query("SELECT c FROM ContactJpaEntity c WHERE LOWER(c.value) LIKE LOWER(CONCAT('%', :value, '%'))")
    List<ContactJpaEntity> findByValueContainingIgnoreCase(@Param("value") String value);

    @Query("SELECT c FROM ContactJpaEntity c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<ContactJpaEntity> findByDescriptionContainingIgnoreCase(@Param("description") String description);

    boolean existsByValueAndType(String value, String type);

    long countByUserId(UUID userId);

    long countByUserIdAndActiveTrue(UUID userId);

    long countByUserIdAndType(UUID userId, String type);
}
