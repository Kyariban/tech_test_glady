package com.glady.challenge.usecase.openapi.api;

import com.glady.challenge.usecase.delegate.UserDelegate;
import com.glady.challenge.usecase.openapi.model.UserBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class UserApiController implements UserApi {

    private final UserDelegate userDelegate;

    @Override
    public ResponseEntity<UserBalance> getBalanceForUser(Long userId) {
        return ResponseEntity.ok().body(userDelegate.getBalanceForUser(userId));
    }
}
