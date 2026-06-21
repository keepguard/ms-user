package com.keepguard.ms_user.domain.enums;

public enum IncomeRangeEnum {
    UP_TO_1_MINIMUM_WAGE("Até 1 salário mínimo"),
    FROM_1_TO_2_MINIMUM_WAGES("De 1 a 2 salários mínimos"),
    FROM_2_TO_5_MINIMUM_WAGES("De 2 a 5 salários mínimos"),
    FROM_5_TO_10_MINIMUM_WAGES("De 5 a 10 salários mínimos"),
    FROM_10_TO_20_MINIMUM_WAGES("De 10 a 20 salários mínimos"),
    ABOVE_20_MINIMUM_WAGES("Acima de 20 salários mínimos"),
    NOT_INFORMED("Não informado");

    private final String description;

    IncomeRangeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
