package com.glady.challenge.usecase.delegate;

import com.glady.challenge.usecase.openapi.model.UserBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDelegate {

    public UserBalance getBalanceForUser(Long userId) {
        return null;
    }
}
