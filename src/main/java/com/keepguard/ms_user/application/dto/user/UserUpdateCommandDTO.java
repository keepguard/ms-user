package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.application.dto.profile.ProfileCommandDTO;
import com.keepguard.ms_user.application.validator.ValidLocale;
import com.keepguard.ms_user.application.validator.ValidPhone;
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
    /** Apelido (display_handle) do usuário; pode vir do request em personProfile. */
    Optional<String> displayHandle,
    Optional<PersonProfile> personProfile,
    Optional<CompanyProfile> companyProfile
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
