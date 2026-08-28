package com.keepguard.ms_user.application.service.user;

import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.mapper.UserApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.application.port.out.cache.UserCachePort;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.entity.UserProfile;
import com.keepguard.ms_user.infrastructure.persistence.UserRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryService {

    private final UserRepositoryPort userRepositoryPort;
    private final UserCachePort userCachePort;
    private final UserApplicationMapper userApplicationMapper;
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final MetricsPort metricsPort;

    @Transactional(readOnly = true)
    public UserDetailsViewDTO getById(UserGetByIdQueryDTO query) {
        log.info("companyId: {} companyId: {} - Buscando usuário por ID: {}", query.companyId(), query.companyId(), query.id());

        var cachedUser = userCachePort.getUserByIdFromCache(query.id().toString());
        if (cachedUser != null) {
            if (!query.companyId().equals(cachedUser.companyId())) {
                metricsPort.incrementCounter("user_not_found_total",
                    Map.of("entity_id", query.id().toString(), "operation", "get_by_id"));
                throw new NotFoundException("Usuário não encontrado: " + query.id(), "USER_NOT_FOUND", Map.of("userId", query.id()));
            }
            metricsPort.incrementCounter("user_queries_total",
                Map.of("query_type", "GET_BY_ID", "status", "CACHE_HIT"));
            return cachedUser;
        }

        var user = userRepositoryPort.findByIdAndCompanyId(query.id(), query.companyId())
                .orElseThrow(() -> {
                    metricsPort.incrementCounter("user_not_found_total",
                        Map.of("entity_id", query.id().toString(), "operation", "get_by_id"));
                    return new NotFoundException("Usuário não encontrado: " + query.id(), "USER_NOT_FOUND", Map.of("userId", query.id()));
                });

        var userView = userApplicationMapper.toGetByIdView(user, loadProfile(user));
        userCachePort.cacheUserById(user.getId().toString(), userView);

        metricsPort.incrementCounter("user_queries_total",
            Map.of("query_type", "GET_BY_ID", "status", "SUCCESS"));

        return userView;
    }

    @Transactional(readOnly = true)
    public UserDetailsViewDTO getByCodeUser(UserGetByCodeUserQueryDTO query) {
        log.info("companyId: {} companyId: {} - Buscando usuário por codeUser: {}", query.companyId(), query.companyId(), query.codeUser());

        var cachedUser = userCachePort.getUserByCodeFromCache(query.codeUser().toString());
        if (cachedUser != null) {
            if (!query.companyId().equals(cachedUser.companyId())) {
                metricsPort.incrementCounter("user_not_found_total",
                    Map.of("entity_id", query.codeUser().toString(), "operation", "get_by_code"));
                throw new NotFoundException("Usuário não encontrado com codeUser: " + query.codeUser(), "USER_NOT_FOUND", Map.of("codeUser", query.codeUser()));
            }
            metricsPort.incrementCounter("user_queries_total",
                Map.of("query_type", "GET_BY_CODE", "status", "CACHE_HIT"));
            return cachedUser;
        }

        var user = userRepositoryPort.findByCodeUserAndCompanyId(query.codeUser(), query.companyId())
                .orElseThrow(() -> {
                    metricsPort.incrementCounter("user_not_found_total",
                        Map.of("entity_id", query.codeUser().toString(), "operation", "get_by_code"));
                    return new NotFoundException("Usuário não encontrado com codeUser: " + query.codeUser(), "USER_NOT_FOUND", Map.of("codeUser", query.codeUser()));
                });

        var userView = userApplicationMapper.toByCodeUserView(user, loadProfile(user));
        userCachePort.cacheUserByCode(user.getCodeUser().toString(), userView);

        metricsPort.incrementCounter("user_queries_total",
            Map.of("query_type", "GET_BY_CODE", "status", "SUCCESS"));

        return userView;
    }

    @Transactional(readOnly = true)
    public UserDetailsViewDTO getByCodeUserForTenant(UUID codeUser, UUID companyId) {
        log.info("companyId: {} - Buscando usuário por codeUser no tenant: {}", companyId, codeUser);

        var user = userRepositoryPort.findByCodeUser(codeUser)
                .orElseThrow(() -> {
                    metricsPort.incrementCounter("user_not_found_total",
                        Map.of("entity_id", codeUser.toString(), "operation", "get_by_code_tenant"));
                    return new NotFoundException("Usuário não encontrado com codeUser: " + codeUser, "USER_NOT_FOUND", Map.of("codeUser", codeUser));
                });

        if (!companyId.equals(user.getCompanyId())) {
            metricsPort.incrementCounter("user_not_found_total",
                Map.of("entity_id", codeUser.toString(), "operation", "get_by_code_tenant"));
            throw new NotFoundException("Usuário não encontrado com codeUser: " + codeUser, "USER_NOT_FOUND", Map.of("codeUser", codeUser));
        }

        var userView = userApplicationMapper.toByCodeUserView(user, loadProfile(user));
        userCachePort.cacheUserByCode(user.getCodeUser().toString(), userView);

        metricsPort.incrementCounter("user_queries_total",
            Map.of("query_type", "GET_BY_CODE_TENANT", "status", "SUCCESS"));

        return userView;
    }

    @Transactional(readOnly = true)
    public UserDetailsViewDTO getByEmail(UserGetByEmailQueryDTO query) {
        log.info("companyId: {} companyId: {} - Buscando usuário por email: {}", query.companyId(), query.companyId(), query.email());

        var cachedUser = userCachePort.getUserByEmailFromCache(query.companyId(), query.email());
        if (cachedUser != null) {
            metricsPort.incrementCounter("user_queries_total",
                Map.of("query_type", "GET_BY_EMAIL", "status", "CACHE_HIT"));
            return cachedUser;
        }

        var user = userRepositoryPort.findByEmailAndCompanyId(query.email(), query.companyId())
                .orElseThrow(() -> {
                    metricsPort.incrementCounter("user_not_found_total",
                        Map.of("entity_id", query.email(), "operation", "get_by_email"));
                    return new NotFoundException("Usuário não encontrado com email: " + query.email(), "USER_NOT_FOUND", Map.of("email", query.email()));
                });

        var userView = userApplicationMapper.toByEmailView(user, loadProfile(user));
        userCachePort.cacheUserByEmail(query.companyId(), query.email(), userView);

        metricsPort.incrementCounter("user_queries_total",
            Map.of("query_type", "GET_BY_EMAIL", "status", "SUCCESS"));

        return userView;
    }

    @Transactional(readOnly = true)
    public PageResultDTO<UserSearchViewDTO> search(UserSearchQueryDTO query) {
        log.info("companyId: {} - Buscando usuários com critérios: email={}, companyId={}, type={}, status={}, page={}, size={}", 
                query.companyId(), query.email(), query.companyId(), query.type(), query.status(), query.page(), query.size());
        
        var criteria = userApplicationMapper.toSearchCriteria(query);
        var pageResult = userRepositoryPort.search(criteria);
        var userSearchViews = pageResult.content().stream()
                .map(user -> {
                    return userApplicationMapper.toSearchView(user, loadProfile(user));
                })
                .toList();

        return new PageResultDTO<>(
                userSearchViews,
                pageResult.totalElements(),
                pageResult.page(),
                pageResult.size()
        );
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.info("Verificando existência de usuário por ID: {}", id);
        return userRepositoryPort.existsById(id);
    }

    private UserProfile loadProfile(User user) {
        return switch (user.getType()) {
            case PERSON -> userRepositoryAdapter.findPersonProfileByUserId(user.getId());
            case COMPANY -> userRepositoryAdapter.findCompanyProfileByUserId(user.getId());
        };
    }

}
