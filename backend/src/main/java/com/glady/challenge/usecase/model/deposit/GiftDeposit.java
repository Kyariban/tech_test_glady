package com.glady.challenge.usecase.model.deposit;

import com.glady.challenge.usecase.model.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Entity
@DiscriminatorValue("GIFT")
@SuperBuilder
@NoArgsConstructor
public class GiftDeposit extends Deposit {

    public GiftDeposit(BigDecimal amount, Long companyId, User user) {
        super(amount, companyId, user);
    }

    @Override
    protected Date defineExpirationDate() {
        return getCurrentDatePlusOneYear();
    }

    private static Date getCurrentDatePlusOneYear() {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, 1);

        return calendar.getTime();
    }
}
