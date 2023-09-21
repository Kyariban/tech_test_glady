package com.glady.challenge.usecase.openapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glady.challenge.usecase.delegate.CompanyDelegate;
import com.glady.challenge.usecase.exception.CompanyNotFoundException;
import com.glady.challenge.usecase.exception.CompanyUserMismatchException;
import com.glady.challenge.usecase.exception.InsufficientBalanceException;
import com.glady.challenge.usecase.exception.UserNotFoundException;
import com.glady.challenge.usecase.model.Company;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.openapi.model.DepositInformation;
import com.glady.challenge.usecase.openapi.model.DepositType;
import com.glady.challenge.usecase.util.TestUtilParent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Stubber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyApiController.class)
@ExtendWith(SpringExtension.class)
class CompanyApiControllerTest extends TestUtilParent {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyDelegate companyDelegate;

    @Test
    void performDepositShouldBeOk() throws Exception {
        User user = getTestUserMat();
        Company company = getWealthyCompany();
        performPostAndExpectNoContent(company, user);
    }

    @Test
    void performDepositWithUserNotFound() throws Exception {
        User user = getTestUserMat();
        Company company = getWealthyCompany();

        mockExceptionWhenCallingDelegate(doThrow(new UserNotFoundException(user.getUserId())));
        performPostAndVerityNotFoundError(company, user, "User with id : %d not found", user.getUserId());
    }

    @Test
    void performDepositWithCompanyNotFound() throws Exception {
        User user = getTestUserMat();
        Company company = getWealthyCompany();

        mockExceptionWhenCallingDelegate(doThrow(new CompanyNotFoundException(company.getCompanyId())));
        performPostAndVerityNotFoundError(company, user, "Company with id : %d not found", company.getCompanyId());
    }

    @Test
    void performDepositWithInsufficientBalance() throws Exception {
        User user = getTestUserPerrin();
        Company company = getBankruptedCompany();

        mockExceptionWhenCallingDelegate(doThrow(new InsufficientBalanceException("Insufficient balance")));
        performPostAndVerifyInsufficientBalanceError(company, user);
    }

    @Test
    void performDepositWithCompanyUserMismatch() throws Exception {
        User user = getTestUserPerrin();
        Company company = getBankruptedCompany();

        mockExceptionWhenCallingDelegate(doThrow(new CompanyUserMismatchException("Mismatch")));
        performPostAndVerifyMismatchError(company, user);
    }

    private void performPostAndExpectNoContent(Company company, User user) throws Exception {
        this.mockMvc
                .perform(
                        post("/api/company/{companyId}/user/{userId}/deposit", company.getCompanyId(), user.getUserId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(getDepositInformation(DepositType.MEAL))))
                .andExpect(status().isNoContent());
    }

    private void mockExceptionWhenCallingDelegate(Stubber stubber) {
        stubber
                .when(companyDelegate).distributeDepositToUser(
                        anyLong(),
                        anyLong(),
                        any()
                );
    }

    private void performPostAndVerityNotFoundError(Company company, User user, String format, Long techId) throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(
                        post("/api/company/{companyId}/user/{userId}/deposit", company.getCompanyId(), user.getUserId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(getDepositInformation(DepositType.MEAL))))
                .andExpect(status().isNotFound());

        resultActions.andExpect(jsonPath("$.errors[0].detail").value("Resource not found"))
                .andExpect(
                        jsonPath("$.errors[0].message")
                                .value(String.format(format, techId))
                );
    }

    private void performPostAndVerifyInsufficientBalanceError(Company company, User user) throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(
                        post("/api/company/{companyId}/user/{userId}/deposit", company.getCompanyId(), user.getUserId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(getDepositInformation(DepositType.GIFT))))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(jsonPath("$.errors[0].detail").value("Insufficient Balance"))
                .andExpect(
                        jsonPath("$.errors[0].message")
                                .value("Insufficient balance")
                );
    }

    private void performPostAndVerifyMismatchError(Company company, User user) throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(
                        post("/api/company/{companyId}/user/{userId}/deposit", company.getCompanyId(), user.getUserId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(getDepositInformation(DepositType.MEAL))))
                .andExpect(status().isBadRequest());

        resultActions.andExpect(jsonPath("$.errors[0].detail").value("User does not belong to the company"))
                .andExpect(
                        jsonPath("$.errors[0].message")
                                .value("Mismatch")
                );
    }

    private String asJsonString(DepositInformation depositInformation) {
        try {
            return objectMapper.writeValueAsString(depositInformation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

