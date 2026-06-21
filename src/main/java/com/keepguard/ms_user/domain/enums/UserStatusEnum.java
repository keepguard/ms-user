package com.keepguard.ms_user.domain.enums;

public enum UserStatusEnum {
    ACTIVE("Ativo"),
    INACTIVE("Inativo"),
    PENDING("Pendente"),
    BLOCKED("Bloqueado"),
    SUSPENDED("Suspenso");

    private final String description;

    UserStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserStatusEnum fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UserStatusEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status de usuário inválido: " + value);
        }
    }
}
