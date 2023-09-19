package com.glady.challenge.usecase.openapi.api;

import com.glady.challenge.usecase.delegate.CompanyDelegate;
import com.glady.challenge.usecase.openapi.model.DepositInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class CompanyApiController implements CompanyApi {

    private final CompanyDelegate companyDelegate;

    @Override
    public ResponseEntity<Void> distributeDepositToUser(Long userId,
                                                        Long companyId,
                                                        DepositInformation depositInformation) {
        companyDelegate.distributeDepositToUser(
                userId,
                companyId,
                depositInformation
        );
        return ResponseEntity.noContent().build();
    }
}
