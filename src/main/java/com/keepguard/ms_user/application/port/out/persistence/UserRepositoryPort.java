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

    List<User> findAll();

    void deleteById(UUID id);

    void delete(User user);

    Optional<User> findByCodeUser(UUID codeUser);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndXApplication(String email, UUID xApplication);

    List<User> findAllByCompanyId(UUID companyId);

    List<User> findAllByStatus(UserStatusEnum status);

    List<User> findAllByType(UserTypeEnum type);

    boolean existsByEmail(String email);

    boolean existsByEmailAndXApplication(String email, UUID xApplication);

    boolean existsByCodeUser(UUID codeUser);

    List<User> findByEmailContainingIgnoreCase(String email);

    PageResultDTO<User> search(UserSearchCriteriaDTO criteria);

    List<User> findAllByIdIn(List<UUID> ids);

    long count();

    long countByCompanyId(UUID companyId);

    boolean existsById(UUID id);

    boolean existsByDisplayHandleAndCompanyId(String displayHandle, UUID companyId, UUID excludeUserId);
}

