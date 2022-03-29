package io.sanlo.credit.risk.enums;

import lombok.Getter;

@Getter
public enum Order {
    ASC(1), DESC(-1);
    private final int sign;

    Order(int sign) {
        this.sign = sign;
    }
}
