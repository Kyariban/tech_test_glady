package com.glady.challenge.usecase.delegate;

import com.glady.challenge.usecase.exception.UserNotFoundException;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.model.deposit.Deposit;
import com.glady.challenge.usecase.model.deposit.GiftDeposit;
import com.glady.challenge.usecase.model.deposit.MealDeposit;
import com.glady.challenge.usecase.openapi.model.UserBalance;
import com.glady.challenge.usecase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDelegate {

    private final UserRepository userRepository;

    public UserBalance getBalanceForUser(Long userId) {
        User user = getUser(userId);
        BigDecimal mealDepositBalance = getBalanceByDepositType(user, MealDeposit.class);
        BigDecimal giftDepositBalance = getBalanceByDepositType(user, GiftDeposit.class);

        return new UserBalance()
                .userId(userId)
                .giftAmount(giftDepositBalance)
                .mealAmount(mealDepositBalance)
                .currency("USD");
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private <T extends Deposit> BigDecimal getBalanceByDepositType(User user, Class<T> depositClass) {
        return user.getDeposits()
                .stream()
                .filter(depositClass::isInstance)
                .map(Deposit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
