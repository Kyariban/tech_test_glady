package com.glady.challenge.usecase.delegate;

import com.glady.challenge.usecase.exception.CompanyNotFoundException;
import com.glady.challenge.usecase.exception.InsufficientBalanceException;
import com.glady.challenge.usecase.exception.UserNotFoundException;
import com.glady.challenge.usecase.model.Company;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.model.deposit.Deposit;
import com.glady.challenge.usecase.model.deposit.DepositFactory;
import com.glady.challenge.usecase.openapi.model.DepositInformation;
import com.glady.challenge.usecase.repository.CompanyRepository;
import com.glady.challenge.usecase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyDelegate {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public void distributeDepositToUser(Long userId, Long companyId, DepositInformation depositInformation) {

        Company company = getCompany(companyId);
        User user = getUser(userId);

        checkCompanyHasSufficientBalance(depositInformation, company);
        Deposit deposit = performDepositToUser(companyId, depositInformation, user);
        updateCompanyBalance(company, deposit);
    }

    private Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void checkCompanyHasSufficientBalance(DepositInformation depositInformation, Company company) {
        if(company.getBalance().compareTo(depositInformation.getAmount()) < 0) {
            throw new InsufficientBalanceException("The company : " + company.getCompanyName() + " doesn't have a sufficient balance to perform the deposit");
        }
    }

    private Deposit performDepositToUser(Long companyId, DepositInformation depositInformation, User user) {
        Deposit deposit = createDeposit(user, companyId, depositInformation);
        user.getDeposits().add(deposit);
        userRepository.save(user);
        return deposit;
    }

    private Deposit createDeposit(User user, Long companyId, DepositInformation depositInformation) {
        return DepositFactory.makeDepositByType(depositInformation.getDepositType(),
                depositInformation.getAmount(),
                companyId,
                user);
    }

    private void updateCompanyBalance(Company company, Deposit deposit) {
        BigDecimal companyNewBalance = company.getBalance().subtract(deposit.getAmount());
        company.setBalance(companyNewBalance);
        companyRepository.save(company);
    }
}
