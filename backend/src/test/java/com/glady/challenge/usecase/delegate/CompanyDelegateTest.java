package com.glady.challenge.usecase.delegate;

import com.glady.challenge.usecase.exception.CompanyNotFoundException;
import com.glady.challenge.usecase.exception.CompanyUserMismatchException;
import com.glady.challenge.usecase.exception.InsufficientBalanceException;
import com.glady.challenge.usecase.exception.UserNotFoundException;
import com.glady.challenge.usecase.model.Company;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.model.deposit.Deposit;
import com.glady.challenge.usecase.openapi.model.DepositInformation;
import com.glady.challenge.usecase.openapi.model.DepositType;
import com.glady.challenge.usecase.repository.CompanyRepository;
import com.glady.challenge.usecase.repository.DepositRepository;
import com.glady.challenge.usecase.util.TestUtilParent;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CompanyDelegateTest extends TestUtilParent {

    @Autowired
    private CompanyDelegate companyDelegate;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Test
    void distributeDeposit_shouldThrowUserNotFound() {
        User user = setupUnknownUserId();
        Company company = getWealthyCompany();
        DepositInformation depositInformation = getDepositInformation(DepositType.MEAL);

        Long userId = user.getUserId();
        Long companyId = company.getCompanyId();

        UserNotFoundException userNotFoundException = expectUserNotFoundException(
                userId,
                companyId,
                depositInformation
        );

        verifyUserNotFoundExceptionData(user, userNotFoundException, userId);
    }

    private User setupUnknownUserId() {
        User user = getTestUserMat();
        user.setUserId(139L);
        return user;
    }

    private UserNotFoundException expectUserNotFoundException(Long userId,
                                                              Long companyId,
                                                              DepositInformation depositInformation) {
        return assertThrows(UserNotFoundException.class, () ->
                companyDelegate.distributeDepositToUser(
                        userId,
                        companyId,
                        depositInformation)
        );
    }

    private void verifyUserNotFoundExceptionData(User user,
                                                        UserNotFoundException userNotFoundException,
                                                        Long userId) {
        assertEquals(
                user.getUserId(),
                userNotFoundException.getResourceId(),
                "The resource id should be : " + userId
        );
        assertEquals("User",
                userNotFoundException.getResourceName(),
                "The resource name should be User"
        );
    }

    @Test
    void distributeDeposit_shouldThrowCompanyNotFound() {
        User user = getTestUserMat();
        Company company = setupUnknownCompanyId();
        DepositInformation depositInformation = getDepositInformation(DepositType.MEAL);

        Long userId = user.getUserId();
        Long companyId = company.getCompanyId();

        CompanyNotFoundException companyNotFoundException = expectCompanyNotFoundException(
                userId,
                companyId,
                depositInformation
        );

        verifyCompanyNotFoundExceptionData(companyId, companyNotFoundException);
    }

    private Company setupUnknownCompanyId() {
        Company company = getWealthyCompany();
        company.setCompanyId(145L);
        return company;
    }

    private CompanyNotFoundException expectCompanyNotFoundException(Long userId,
                                                                    Long companyId,
                                                                    DepositInformation depositInformation) {
        return assertThrows(CompanyNotFoundException.class, () ->
                companyDelegate.distributeDepositToUser(
                        userId,
                        companyId,
                        depositInformation)
        );
    }

    private void verifyCompanyNotFoundExceptionData(Long companyId, CompanyNotFoundException companyNotFoundException) {
        assertEquals(
                companyId,
                companyNotFoundException.getResourceId(),
                "The resource id should be : " + companyId
        );
        assertEquals(
                "Company",
                companyNotFoundException.getResourceName(),
                "The resource name should be Company"
        );
    }

    @Test
    @Transactional
    void distributeDeposit_shouldThrowMismatchException() {
        User user = getTestUserPerrin();
        Company company = getWealthyCompany();
        DepositInformation depositInformation = getDepositInformation(DepositType.MEAL);

        Long userId = user.getUserId();
        Long companyId = company.getCompanyId();

        CompanyUserMismatchException mismatchException = expectCompanyUserMismatch(
                userId,
                companyId,
                depositInformation
        );

        verifyMismatchExceptionData(mismatchException);
    }

    private CompanyUserMismatchException expectCompanyUserMismatch(Long userId, Long companyId, DepositInformation depositInformation) {
        return assertThrows(CompanyUserMismatchException.class, () ->
                companyDelegate.distributeDepositToUser(
                        userId,
                        companyId,
                        depositInformation)
        );
    }

    private void verifyMismatchExceptionData(CompanyUserMismatchException mismatchException) {
        assertEquals(
                "User does not belong to the company",
                mismatchException.getErrorDetail(),
                "The error detail should match"
        );
        assertEquals(
                "The user Perrin Aybara does not belong the company : Wealthy & Co",
                mismatchException.getMessage(),
                "The error message should match"
        );
    }

    @Test
    @Transactional
    void distributeDeposit_shouldThrowInsufficientBalanceException() {
        User user = getTestUserPerrin();
        Company company = getBankruptedCompany();
        DepositInformation depositInformation = setupOverpricedAmount();

        Long userId = user.getUserId();
        Long companyId = company.getCompanyId();

        InsufficientBalanceException insufficientBalanceException = expectInsufficientFoundException(
                userId,
                companyId,
                depositInformation);

        verifyInsufficientBalanceExceptionData(insufficientBalanceException);
    }

    private DepositInformation setupOverpricedAmount() {
        DepositInformation depositInformation = getDepositInformation(DepositType.MEAL);
        depositInformation.setAmount(BigDecimal.valueOf(9999999999922L));
        return depositInformation;
    }

    private InsufficientBalanceException expectInsufficientFoundException(Long userId, Long companyId, DepositInformation depositInformation) {
        return assertThrows(InsufficientBalanceException.class, () ->
                companyDelegate.distributeDepositToUser(
                        userId,
                        companyId,
                        depositInformation)
        );
    }

    private void verifyInsufficientBalanceExceptionData(InsufficientBalanceException insufficientBalanceException) {
        assertEquals(
                "Insufficient Balance",
                insufficientBalanceException.getErrorDetail(),
                "The error detail should match"
        );
        assertEquals(
                "The company : Andoran Transport doesn't have a sufficient balance to perform the deposit",
                insufficientBalanceException.getMessage(),
                "The error message should match"
        );
    }

    @Test
    @Transactional
    void distributeDeposit_shouldSucceed() {
        User user = getTestUserMat();
        Company company = getWealthyCompany();
        DepositInformation depositInformation = getDepositInformation(DepositType.GIFT);

        BigDecimal companyInitialBalance = company.getBalance();
        BigDecimal expectedCompanyBalance = companyInitialBalance.subtract(depositInformation.getAmount());

        performSuccessfulDeposit(user, company, depositInformation);

        Company resultCompany = getUpdatedCompany(company);
        List<Deposit> deposits = getDeposits(user);

        verifyCompanyBalance(resultCompany, expectedCompanyBalance);
        verifyDepositCountAndData(deposits, company, user);

    }

    private void performSuccessfulDeposit(User user, Company company, DepositInformation depositInformation) {
        companyDelegate.distributeDepositToUser(
                user.getUserId(),
                company.getCompanyId(),
                depositInformation
        );
    }

    private Company getUpdatedCompany(Company company) {
        return companyRepository.findById(company.getCompanyId())
                .orElseThrow(
                        () -> new CompanyNotFoundException(company.getCompanyId())
                );
    }

    private List<Deposit> getDeposits(User user) {
        return depositRepository.findAllByUser_UserId(user.getUserId());
    }

    private static void verifyCompanyBalance(Company resultCompany, BigDecimal expectedCompanyBalance) {
        assertEquals(
                0,
                resultCompany.getBalance().compareTo(expectedCompanyBalance),
                "The company balance should be lowered by the amount of the deposit"
        );
    }

    private void verifyDepositCountAndData(List<Deposit> deposits, Company company, User user) {
        assertEquals(
                1,
                deposits.size(),
                "There should be only one deposit saved in the database"
        );

        Deposit deposit = deposits.get(0);

        assertEquals(
                company.getCompanyId(),
                deposit.getCompanyId(),
                "The companyId should be matching"
        );

        assertEquals(
                user.getUserId(),
                deposit.getUser().getUserId(),
                "The userId should be matching"
        );

        assertEquals(
                BigDecimal.TEN,
                deposit.getAmount(),
                "The amount should be ten"
        );
    }
}
