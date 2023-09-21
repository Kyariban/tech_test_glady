package com.glady.challenge.usecase.delegate;

import com.glady.challenge.usecase.component.DepositFactory;
import com.glady.challenge.usecase.exception.UserNotFoundException;
import com.glady.challenge.usecase.model.Company;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.model.deposit.Deposit;
import com.glady.challenge.usecase.openapi.model.DepositType;
import com.glady.challenge.usecase.openapi.model.UserBalance;
import com.glady.challenge.usecase.repository.DepositRepository;
import com.glady.challenge.usecase.util.TestUtilParent;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDelegateTest extends TestUtilParent {

    @Autowired
    private UserDelegate userDelegate;

    @Autowired
    private DepositFactory depositFactory;

    @Autowired
    private DepositRepository depositRepository;

    @Test
    void getBalance_shouldThrowUserNotFound() {
        Long userId = 150L;
        UserNotFoundException userNotFoundException = expectNotFoundThrow(userId);
        assertExceptionData(userId, userNotFoundException);
    }

    private UserNotFoundException expectNotFoundThrow(Long userId) {
        return assertThrows(UserNotFoundException.class, () ->
                userDelegate.getBalanceForUser(userId)
        );
    }

    private static void assertExceptionData(Long userId, UserNotFoundException userNotFoundException) {
        assertEquals(userId, userNotFoundException.getResourceId(),"The resource id should be : " + userId);
        assertEquals(
                "User",
                userNotFoundException.getResourceName(),
                "The resource name should be User"
        );
    }

    @Test
    @Transactional
    void getBalance_shouldWorkNormally() {
        Company company = getWealthyCompany();
        User user = getTestUserPerrin();

        setupTestDeposit(company, user);
        getBalanceAndAssertData(user);

    }


    private void setupTestDeposit(Company company, User user) {
        Deposit mealDeposit1 = depositFactory.makeDepositByType(
                DepositType.MEAL,
                BigDecimal.valueOf(500L),
                company.getCompanyId(),
                user
        );

        Deposit mealDeposit2 = depositFactory.makeDepositByType(
                DepositType.MEAL,
                BigDecimal.valueOf(150L),
                company.getCompanyId(),
                user
        );

        Deposit giftDeposit1 = depositFactory.makeDepositByType(
                DepositType.GIFT,
                BigDecimal.valueOf(200L),
                company.getCompanyId(),
                user
        );

        Deposit giftDeposit2 = depositFactory.makeDepositByType(
                DepositType.GIFT,
                BigDecimal.valueOf(400L),
                company.getCompanyId(),
                user
        );

        Deposit giftDeposit3 = depositFactory.makeDepositByType(
                DepositType.GIFT,
                BigDecimal.valueOf(220L),
                company.getCompanyId(),
                user
        );

        depositRepository.save(mealDeposit1);
        depositRepository.save(mealDeposit2);
        depositRepository.save(giftDeposit1);
        depositRepository.save(giftDeposit2);
        depositRepository.save(giftDeposit3);
    }

    private void getBalanceAndAssertData(User user) {
        UserBalance balanceForUser = userDelegate.getBalanceForUser(user.getUserId());

        assertEquals(user.getUserId(), balanceForUser.getUserId(),"The userId should be matching");
        assertEquals("USD", balanceForUser.getCurrency(),"The currency should be in USD");
        assertEquals(BigDecimal.valueOf(820L), balanceForUser.getGiftAmount(),"The gift amount must be right");
        assertEquals(BigDecimal.valueOf(650L), balanceForUser.getMealAmount(),"The meal amount must be right");
    }
}
