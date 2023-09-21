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
@DiscriminatorValue("MEAL")
@SuperBuilder
@NoArgsConstructor
public class MealDeposit extends Deposit {

    public MealDeposit(BigDecimal amount, Long companyId, User user) {
        super(amount, companyId, user);
    }

    @Override
    protected Date defineExpirationDate() {
        return getNextYearLastDayOfFebruary();
    }

    private static Date getNextYearLastDayOfFebruary() {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, 1);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }
}
