package com.keepguard.ms_user.application.dto.user;

import java.util.UUID;

public record UserPatchPersonDocumentCommandDTO(
    UUID id,
    UUID companyId,
    String cpf
) {}
