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

    public GiftDeposit(BigDecimal amount, Long companyId, User user, Date currentDate) {
        super(amount, companyId, user, currentDate);
    }

    @Override
    protected Date defineExpirationDate(Date currentDate) {
        return getCurrentDatePlusOneYear(currentDate);
    }

    private static Date getCurrentDatePlusOneYear(Date currentDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Unsure here if there is missing information in the test statement

        return calendar.getTime();
    }
}
