package com.glady.challenge.usecase.model.deposit.date;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateProviderImpl implements DateProvider{
    @Override
    public Date getCurrentDate() {
        return new Date();
    }
}
