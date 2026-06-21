package com.keepguard.ms_user.application.port.out.persistence;

import com.keepguard.ms_user.application.dto.address.AddressSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.domain.entity.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepositoryPort {

    Address save(Address address);
    Optional<Address> findById(UUID id);
    List<Address> findByUserId(UUID userId);
    Optional<Address> findByUserIdAndPrimaryTrue(UUID userId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    PageResultDTO<Address> search(AddressSearchCriteriaDTO criteria);
}

