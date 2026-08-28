package com.keepguard.ms_user.application.port.out.persistence;

import com.keepguard.ms_user.application.dto.user.UserSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByIdAndCompanyId(UUID id, UUID companyId);

    List<User> findAll();

    void deleteById(UUID id);

    void delete(User user);

    Optional<User> findByCodeUser(UUID codeUser);

    Optional<User> findByCodeUserAndCompanyId(UUID codeUser, UUID companyId);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    Optional<User> findByEmailAndCompanyId(String email, UUID companyId);

    List<User> findAllByCompanyId(UUID companyId);

    List<User> findAllByStatus(UserStatusEnum status);

    List<User> findAllByType(UserTypeEnum type);

    boolean existsByEmail(String email);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    boolean existsByEmailAndCompanyId(String email, UUID companyId, UUID excludeUserId);

    boolean existsByPhoneE164AndCompanyId(String phoneE164, UUID companyId, UUID excludeUserId);

    boolean existsByCodeUser(UUID codeUser);

    List<User> findByEmailContainingIgnoreCase(String email);

    PageResultDTO<User> search(UserSearchCriteriaDTO criteria);

    List<User> findAllByIdIn(List<UUID> ids);

    long count();

    long countByCompanyId(UUID companyId);

    boolean existsById(UUID id);

    boolean existsByDisplayHandleAndCompanyId(String displayHandle, UUID companyId, UUID excludeUserId);
}

