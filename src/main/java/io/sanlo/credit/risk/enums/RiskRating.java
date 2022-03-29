package io.sanlo.credit.risk.enums;

import lombok.Getter;

@Getter
public enum RiskRating {
    UNDOUBTED("Undoubted"),
    LOW("Low"),
    MODERATE("Moderate"),
    UNSATISFACTORY("Unsatisfactory"),
    UNACCEPTABLE("Unacceptable");

    RiskRating(String name) {
        this.name = name;
    }

    private final String name;
}
