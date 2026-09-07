package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.application.dto.profile.CompanyProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.PersonProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.ProfileCommandDTO;
import com.keepguard.ms_user.application.validator.ValidLocale;
import com.keepguard.ms_user.application.validator.ValidPhone;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserCreateCommandDTO(
    @NotNull(message = "companyId é obrigatório")
    UUID companyId,

    UUID tenantId,

    @NotNull(message = "type é obrigatório")
    UserTypeEnum type,

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    String email,

    @ValidPhone
    String phoneE164,

    @ValidLocale
    String preferredLocale,

    String timezone,
    String avatarUrl,

    String displayHandle,

    @Valid
    PersonProfileCommandDTO personProfile,

    @Valid
    CompanyProfileCommandDTO companyProfile
) implements ProfileCommandDTO {

    @Override
    public UserTypeEnum getUserType() {
        return type;
    }

    @Override
    public Object getProfileData() {
        return switch (type) {
            case PERSON -> personProfile;
            case COMPANY -> companyProfile;
            default -> null;
        };
    }

    @Override
    public boolean hasProfileData() {
        return getProfileData() != null;
    }
}
