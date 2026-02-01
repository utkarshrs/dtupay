package dtu.pay.payment;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import dtu.pay.payment.dto.CustomerBankAccountResolved;
import dtu.pay.payment.dto.MerchantBankAccountResolved;
import dtu.pay.payment.dto.PaymentInitiatedEvent;
import dtu.pay.payment.dto.TokenValidationFailedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;

public class PaymentSteps {

    private final PaymentTestContext.InMemoryMessageQueue queue = PaymentTestContext.getQueue();
    private PaymentCompletedEvent lastCompletedEvent;
    private PaymentFailedEvent lastFailedEvent;

    @Given("the bank transfer is configured to fail")
    public void the_bank_transfer_is_configured_to_fail() {
        PaymentTestContext.getBankAdapter().setShouldFail(true);
    }

    @When("the {string} event is sent for payment {string}, token {string}, merchant {string}, amount {double}, description {string}, timestamp {string}")
    public void the_payment_requested_event_is_sent(String eventType,
                                                    String paymentId,
                                                    String token,
                                                    String merchantId,
                                                    double amount,
                                                    String description,
                                                    String timestamp) {
        PaymentInitiatedEvent request = new PaymentInitiatedEvent(
                merchantId,
                token,
                BigDecimal.valueOf(amount),
                paymentId,
                description,
                timestamp
        );
        Event event = new Event(eventType, new Object[]{request});
        queue.publish(event);
    }

    @When("the {string} event is sent for payment {string}, customer {string}, from account {string}")
    public void the_customer_bank_account_resolved_event_is_sent(String eventType,
                                                                 String paymentId,
                                                                 String customerId,
                                                                 String fromAccountId) {
        CustomerBankAccountResolved customer = new CustomerBankAccountResolved(
                paymentId,
                customerId,
                fromAccountId
        );
        Event event = new Event(eventType, new Object[]{customer});
        queue.publish(event);
    }

    @When("the {string} event is sent for payment {string}, merchant {string}, to account {string}")
    public void the_merchant_bank_account_resolved_event_is_sent(String eventType,
                                                                 String paymentId,
                                                                 String merchantId,
                                                                 String toAccountId) {
        MerchantBankAccountResolved merchant = new MerchantBankAccountResolved(
                paymentId,
                merchantId,
                toAccountId
        );
        Event event = new Event(eventType, new Object[]{merchant});
        queue.publish(event);
        Event response = queue.getLastPublishedEvent();
        if (response != null) {
            if ("PaymentCompleted".equals(response.getType())) {
                lastCompletedEvent = response.getArgument(0, PaymentCompletedEvent.class);
            } else if ("PaymentFailed".equals(response.getType())) {
                lastFailedEvent = response.getArgument(0, PaymentFailedEvent.class);
            }
        }
    }

    @When("the {string} event is sent for payment {string}, token {string}, reason {string}")
    public void the_token_validation_failed_event_is_sent(String eventType,
                                                          String paymentId,
                                                          String token,
                                                          String reason) {
        TokenValidationFailedEvent event = new TokenValidationFailedEvent(
                paymentId,
                token,
                reason
        );
        Event messageEvent = new Event(eventType, new Object[]{event});
        queue.publish(messageEvent);
        Event response = queue.getLastPublishedEvent();
        if (response != null && "PaymentFailed".equals(response.getType())) {
            lastFailedEvent = response.getArgument(0, PaymentFailedEvent.class);
        }
    }

    @Then("a {string} event was published")
    public void an_event_was_published(String expectedType) {
        Event last = queue.getLastPublishedEvent();
        assertNotNull("No event was published", last);
        assertEquals(expectedType, last.getType());
    }

    @Then("the completed payment has amount {double}, customer {string}, merchant {string}, description {string}")
    public void the_completed_payment_has_details(double amount,
                                                  String customerId,
                                                  String merchantId,
                                                  String description) {
        assertNotNull("No completed payment captured", lastCompletedEvent);
        assertEquals(BigDecimal.valueOf(amount), lastCompletedEvent.getAmount());
        assertEquals(customerId, lastCompletedEvent.getCustomerId());
        assertEquals(merchantId, lastCompletedEvent.getMerchantId());
        assertEquals(description, lastCompletedEvent.getDescription());
    }

    @Then("the error message contains {string}")
    public void the_error_message_contains(String expectedText) {
        assertNotNull("No failed payment captured", lastFailedEvent);
        assertNotNull("Error message is null", lastFailedEvent.getErrorMessage());

        String actual = lastFailedEvent.getErrorMessage().toLowerCase();
        String expected = expectedText.toLowerCase();

        assertTrue(
            "Error message does not contain expected text. Expected: '" + expectedText +
            "', Actual: '" + lastFailedEvent.getErrorMessage() + "'",
            actual.contains(expected)
        );
    }

}
