package com.keepguard.ms_user.application.service.user.strategy.profile;

import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileStrategyTypeEnum {

    PERSON_PROFILE(UserTypeEnum.PERSON, PersonProfileStrategy.class),
    COMPANY_PROFILE(UserTypeEnum.COMPANY, CompanyProfileStrategy.class);

    private final UserTypeEnum userType;
    private final Class<? extends ProfileStrategy> strategyClass;

    public static ProfileStrategyTypeEnum fromUserType(UserTypeEnum userType) {
        for (ProfileStrategyTypeEnum strategyType : values()) {
            if (strategyType.getUserType().equals(userType)) {
                return strategyType;
            }
        }
        throw new IllegalArgumentException("Tipo de usuário não suportado: " + userType);
    }

    public static boolean isSupported(UserTypeEnum userType) {
        try {
            fromUserType(userType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getStrategyName() {
        return strategyClass.getSimpleName();
    }

    public Class<? extends ProfileStrategy> getStrategyClass() {
        return strategyClass;
    }
}
