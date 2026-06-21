package com.keepguard.ms_user.domain.enums;

public enum MaritalStatusEnum {
    SINGLE("Solteiro(a)"),
    MARRIED("Casado(a)"),
    DIVORCED("Divorciado(a)"),
    WIDOWED("Viúvo(a)"),
    SEPARATED("Separado(a)"),
    NOT_INFORMED("Não informado");

    private final String description;

    MaritalStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
