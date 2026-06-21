package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.application.dto.profile.ProfileCommandDTO;
import com.keepguard.ms_user.application.validator.ValidLocale;
import com.keepguard.ms_user.application.validator.ValidPhone;
import com.keepguard.lib_validation.moderation.application.validator.ModeratedContent;
import com.keepguard.lib_validation.moderation.domain.model.ModerationCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.util.UUID;

public record UserCreateCommandDTO(
    @NotNull(message = "companyId é obrigatório")
    UUID companyId,

    @NotNull(message = "xApplication é obrigatório")
    UUID xApplication,

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

    /** Apelido (display_handle) do usuário; pode vir do request em user ou personProfile. */
    String displayHandle,

    @Valid
    PersonProfile personProfile,
    
    @Valid
    CompanyProfile companyProfile
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
