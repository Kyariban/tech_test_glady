package com.glady.challenge.usecase.openapi.api;

import com.glady.challenge.usecase.delegate.UserDelegate;
import com.glady.challenge.usecase.exception.UserNotFoundException;
import com.glady.challenge.usecase.model.User;
import com.glady.challenge.usecase.openapi.model.UserBalance;
import com.glady.challenge.usecase.util.TestUtilParent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
@ExtendWith(SpringExtension.class)
class UserApiControllerTest extends TestUtilParent {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDelegate userDelegate;

    @Test
    void getUserBalanceShouldReturnUserBalance() throws Exception {
        User user = getTestUserMat();
        mockGetBalance(user);
        performGetAndCheckResults(user);
    }

    @Test
    void getUserBalanceShouldFailWithNotFound() throws Exception {
        User user = getTestUserPerrin();
        mockGetBalanceThrowsUserNotFoundException(user);
        performGetExpect4xxAndCheckErrorMessage(user);
    }

    private void mockGetBalance(User user) {
        UserBalance userBalance = new UserBalance();
        userBalance
                .currency("USD")
                .giftAmount(BigDecimal.TEN)
                .mealAmount(BigDecimal.ONE)
                .userId(user.getUserId());

        when(userDelegate.getBalanceForUser(anyLong())).thenReturn(userBalance);
    }

    private void performGetAndCheckResults(User user) throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(get("/api/user/{userId}/balance", user.getUserId()))
                .andExpect(status().isOk());

        resultActions.andExpect(jsonPath("$.userId").value(user.getUserId()))
                .andExpect(jsonPath("$.giftAmount").value(BigDecimal.TEN))
                .andExpect(jsonPath("$.mealAmount").value(BigDecimal.ONE))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    private void mockGetBalanceThrowsUserNotFoundException(User user) {
        when(userDelegate.getBalanceForUser(anyLong())).thenThrow(new UserNotFoundException(user.getUserId()));
    }

    private void performGetExpect4xxAndCheckErrorMessage(User user) throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(get("/api/user/{userId}/balance", user.getUserId()))
                .andExpect(status().isNotFound());

        resultActions.andExpect(jsonPath("$.errors[0].detail").value("Resource not found"))
                .andExpect(
                        jsonPath("$.errors[0].message")
                                .value(String.format("User with id : %d not found", user.getUserId()))
                );
    }

}
