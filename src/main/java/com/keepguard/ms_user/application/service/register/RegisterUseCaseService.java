package com.keepguard.ms_user.application.service.register;

import com.keepguard.ms_user.application.dto.register.RegisterInitCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitViewDTO;
import com.keepguard.ms_user.application.dto.register.RegisterConfirmCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterResendCommandDTO;
import com.keepguard.ms_user.application.port.in.RegisterPort;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class RegisterUseCaseService implements RegisterPort {

    private final RegisterCommandService registerCommandService;

    @Override
    public RegisterInitViewDTO init(RegisterInitCommandDTO command) {
        log.info("Iniciando caso de uso de registro: email={}, tenantId={}", 
                command.email(), command.tenantId());
        
        return registerCommandService.init(command);
    }

    @Override
    public RegisterSession confirm(RegisterConfirmCommandDTO command) {
        log.info("Iniciando caso de uso de confirmação de registro: email={}, tenantId={}", 
                command.email(), command.tenantId());
        
        return registerCommandService.confirm(command);
    }

    @Override
    public RegisterSession resend(RegisterResendCommandDTO command) {
        log.info("Iniciando caso de uso de reenvio de token: email={}, tenantId={}", 
                command.email(), command.tenantId());
        
        return registerCommandService.resend(command);
    }
}

