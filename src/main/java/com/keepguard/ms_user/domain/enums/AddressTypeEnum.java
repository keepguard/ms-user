package com.keepguard.ms_user.domain.enums;

public enum AddressTypeEnum {
    RESIDENTIAL("Residencial"),
    COMMERCIAL("Comercial"),
    BILLING("Cobrança"),
    DELIVERY("Entrega"),
    WORK("Trabalho"),
    OTHER("Outro");

    private final String description;

    AddressTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
