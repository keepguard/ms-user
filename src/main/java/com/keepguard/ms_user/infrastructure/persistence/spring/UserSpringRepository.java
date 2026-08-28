package com.keepguard.ms_user.infrastructure.persistence.spring;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSpringRepository extends JpaRepository<UserJpaEntity, UUID>, JpaSpecificationExecutor<UserJpaEntity> {

    @Query("SELECT u FROM UserJpaEntity u WHERE u.id = :id")
    Optional<UserJpaEntity> findByIdWithRelations(@Param("id") UUID id);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.id = :id AND u.companyId = :companyId")
    Optional<UserJpaEntity> findByIdAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.codeUser = :codeUser")
    Optional<UserJpaEntity> findByCodeUser(@Param("codeUser") UUID codeUser);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.codeUser = :codeUser AND u.companyId = :companyId")
    Optional<UserJpaEntity> findByCodeUserAndCompanyId(@Param("codeUser") UUID codeUser, @Param("companyId") UUID companyId);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.email = :email")
    Optional<UserJpaEntity> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.email = :email AND u.tenantId = :tenantId")
    Optional<UserJpaEntity> findByEmailAndTenantId(@Param("email") String email, @Param("tenantId") UUID tenantId);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.email = :email AND u.companyId = :companyId")
    Optional<UserJpaEntity> findByEmailAndCompanyId(@Param("email") String email, @Param("companyId") UUID companyId);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.companyId = :companyId")
    List<UserJpaEntity> findAllByCompanyId(@Param("companyId") UUID companyId);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.status = :status")
    List<UserJpaEntity> findAllByStatus(@Param("status") UserStatusEnum status);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.type = :type")
    List<UserJpaEntity> findAllByType(@Param("type") UserTypeEnum type);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM UserJpaEntity u WHERE u.email = :email AND u.tenantId = :tenantId")
    boolean existsByEmailAndTenantId(@Param("email") String email, @Param("tenantId") UUID tenantId);

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM UserJpaEntity u
        WHERE u.email = :email
        AND u.companyId = :companyId
        AND (:excludeUserId IS NULL OR u.id != :excludeUserId)
        """)
    boolean existsByEmailAndCompanyId(
        @Param("email") String email,
        @Param("companyId") UUID companyId,
        @Param("excludeUserId") UUID excludeUserId
    );

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM UserJpaEntity u
        WHERE u.phoneE164 = :phoneE164
        AND u.companyId = :companyId
        AND (:excludeUserId IS NULL OR u.id != :excludeUserId)
        """)
    boolean existsByPhoneE164AndCompanyId(
        @Param("phoneE164") String phoneE164,
        @Param("companyId") UUID companyId,
        @Param("excludeUserId") UUID excludeUserId
    );

    boolean existsByCodeUser(UUID codeUser);

    @Query("SELECT u FROM UserJpaEntity u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<UserJpaEntity> findByEmailContainingIgnoreCase(@Param("email") String email);

    @Query("SELECT u FROM UserJpaEntity u")
    List<UserJpaEntity> findAllWithRelations();

    @Query("SELECT DISTINCT u FROM UserJpaEntity u WHERE " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:companyId IS NULL OR u.companyId = :companyId) AND " +
           "(:type IS NULL OR u.type = :type) AND " +
           "(:status IS NULL OR u.status = :status)")
    List<UserJpaEntity> searchWithRelations(@Param("email") String email,
                                           @Param("companyId") UUID companyId,
                                           @Param("type") UserTypeEnum type,
                                           @Param("status") UserStatusEnum status);

    @Query("SELECT DISTINCT u FROM UserJpaEntity u WHERE " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:companyId IS NULL OR u.companyId = :companyId) AND " +
           "(:type IS NULL OR u.type = :type) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<UserJpaEntity> findByFilters(@Param("email") String email,
                                     @Param("companyId") UUID companyId,
                                     @Param("type") UserTypeEnum type,
                                     @Param("status") UserStatusEnum status,
                                     Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserJpaEntity u WHERE u.companyId = :companyId")
    long countByCompanyId(@Param("companyId") UUID companyId);

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM UserJpaEntity u
        WHERE u.displayHandle = :displayHandle
        AND u.companyId = :companyId
        AND (:excludeUserId IS NULL OR u.id != :excludeUserId)
        """)
    boolean existsByDisplayHandleAndCompanyId(
        @Param("displayHandle") String displayHandle,
        @Param("companyId") UUID companyId,
        @Param("excludeUserId") UUID excludeUserId
    );
}
