package dtu.pay.steps;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dtu.pay.dto.PaymentRequest;
import dtu.pay.dto.PaymentResponse;
import dtu.pay.service.FacadeClient;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PaymentSteps {

    private final FacadeClient client = SharedSteps.getClient();

    // Payment state
    private PaymentResponse paymentResponse;

    @When("the merchant initiates a payment of {int} kr using the customer's token")
    public void theMerchantInitiatesPaymentUsingToken(int amount) {
        assertFalse(SharedSteps.customerTokens.isEmpty(), "Customer should have tokens");
        String token = SharedSteps.customerTokens.remove(0);
        
        PaymentRequest request = new PaymentRequest(
                token,
                BigDecimal.valueOf(amount),
                ""
        );
        paymentResponse = client.initiatePayment(SharedSteps.merchantId, request);
    }

    @When("the merchant initiates a payment of {int} kr with description {string}")
    public void theMerchantInitiatesPaymentWithDescription(int amount, String description) {
        assertFalse(SharedSteps.customerTokens.isEmpty(), "Customer should have tokens");
        String token = SharedSteps.customerTokens.remove(0);
        
        PaymentRequest request = new PaymentRequest(
                token,
                BigDecimal.valueOf(amount),
                description
        );
        paymentResponse = client.initiatePayment(SharedSteps.merchantId, request);
    }

    @When("the merchant initiates a payment of {int} kr using invalid token {string}")
    public void theMerchantInitiatesPaymentUsingInvalidToken(int amount, String invalidToken) {
        PaymentRequest request = new PaymentRequest(
                invalidToken,
                BigDecimal.valueOf(amount),
                ""
        );
        paymentResponse = client.initiatePayment(SharedSteps.merchantId, request);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(paymentResponse.isSuccess(),
                "Payment should be successful but got error: " + paymentResponse.getErrorMessage());
    }

    @Then("the payment fails")
    public void thePaymentFails() {
        assertNotNull(paymentResponse, "Payment response should not be null");
        assertFalse(paymentResponse.isSuccess(),
                "Payment should fail but was successful");
        assertNotNull(paymentResponse.getErrorMessage(),
                "Error message should be present for failed payment");
    }

    @Then("the customer's bank balance is {int} kr")
    public void theCustomersBankBalanceIs(int expectedBalance) {
        BigDecimal actualBalance = SharedSteps.getBankHelper().getBalance(SharedSteps.customerBankAccount);
        assertEquals(0, BigDecimal.valueOf(expectedBalance).compareTo(actualBalance),
                "Customer bank balance should be " + expectedBalance + " kr");
    }

    @Then("the merchant's bank balance is {int} kr")
    public void theMerchantsBankBalanceIs(int expectedBalance) {
        BigDecimal actualBalance = SharedSteps.getBankHelper().getBalance(SharedSteps.merchantBankAccount);
        assertEquals(0, BigDecimal.valueOf(expectedBalance).compareTo(actualBalance),
                "Merchant bank balance should be " + expectedBalance + " kr");
    }

    @Then("the payment amount is {int} kr")
    public void thePaymentAmountIs(int expectedAmount) {
        assertNotNull(paymentResponse.getAmount(), "Payment amount should not be null");
        assertEquals(0, BigDecimal.valueOf(expectedAmount).compareTo(paymentResponse.getAmount()),
                "Payment amount should be " + expectedAmount + " kr");
    }

    @Then("the error message contains {string}")
    public void theErrorMessageContains(String expectedText) {
        assertNotNull(paymentResponse, "Payment response should not be null");
        assertNotNull(paymentResponse.getErrorMessage(), "Error message should not be null");
        assertTrue(paymentResponse.getErrorMessage().contains(expectedText),
                "Error message should contain '" + expectedText + "' but was: " + paymentResponse.getErrorMessage());
    }
}
