package com.keepguard.ms_user.application.port.in;

import com.keepguard.ms_user.application.dto.address.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;

import java.util.List;

public interface AddressPort {

    // Commands
    AddressDetailsViewDTO create(AddressCreateCommandDTO command);
    AddressDetailsViewDTO update(AddressUpdateCommandDTO command);
    void delete(AddressDeleteCommandDTO command);

    // Queries
    AddressDetailsViewDTO getById(AddressGetByIdQueryDTO query);
    List<AddressDetailsViewDTO> getByUserId(AddressGetByUserIdQueryDTO query);
    PageResultDTO<AddressSearchViewDTO> search(AddressSearchQueryDTO query);
}

