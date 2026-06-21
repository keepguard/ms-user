package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.dto.contact.ContactSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.out.persistence.ContactRepositoryPort;
import com.keepguard.ms_user.domain.entity.Contact;
import com.keepguard.ms_user.infrastructure.persistence.entity.ContactJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.ContactJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.ContactSpringRepository;
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
public class ContactRepositoryAdapter implements ContactRepositoryPort {

    private final ContactSpringRepository springRepository;
    private final ContactJpaMapper mapper;

    @Override
    public Contact save(Contact contact) {
        var jpaEntity = mapper.toJpa(contact);
        var savedEntity = springRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Contact> findById(UUID id) {
        return springRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Contact> findByUserId(UUID userId) {
        return springRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
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
    public PageResultDTO<Contact> search(ContactSearchCriteriaDTO criteria) {
        var spec = buildSpecification(criteria);
        var pageable = buildPageable(criteria);

        var page = springRepository.findAll(spec, pageable);

        var contacts = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResultDTO<>(
                contacts,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    private Specification<ContactJpaEntity> buildSpecification(ContactSearchCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.userId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), criteria.userId()));
            }

            if (criteria.value() != null && !criteria.value().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    root.get("value"),
                    "%" + criteria.value() + "%"
                ));
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

    private PageRequest buildPageable(ContactSearchCriteriaDTO criteria) {
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

