package com.keepguard.ms_user.application.service.user.strategy.profile;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileStrategyFactory {

    private final List<ProfileStrategy> strategies;
    private Map<ProfileStrategyTypeEnum, ProfileStrategy> strategyMap;

    private void initializeStrategyMap() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(
                            strategy -> ProfileStrategyTypeEnum.fromUserType(
                                    strategy.supports(UserTypeEnum.PERSON) ? UserTypeEnum.PERSON : UserTypeEnum.COMPANY
                            ),
                            Function.identity()
                    ));
            log.info("Mapa de strategies inicializado com {} strategies", strategyMap.size());
        }
    }

    public ProfileStrategy getStrategy(UserTypeEnum userType) {
        log.debug("Buscando strategy para tipo de usuário: {}", userType);

        initializeStrategyMap();

        try {
            ProfileStrategyTypeEnum strategyType = ProfileStrategyTypeEnum.fromUserType(userType);
            ProfileStrategy strategy = strategyMap.get(strategyType);

            if (strategy == null) {
                log.error("Strategy não encontrada no mapa para tipo: {}", strategyType);
                throw new ValidationException("Strategy não encontrada para tipo: " + userType);
            }

            log.debug("Strategy encontrada: {} para tipo: {}", strategyType.getStrategyName(), userType);
            return strategy;

        } catch (IllegalArgumentException e) {
            log.error("Tipo de usuário não suportado: {}", userType);
            throw new ValidationException("Tipo de usuário não suportado: " + userType);
        }
    }

    public boolean hasStrategy(UserTypeEnum userType) {
        try {
            return ProfileStrategyTypeEnum.isSupported(userType);
        } catch (Exception e) {
            log.warn("Erro ao verificar suporte para tipo: {} - {}", userType, e.getMessage());
            return false;
        }
    }

    public ProfileStrategyTypeEnum getStrategyType(UserTypeEnum userType) {
        return ProfileStrategyTypeEnum.fromUserType(userType);
    }
}
