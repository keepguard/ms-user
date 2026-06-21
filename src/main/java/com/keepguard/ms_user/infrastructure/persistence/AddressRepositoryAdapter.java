package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.dto.address.AddressSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.out.persistence.AddressRepositoryPort;
import com.keepguard.ms_user.domain.entity.Address;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.AddressJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.AddressJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.AddressSpringRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AddressRepositoryAdapter implements AddressRepositoryPort {

    private final AddressSpringRepository springRepository;
    private final AddressJpaMapper mapper;

    @Override
    public Address save(Address address) {
        var jpaEntity = mapper.toJpa(address);
        var savedEntity = springRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return springRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Address> findByUserId(UUID userId) {
        return springRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Address> findByUserIdAndPrimaryTrue(UUID userId) {
        return springRepository.findByUserIdAndPrimaryTrue(userId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        springRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return springRepository.existsById(id);
    }

    @Override
    public PageResultDTO<Address> search(AddressSearchCriteriaDTO criteria) {
        var spec = buildSpecification(criteria);
        var pageable = buildPageable(criteria);

        var page = springRepository.findAll(spec, pageable);

        var addresses = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResultDTO<>(
                addresses,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    private Specification<AddressJpaEntity> buildSpecification(AddressSearchCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.userId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), criteria.userId()));
            }

            if (criteria.city() != null && !criteria.city().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("city")),
                    "%" + criteria.city().toLowerCase() + "%"
                ));
            }

            if (criteria.state() != null && !criteria.state().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.upper(root.get("state")),
                    criteria.state().toUpperCase()
                ));
            }

            if (criteria.zipCode() != null && !criteria.zipCode().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("zipCode"), criteria.zipCode()));
            }

            if (criteria.type() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), criteria.type()));
            }

            if (criteria.primary() != null) {
                predicates.add(criteriaBuilder.equal(root.get("primary"), criteria.primary()));
            }

            if (criteria.active() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), criteria.active()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private PageRequest buildPageable(AddressSearchCriteriaDTO criteria) {
        Sort sort = Sort.unsorted();

        if (criteria.sortFields() != null && !criteria.sortFields().isEmpty()) {
            var direction = "DESC".equalsIgnoreCase(criteria.sortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sort = Sort.by(direction, criteria.sortFields().toArray(new String[0]));
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return PageRequest.of(
                criteria.page() != null ? criteria.page() : 0,
                criteria.size() != null ? criteria.size() : 20,
                sort
        );
    }
}

