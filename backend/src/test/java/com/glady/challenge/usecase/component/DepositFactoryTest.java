package com.glady.challenge.usecase.component;

import com.glady.challenge.usecase.model.Company;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.model.deposit.Deposit;
import com.glady.challenge.usecase.model.deposit.GiftDeposit;
import com.glady.challenge.usecase.model.deposit.MealDeposit;
import com.glady.challenge.usecase.model.deposit.date.DateProvider;
import com.glady.challenge.usecase.openapi.model.DepositType;
import com.glady.challenge.usecase.util.TestUtilParent;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
class DepositFactoryTest extends TestUtilParent {

    @InjectMocks
    private DepositFactory depositFactory;
    @Mock
    private DateProvider dateProvider;

    @Test
    void getGiftDeposit()  {
        LocalDate localDate = new LocalDate(2021,6,15);
        Date date = localDate.toDate();
        when(dateProvider.getCurrentDate()).thenReturn(date);

        LocalDate expectedLocalDate = new LocalDate(2022,6,14);

        Company company = getBankruptedCompany();
        User user = getTestUserMat();
        Deposit deposit = depositFactory.makeDepositByType(DepositType.GIFT, BigDecimal.TEN, company.getCompanyId(), user);
        assertEquals("The deposit should be of type Gift", GiftDeposit.class, deposit.getClass());
        assertEquals("The amount should be 10",deposit.getAmount(),BigDecimal.TEN);
        assertEquals("The company id should be the equal to the above company", deposit.getCompanyId(), company.getCompanyId());
        assertEquals("The user should be the one above", deposit.getUser(), user);
        assertEquals("The expiration date should be 06/14/2022", expectedLocalDate.toDate(), deposit.getExpirationDate());
    }

    @Test
    void getMealDeposit()  {
        LocalDate localDate = new LocalDate(2020,1,1);
        Date date = localDate.toDate();
        when(dateProvider.getCurrentDate()).thenReturn(date);

        LocalDate expectedLocalDate = new LocalDate(2021,2,28);

        Company company = getBankruptedCompany();
        User user = getTestUserMat();
        Deposit deposit = depositFactory.makeDepositByType(DepositType.MEAL, BigDecimal.TEN, company.getCompanyId(), user);
        assertEquals("The deposit should be of type Meal", MealDeposit.class, deposit.getClass());
        assertEquals("The amount should be 10",deposit.getAmount(),BigDecimal.TEN);
        assertEquals("The company id should be the equal to the above company", deposit.getCompanyId(), company.getCompanyId());
        assertEquals("The user should be the one above", deposit.getUser(), user);
        assertEquals("The expiration date should be 28/02/2021", expectedLocalDate.toDate(), deposit.getExpirationDate());
    }
}
