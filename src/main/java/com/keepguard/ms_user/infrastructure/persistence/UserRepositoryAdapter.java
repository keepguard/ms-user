package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.dto.user.UserSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchViewDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.mapper.UserApplicationMapper;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.entity.UserProfile;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.UserJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.mapper.PersonProfileJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.mapper.CompanyProfileJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.UserSpringRepository;
import com.keepguard.ms_user.infrastructure.persistence.spring.PersonProfileSpringRepository;
import com.keepguard.ms_user.infrastructure.persistence.spring.CompanyProfileSpringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;

@Repository
@RequiredArgsConstructor
@Retry(name = "databaseOperation")
@Bulkhead(name = "databaseOperation")
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserSpringRepository springRepository;
    private final PersonProfileSpringRepository personProfileRepository;
    private final CompanyProfileSpringRepository companyProfileRepository;
    private final UserJpaMapper mapper;
    private final PersonProfileJpaMapper personProfileMapper;
    private final CompanyProfileJpaMapper companyProfileMapper;
    private final UserApplicationMapper userApplicationMapper;

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = springRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springRepository.findByIdWithRelations(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByIdAndCompanyId(UUID id, UUID companyId) {
        return springRepository.findByIdAndCompanyId(id, companyId)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return springRepository.findAllWithRelations().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springRepository.deleteById(id);
    }

    @Override
    public void delete(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        springRepository.delete(entity);
    }

    @Override
    public Optional<User> findByCodeUser(UUID codeUser) {
        return springRepository.findByCodeUser(codeUser)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByCodeUserAndCompanyId(UUID codeUser, UUID companyId) {
        return springRepository.findByCodeUserAndCompanyId(codeUser, companyId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailAndTenantId(String email, UUID tenantId) {
        return springRepository.findByEmailAndTenantId(email, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailAndCompanyId(String email, UUID companyId) {
        return springRepository.findByEmailAndCompanyId(email, companyId)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findAllByCompanyId(UUID companyId) {
        return springRepository.findAllByCompanyId(companyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllByStatus(UserStatusEnum status) {
        return springRepository.findAllByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllByType(UserTypeEnum type) {
        return springRepository.findAllByType(type).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return springRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndTenantId(String email, UUID tenantId) {
        return springRepository.existsByEmailAndTenantId(email, tenantId);
    }

    @Override
    public boolean existsByEmailAndCompanyId(String email, UUID companyId, UUID excludeUserId) {
        if (email == null || email.trim().isEmpty() || companyId == null) {
            return false;
        }
        return springRepository.existsByEmailAndCompanyId(email.trim(), companyId, excludeUserId);
    }

    @Override
    public boolean existsByPhoneE164AndCompanyId(String phoneE164, UUID companyId, UUID excludeUserId) {
        if (phoneE164 == null || phoneE164.trim().isEmpty() || companyId == null) {
            return false;
        }
        return springRepository.existsByPhoneE164AndCompanyId(phoneE164.trim(), companyId, excludeUserId);
    }

    @Override
    public boolean existsByCodeUser(UUID codeUser) {
        return springRepository.existsByCodeUser(codeUser);
    }

    @Override
    public List<User> findByEmailContainingIgnoreCase(String email) {
        return springRepository.findByEmailContainingIgnoreCase(email).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResultDTO<User> search(UserSearchCriteriaDTO criteria) {
        var spec = buildSpecification(criteria);
        var pageable = buildPageable(criteria);

        var page = springRepository.findAll(spec, pageable);

        var users = page.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return new PageResultDTO<>(
                users,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * Busca perfil de pessoa por ID do usuário
     */
    public UserProfile findPersonProfileByUserId(UUID userId) {
        var personProfile = personProfileRepository.findByUserId(userId);
        return personProfile.map(personProfileMapper::toDomain).orElse(null);
    }

    /**
     * Busca perfil de empresa por ID do usuário
     */
    public UserProfile findCompanyProfileByUserId(UUID userId) {
        var companyProfile = companyProfileRepository.findByUserId(userId);
        return companyProfile.map(companyProfileMapper::toDomain).orElse(null);
    }

    /**
     * Busca usuários com seus profiles carregados.
     * Versão do método search que inclui os dados de profile.
     */
    public PageResultDTO<UserSearchViewDTO> searchWithProfiles(UserSearchCriteriaDTO criteria) {
        var spec = buildSpecification(criteria);
        var pageable = buildPageable(criteria);

        var page = springRepository.findAll(spec, pageable);

        var usersWithProfiles = page.getContent().stream()
                .map(entity -> {
                    var user = mapper.toDomain(entity);
                    var profile = loadProfile(entity);
                    return new UserWithProfile(user, profile);
                })
                .collect(Collectors.toList());

        // Converter para UserSearchViewDTO com profiles
        var userSearchViews = usersWithProfiles.stream()
                .map(userWithProfile -> userApplicationMapper.toSearchView(
                    userWithProfile.getUser(), 
                    userWithProfile.getProfile()
                ))
                .collect(Collectors.toList());

        return new PageResultDTO<>(
                userSearchViews,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }


    private Specification<UserJpaEntity> buildSpecification(UserSearchCriteriaDTO criteria) {
        Specification<UserJpaEntity> spec = Specification.where(null);

        if (criteria.email() != null) {
            spec = spec.and(hasEmailContaining(criteria.email()));
        }
        if (criteria.companyId() == null) {
            spec = spec.and((root, query, cb) -> cb.disjunction());
        } else {
            spec = spec.and(hasCompanyId(criteria.companyId()));
        }
        if (criteria.type() != null) {
            spec = spec.and(hasType(criteria.type()));
        }
        if (criteria.status() != null) {
            spec = spec.and(hasStatus(criteria.status()));
        }

        return spec;
    }

    private Pageable buildPageable(UserSearchCriteriaDTO criteria) {
        Sort sort = Sort.by(Sort.Direction.ASC, "email");

        if (criteria.sortFields() != null && !criteria.sortFields().isEmpty()) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(criteria.sortDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, criteria.sortFields().toArray(new String[0]));
        }

        return PageRequest.of(criteria.page(), criteria.size(), sort);
    }

    private Specification<UserJpaEntity> hasEmailContaining(String email) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private Specification<UserJpaEntity> hasCompanyId(UUID companyId) {
        return (root, query, cb) -> cb.equal(root.get("companyId"), companyId);
    }

    private Specification<UserJpaEntity> hasType(UserTypeEnum type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    private Specification<UserJpaEntity> hasStatus(UserStatusEnum status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public long count() {
        return springRepository.count();
    }

    @Override
    public List<User> findAllByIdIn(List<UUID> ids) {
        return springRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByCompanyId(UUID companyId) {
        return springRepository.countByCompanyId(companyId);
    }

    @Override
    public boolean existsById(UUID id) {
        return springRepository.existsById(id);
    }

    @Override
    public boolean existsByDisplayHandleAndCompanyId(String displayHandle, UUID companyId, UUID excludeUserId) {
        if (displayHandle == null || displayHandle.trim().isEmpty() || companyId == null) {
            return false;
        }
        return springRepository.existsByDisplayHandleAndCompanyId(displayHandle.trim().toLowerCase(), companyId, excludeUserId);
    }

    /**
     * Carrega o profile do usuário baseado no seu tipo.
     * Identifica automaticamente o tipo e faz a consulta apropriada.
     * 
     * @param userEntity entidade JPA do usuário
     * @return objeto profile correspondente ou null se não encontrado
     */
    private UserProfile loadProfile(UserJpaEntity userEntity) {
        if (userEntity == null || userEntity.getId() == null) {
            return null;
        }

        return switch (userEntity.getType()) {
            case PERSON -> {
                var personProfile = personProfileRepository.findByUserId(userEntity.getId());
                yield personProfile.map(personProfileMapper::toDomain).orElse(null);
            }
            case COMPANY -> {
                var companyProfile = companyProfileRepository.findByUserId(userEntity.getId());
                yield companyProfile.map(companyProfileMapper::toDomain).orElse(null);
            }
            default -> null;
        };
    }

    /**
     * Método público para carregar um usuário com seu profile completo.
     * Útil quando você precisa do usuário e seu profile em uma única operação.
     * 
     * @param userId ID do usuário
     * @return Optional contendo o usuário com profile carregado, ou vazio se não encontrado
     */
    public Optional<UserWithProfile> findUserWithProfileById(UUID userId) {
        return springRepository.findById(userId)
                .map(entity -> {
                    var user = mapper.toDomain(entity);
                    var profile = loadProfile(entity);
                    return new UserWithProfile(user, profile);
                });
    }

    /**
     * Classe interna para encapsular usuário com seu profile.
     * Usa a interface selada UserProfile para garantir type safety.
     */
    public static class UserWithProfile {
        private final User user;
        private final UserProfile profile;

        public UserWithProfile(User user, UserProfile profile) {
            this.user = user;
            this.profile = profile;
        }

        public User getUser() {
            return user;
        }

        public UserProfile getProfile() {
            return profile;
        }

        /**
         * Retorna o profile com type casting seguro usando pattern matching.
         * @param profileType classe do tipo esperado
         * @return profile do tipo especificado ou null se não for compatível
         */
        @SuppressWarnings("unchecked")
        public <T extends UserProfile> T getProfile(Class<T> profileType) {
            if (profile != null && profileType.isAssignableFrom(profile.getClass())) {
                return (T) profile;
            }
            return null;
        }
    }
}
