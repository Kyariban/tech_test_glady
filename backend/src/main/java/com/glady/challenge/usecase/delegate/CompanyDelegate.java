package com.glady.challenge.usecase.delegate;

import com.glady.challenge.usecase.exception.*;
import com.glady.challenge.usecase.model.Company;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.model.deposit.Deposit;
import com.glady.challenge.usecase.component.DepositFactory;
import com.glady.challenge.usecase.openapi.model.DepositInformation;
import com.glady.challenge.usecase.repository.CompanyRepository;
import com.glady.challenge.usecase.repository.DepositRepository;
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
    private final DepositRepository depositRepository;
    private final DepositFactory depositFactory;

    public void distributeDepositToUser(Long userId, Long companyId, DepositInformation depositInformation) {
        checkMissingBodyParam(depositInformation);

        Company company = getCompany(companyId);
        User user = getUser(userId);

        checkIfUserBelongsToCompany(company, user);
        checkCompanyHasSufficientBalance(depositInformation, company);
        Deposit deposit = performDepositToUser(companyId, depositInformation, user);
        updateCompanyBalance(company, deposit);
    }

    private static void checkMissingBodyParam(DepositInformation depositInformation) {
        if(depositInformation.getAmount() == null)  {
            throw new MissingBodyParameterException("Missing parameter amount");
        }

        if(depositInformation.getDepositType() == null) {
            throw new MissingBodyParameterException("Missing parameter depositType");
        }
    }


    private Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void checkIfUserBelongsToCompany(Company company, User user) {
        if(!company.equals(user.getCompany())) {
            throw new CompanyUserMismatchException(
                    String.format("The user %s does not belong the company : %s",
                            user.getUsername(),
                            company.getCompanyName()
                    )
            );
        }
    }

    private void checkCompanyHasSufficientBalance(DepositInformation depositInformation, Company company) {
        if(company.getBalance().compareTo(depositInformation.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    String.format("The company : %s doesn't have a sufficient balance to perform the deposit",
                            company.getCompanyName()
                    )
            );
        }
    }

    private Deposit performDepositToUser(Long companyId, DepositInformation depositInformation, User user) {
        Deposit deposit = createDeposit(user, companyId, depositInformation);
        user.getDeposits().add(deposit);
        return depositRepository.save(deposit);
    }

    private Deposit createDeposit(User user, Long companyId, DepositInformation depositInformation) {
        return depositFactory.makeDepositByType(depositInformation.getDepositType(),
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
