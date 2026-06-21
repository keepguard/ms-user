package com.keepguard.ms_user.domain.enums;

public enum UserTypeEnum {
    PERSON("Pessoa Física"),
    COMPANY("Pessoa Jurídica");

    private final String description;

    UserTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserTypeEnum fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UserTypeEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de usuário inválido: " + value);
        }
    }
}
