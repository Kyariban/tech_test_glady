package com.glady.challenge.usecase.component;

import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.model.deposit.Deposit;
import com.glady.challenge.usecase.model.deposit.GiftDeposit;
import com.glady.challenge.usecase.model.deposit.MealDeposit;
import com.glady.challenge.usecase.model.deposit.date.DateProvider;
import com.glady.challenge.usecase.openapi.model.DepositType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DepositFactory {
    private DateProvider dateProvider;
    @Autowired
    public DepositFactory(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    public Deposit makeDepositByType(DepositType depositType, BigDecimal amount, Long companyId, User user) {
        Deposit deposit;
        switch (depositType) {
            case GIFT -> deposit = new GiftDeposit(amount, companyId, user, dateProvider.getCurrentDate());
            case MEAL -> deposit = new MealDeposit(amount, companyId, user, dateProvider.getCurrentDate());
            default -> throw new UnsupportedOperationException("The provided Deposit Type is not implemented yet");
        }
        return deposit;
    }
}
