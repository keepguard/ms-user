package com.keepguard.ms_user.application.port.out.persistence;

import com.keepguard.ms_user.application.dto.contact.ContactSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.domain.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepositoryPort {

    Contact save(Contact contact);
    Optional<Contact> findById(UUID id);
    List<Contact> findByUserId(UUID userId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    PageResultDTO<Contact> search(ContactSearchCriteriaDTO criteria);
}

