package com.keepguard.ms_user.application.dto.register;

import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterInitCommandDTO(
    @NotNull(message = "companyId é obrigatório")
    UUID companyId,

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    @Size(max = 255, message = "email deve ter no máximo 255 caracteres")
    String email,

    @NotBlank(message = "nameFull é obrigatório")
    @Size(max = 255, message = "nameFull deve ter no máximo 255 caracteres")
    String nameFull,

    @NotBlank(message = "password é obrigatório")
    @Size(min = 8, max = 100, message = "password deve ter entre 8 e 100 caracteres")
    String password,

    @NotBlank(message = "phone é obrigatório")
    @Size(max = 20, message = "phone deve ter no máximo 20 caracteres")
    String phone,

    @NotNull(message = "hasAcceptedTermsAndPrivacy é obrigatório")
    Boolean hasAcceptedTermsAndPrivacy,

    Boolean acceptedMarketing,

    @Size(max = 45, message = "ipAddress deve ter no máximo 45 caracteres")
    String ipAddress,

    @Size(max = 500, message = "userAgent deve ter no máximo 500 caracteres")
    String userAgent,

    @Size(max = 255, message = "geolocation deve ter no máximo 255 caracteres")
    String geolocation,

    @NotNull(message = "type é obrigatório")
    UserTypeEnum type
) {}

