package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.application.dto.profile.CompanyProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.PersonProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.ProfileCommandDTO;
import com.keepguard.ms_user.application.validator.ValidLocale;
import com.keepguard.ms_user.application.validator.ValidPhone;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateCommandDTO(
    @NotNull(message = "id é obrigatório")
    UUID id,

    @NotNull(message = "companyId é obrigatório")
    UUID companyId,

    Optional<UUID> codeUser,
    Optional<UserTypeEnum> type,
    Optional<UserStatusEnum> status,
    Optional<@Email String> email,
    Optional<@ValidPhone String> phoneE164,
    Optional<@ValidLocale String> preferredLocale,
    Optional<String> timezone,
    Optional<String> avatarUrl,
    Optional<String> displayHandle,
    Optional<PersonProfileCommandDTO> personProfile,
    Optional<CompanyProfileCommandDTO> companyProfile
) implements ProfileCommandDTO {

    @Override
    public UserTypeEnum getUserType() {
        return type.orElse(null);
    }

    @Override
    public Object getProfileData() {
        UserTypeEnum userType = getUserType();
        if (userType == null) {
            return null;
        }

        return switch (userType) {
            case PERSON -> personProfile.orElse(null);
            case COMPANY -> companyProfile.orElse(null);
            default -> null;
        };
    }

    @Override
    public boolean hasProfileData() {
        return getProfileData() != null;
    }
}
