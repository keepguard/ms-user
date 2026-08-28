package com.keepguard.ms_user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio para sessão de registro de usuário.
 * Representa os dados temporários armazenados no Redis durante o processo de registro.
 */
@Getter
public final class RegisterSession {

    private final UUID registrationSessionId;
    
    @JsonProperty("companyId")
    private final UUID companyId;
    private final String email;
    private final String token; // Legado / fallback
    private final String emailToken;
    private final String smsToken;
    private final String whatsAppToken;
    private final String passwordHash;
    private final String nameFull;
    private final String phone;
    private final Boolean hasAcceptedTermsAndPrivacy;
    private final Boolean acceptedMarketing;
    private final String ipAddress;
    private final String userAgent;
    private final String geolocation;
    private final UserTypeEnum type;
    private final OffsetDateTime createdAt;
    private Integer attempts;
    private Integer resendAttempts;

    @JsonCreator
    private RegisterSession(
            @JsonProperty("registrationSessionId") UUID registrationSessionId, 
            @JsonProperty("companyId") UUID companyId, 
            @JsonProperty("email") String email, 
            @JsonProperty("token") String token,
            @JsonProperty("emailToken") String emailToken,
            @JsonProperty("smsToken") String smsToken,
            @JsonProperty("whatsAppToken") String whatsAppToken,
            @JsonProperty("passwordHash") String passwordHash, 
            @JsonProperty("nameFull") String nameFull, 
            @JsonProperty("phone") String phone, 
            @JsonProperty("hasAcceptedTermsAndPrivacy") Boolean hasAcceptedTermsAndPrivacy,
            @JsonProperty("acceptedMarketing") Boolean acceptedMarketing, 
            @JsonProperty("ipAddress") String ipAddress, 
            @JsonProperty("userAgent") String userAgent, 
            @JsonProperty("geolocation") String geolocation,
            @JsonProperty("type") UserTypeEnum type, 
            @JsonProperty("createdAt") OffsetDateTime createdAt, 
            @JsonProperty("attempts") Integer attempts,
            @JsonProperty("resendAttempts") Integer resendAttempts) {
        this.registrationSessionId = registrationSessionId;
        this.companyId = validateTenantId(companyId);
        this.email = validateEmail(email);
        this.token = token != null ? token : emailToken;
        this.emailToken = emailToken != null ? emailToken : token;
        this.smsToken = smsToken;
        this.whatsAppToken = whatsAppToken;
        this.passwordHash = passwordHash;
        this.nameFull = nameFull;
        this.phone = phone;
        this.hasAcceptedTermsAndPrivacy = hasAcceptedTermsAndPrivacy;
        this.acceptedMarketing = acceptedMarketing;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.geolocation = geolocation;
        this.type = type;
        this.createdAt = createdAt;
        this.attempts = attempts != null ? attempts : 0;
        this.resendAttempts = resendAttempts != null ? resendAttempts : 0;
    }

    public static RegisterSession create(UUID registrationSessionId, UUID companyId, String email, String token,
                                        String passwordHash, String nameFull, String phone,
                                        Boolean hasAcceptedTermsAndPrivacy, Boolean acceptedMarketing,
                                        String ipAddress, String userAgent, String geolocation,
                                        UserTypeEnum type) {
        return create(registrationSessionId, companyId, email, token, token, null, null,
                passwordHash, nameFull, phone, hasAcceptedTermsAndPrivacy, acceptedMarketing,
                ipAddress, userAgent, geolocation, type);
    }

    public static RegisterSession create(UUID registrationSessionId, UUID companyId, String email, 
                                        String emailToken, String smsToken, String whatsAppToken,
                                        String passwordHash, String nameFull, String phone,
                                        Boolean hasAcceptedTermsAndPrivacy, Boolean acceptedMarketing,
                                        String ipAddress, String userAgent, String geolocation,
                                        UserTypeEnum type) {
        return create(registrationSessionId, companyId, email, emailToken, emailToken, smsToken, whatsAppToken,
                passwordHash, nameFull, phone, hasAcceptedTermsAndPrivacy, acceptedMarketing,
                ipAddress, userAgent, geolocation, type);
    }

    public static RegisterSession create(UUID registrationSessionId, UUID companyId, String email, 
                                        String token, String emailToken, String smsToken, String whatsAppToken,
                                        String passwordHash, String nameFull, String phone,
                                        Boolean hasAcceptedTermsAndPrivacy, Boolean acceptedMarketing,
                                        String ipAddress, String userAgent, String geolocation,
                                        UserTypeEnum type) {
        return new RegisterSession(
                registrationSessionId,
                companyId,
                email,
                token,
                emailToken,
                smsToken,
                whatsAppToken,
                passwordHash,
                nameFull,
                phone,
                hasAcceptedTermsAndPrivacy,
                acceptedMarketing,
                ipAddress,
                userAgent,
                geolocation,
                type,
                OffsetDateTime.now(),
                0,
                0
        );
    }

    public static RegisterSession of(UUID registrationSessionId, UUID companyId, String email, String token,
                                    String passwordHash, String nameFull, String phone,
                                    Boolean hasAcceptedTermsAndPrivacy, Boolean acceptedMarketing,
                                    String ipAddress, String userAgent, String geolocation,
                                    UserTypeEnum type, OffsetDateTime createdAt, Integer attempts, Integer resendAttempts) {
        return new RegisterSession(
                registrationSessionId,
                companyId,
                email,
                token,
                token,
                null,
                null,
                passwordHash,
                nameFull,
                phone,
                hasAcceptedTermsAndPrivacy,
                acceptedMarketing,
                ipAddress,
                userAgent,
                geolocation,
                type,
                createdAt,
                attempts,
                resendAttempts
        );
    }

    private UUID validateTenantId(UUID companyId) {
        if (companyId == null) {
            throw new ValidationException("companyId é obrigatório");
        }
        return companyId;
    }

    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email é obrigatório");
        }
        String trimmedEmail = email.trim().toLowerCase();
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new ValidationException("Email inválido");
        }
        return trimmedEmail;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void resetAttempts() {
        this.attempts = 0;
    }

    public void incrementResendAttempts() {
        this.resendAttempts++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterSession that = (RegisterSession) o;
        return Objects.equals(registrationSessionId, that.registrationSessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationSessionId);
    }
}

