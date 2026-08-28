package com.keepguard.ms_user.application.service.register;

import com.keepguard.ms_user.application.dto.register.RegisterInitQueryDTO;
import com.keepguard.ms_user.application.port.out.cache.RegisterCachePort;
import com.keepguard.ms_user.domain.entity.RegisterSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterQueryService {

    private final RegisterCachePort registerCachePort;

    public Optional<RegisterSession> getRegisterSession(RegisterInitQueryDTO query) throws JsonProcessingException {
        log.info("Buscando sessão de registro: email={}, companyId={}", 
                query.email(), query.companyId());
        
        return registerCachePort.getRegisterSession(query.email(), query.companyId());
    }

    public boolean existsRegisterSession(RegisterInitQueryDTO query) {
        log.info("Verificando existência de sessão de registro: email={}, companyId={}", 
                query.email(), query.companyId());
        
        return registerCachePort.existsRegisterSession(query.email(), query.companyId());
    }
}

