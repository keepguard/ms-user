package com.keepguard.ms_user.domain.enums;

public enum KycLevelEnum {
    BASIC("Básico"),
    INTERMEDIATE("Intermediário"),
    ADVANCED("Avançado"),
    PREMIUM("Premium");

    private final String description;

    KycLevelEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
