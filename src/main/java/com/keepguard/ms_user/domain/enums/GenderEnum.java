package com.keepguard.ms_user.domain.enums;

public enum GenderEnum {
    MALE("Masculino"),
    FEMALE("Feminino"),
    OTHER("Outro"),
    NOT_INFORMED("Não informado");

    private final String description;

    GenderEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
