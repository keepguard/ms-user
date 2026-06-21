package com.keepguard.ms_user.application.port.in;

import com.keepguard.ms_user.application.dto.contact.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;

import java.util.List;

public interface ContactPort {

    // Commands
    ContactDetailsViewDTO create(ContactCreateCommandDTO command);
    ContactDetailsViewDTO update(ContactUpdateCommandDTO command);
    void delete(ContactDeleteCommandDTO command);

    // Queries
    ContactDetailsViewDTO getById(ContactGetByIdQueryDTO query);
    List<ContactDetailsViewDTO> getByUserId(ContactGetByUserIdQueryDTO query);
    PageResultDTO<ContactSearchViewDTO> search(ContactSearchQueryDTO query);
}

