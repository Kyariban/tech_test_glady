package com.glady.challenge.usecase.model.deposit;

import com.glady.challenge.usecase.model.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@DiscriminatorValue("MEAL")
@SuperBuilder
public class MealDeposit extends Deposit {


    public MealDeposit(BigDecimal amount, Long companyId, User user) {
        super(amount, companyId, user);
    }

    @Override
    protected Date defineExpirationDate() {
        return null;
    }
}
