package com.glady.challenge.usecase.model.deposit;

import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.openapi.model.DepositType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DepositFactory {

    public static Deposit makeDepositByType(DepositType depositType, BigDecimal amount, Long companyId, User user) {
        Deposit deposit;
        switch (depositType) {
            case GIFT -> deposit = new GiftDeposit(amount, companyId, user);
            case MEAL -> deposit = new MealDeposit(amount, companyId, user);
            default -> throw new UnsupportedOperationException("The provided Deposit Type is not implemented yet");
        }
        return deposit;
    }
}
