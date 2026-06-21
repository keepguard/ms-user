package com.keepguard.ms_user.domain.enums;

public enum ContactTypeEnum {
    PHONE("Telefone"),
    MOBILE("Celular"),
    WHATSAPP("WhatsApp"),
    TELEGRAM("Telegram"),
    SKYPE("Skype"),
    OTHER("Outro");

    private final String description;

    ContactTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
