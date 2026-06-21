package com.keepguard.ms_user.adapters.in.rest.register.mapper;

import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterConfirmRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterInitRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.request.RegisterResendRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.response.RegisterConfirmResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.response.RegisterInitResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.register.dto.response.RegisterResendResponseDTO;
import com.keepguard.ms_user.application.dto.register.RegisterConfirmCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitViewDTO;
import com.keepguard.ms_user.application.dto.register.RegisterResendCommandDTO;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegisterAdapterMapper {

    public RegisterInitCommandDTO toInitCommand(RegisterInitRequestDTO request, UUID xApplication) {
        return new RegisterInitCommandDTO(
                xApplication,
                request.email(),
                request.nameFull(),
                request.password(),
                request.phone(),
                request.hasAcceptedTermsAndPrivacy(),
                request.acceptedMarketing(),
                request.ipAddress(),
                request.userAgent(),
                request.geolocation(),
                request.type()
        );
    }

    public RegisterConfirmCommandDTO toConfirmCommand(RegisterConfirmRequestDTO request, UUID xApplication) {
        return new RegisterConfirmCommandDTO(
                xApplication,
                request.registrationSessionId(),
                request.email(),
                request.token()
        );
    }

    public RegisterInitResponseDTO toResponseDTO(RegisterInitViewDTO view) {
        if (view == null) {
            return null;
        }

        return RegisterInitResponseDTO.builder()
                .registrationSessionId(view.registrationSessionId())
                .email(view.email())
                .expiresIn(view.expiresIn())
                .message(view.message())
                .token(view.token())
                .build();
    }

    public RegisterConfirmResponseDTO toConfirmResponseDTO(RegisterSession session) {
        if (session == null) {
            return null;
        }

        return RegisterConfirmResponseDTO.builder()
                .registrationSessionId(session.getRegistrationSessionId())
                .xApplication(session.getXApplication())
                .email(session.getEmail())
                .nameFull(session.getNameFull())
                .phone(session.getPhone())
                .type(session.getType())
                .hasAcceptedTermsAndPrivacy(session.getHasAcceptedTermsAndPrivacy())
                .acceptedMarketing(session.getAcceptedMarketing())
                .ipAddress(session.getIpAddress())
                .userAgent(session.getUserAgent())
                .geolocation(session.getGeolocation())
                .createdAt(session.getCreatedAt())
                .attempts(session.getAttempts())
                .message("Registro confirmado com sucesso")
                .passwordHash(session.getPasswordHash())
                .build();
    }

    public RegisterResendCommandDTO toResendCommand(RegisterResendRequestDTO request, UUID xApplication) {
        return new RegisterResendCommandDTO(
                xApplication,
                request.email(),
                request.registrationSessionId()
        );
    }

    public RegisterResendResponseDTO toResendResponseDTO(RegisterSession session, int maxResendAttempts, int ttlSeconds) {
        return new RegisterResendResponseDTO(
                "Token reenviado com sucesso",
                session.getEmail(),
                session.getNameFull(),
                session.getToken(),
                session.getRegistrationSessionId().toString(),
                maxResendAttempts - session.getResendAttempts(),
                ttlSeconds
        );
    }
}

