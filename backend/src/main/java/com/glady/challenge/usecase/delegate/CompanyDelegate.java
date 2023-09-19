package com.glady.challenge.usecase.delegate;

import com.glady.challenge.usecase.openapi.model.DepositInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyDelegate {
    public void distributeDepositToUser(Long userId, Long companyId, DepositInformation depositInformation) {
        // moj c_
    }
}
