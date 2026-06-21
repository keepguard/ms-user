package com.keepguard.ms_user.domain.enums;

public enum KycStatusEnum {
    NOT_STARTED("Não iniciado"),
    IN_PROGRESS("Em andamento"),
    PENDING_APPROVAL("Aguardando aprovação"),
    APPROVED("Aprovado"),
    REJECTED("Rejeitado"),
    EXPIRED("Expirado");

    private final String description;

    KycStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
