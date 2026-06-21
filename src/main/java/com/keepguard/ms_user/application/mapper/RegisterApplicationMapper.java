package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.application.dto.register.RegisterInitCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitViewDTO;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
public class RegisterApplicationMapper {

    public RegisterSession toDomain(RegisterInitCommandDTO command, String token, String passwordHash) {
        return RegisterSession.create(
                UUID.randomUUID(),
                command.xApplication(),
                command.email(),
                token,
                passwordHash,
                command.nameFull(),
                command.phone(),
                command.hasAcceptedTermsAndPrivacy(),
                command.acceptedMarketing(),
                command.ipAddress(),
                command.userAgent(),
                command.geolocation(),
                command.type()
        );
    }

    public RegisterInitViewDTO toView(RegisterSession session, String message, Integer expiresIn) {
        return new RegisterInitViewDTO(
                session.getRegistrationSessionId(),
                session.getEmail(),
                expiresIn,
                message,
                session.getToken()
        );
    }
}

