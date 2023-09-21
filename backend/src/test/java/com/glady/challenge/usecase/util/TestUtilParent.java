package com.glady.challenge.usecase.util;

import com.glady.challenge.usecase.model.Company;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.openapi.model.DepositInformation;
import com.glady.challenge.usecase.openapi.model.DepositType;

import java.math.BigDecimal;

public class TestUtilParent {

    protected Company getWealthyCompany() {
        return Company.builder()
                .companyId(1234L)
                .companyName("Wealthy & Co")
                .balance(BigDecimal.valueOf(150000))
                .build();
    }

    protected Company getBankruptedCompany() {
        return Company.builder()
                .companyId(5678L)
                .companyName("Andoran Transport")
                .balance(BigDecimal.valueOf(15))
                .build();
    }

    protected User getTestUserMat()  {
        return User.builder()
                .userId(1L)
                .username("Matrim Cauthon")
                .company(getWealthyCompany())
                .build();
    }

    protected User getTestUserPerrin()  {
        return User.builder()
                .userId(2L)
                .username("Perrin Aybara")
                .company(getBankruptedCompany())
                .build();
    }

    protected DepositInformation getDepositInformation(DepositType depositType) {
        return new DepositInformation()
                .amount(BigDecimal.TEN)
                .depositType(depositType);
    }

}
