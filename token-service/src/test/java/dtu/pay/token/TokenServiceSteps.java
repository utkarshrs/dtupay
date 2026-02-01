package dtu.pay.token;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;

import java.math.BigDecimal;

import dtu.pay.token.dto.TokenGenerationRequest;
import dtu.pay.token.dto.TokenGenerationResponse;
import dtu.pay.token.dto.TokenValidationResponse;

import static org.junit.Assert.*;

public class TokenServiceSteps {

    private final TokenTestContext.InMemoryMessageQueue queue = TokenTestContext.getQueue();
    private String lastGeneratedToken;

    @When("the {string} event is sent with customer id {string}")
    public void the_customer_created_event_is_sent(String eventType, String customerId) {
        CustomerCreatedEvent customerEvent = new CustomerCreatedEvent(null, customerId);
        Event event = new Event(eventType, new Object[]{customerEvent});
        queue.publish(event);
    }

    @When("the {string} event is sent for customer {string} with count {int}")
    public void the_tokens_requested_event_is_sent(String eventType, String customerId, int count) {
        String requestId = "test-request-" + System.currentTimeMillis();
        TokenGenerationRequest request = new TokenGenerationRequest(requestId, customerId, count);
        Event event = new Event(eventType, new Object[]{request});
        queue.publish(event);

        Event responseEvent = queue.getLastPublishedEvent();
        if ("TokenGenerationSucceeded".equals(responseEvent.getType())) {
            TokenGenerationResponse response = responseEvent.getArgument(0, TokenGenerationResponse.class);
            if (response.getTokens() != null && !response.getTokens().isEmpty()) {
                lastGeneratedToken = response.getTokens().get(0).getValue().toString();
            }
        }
    }

    @When("the {string} event is sent with the generated token, merchant {string}, amount {double}, payment id {string}")
    public void the_payment_initiated_event_is_sent_with_generated_token(String eventType,
                                                                          String merchantId,
                                                                          double amount,
                                                                          String paymentId) {
        PaymentInitiatedEvent payload = new PaymentInitiatedEvent(
                merchantId,
                lastGeneratedToken,
                BigDecimal.valueOf(amount),
                paymentId
        );
        Event event = new Event(eventType, new Object[]{payload});
        queue.publish(event);
    }

    @When("the {string} event is sent with token {string}, merchant {string}, amount {double}, payment id {string}")
    public void the_payment_initiated_event_is_sent_with_token(String eventType,
                                                                String token,
                                                                String merchantId,
                                                                double amount,
                                                                String paymentId) {
        PaymentInitiatedEvent payload = new PaymentInitiatedEvent(
                merchantId,
                token,
                BigDecimal.valueOf(amount),
                paymentId
        );
        Event event = new Event(eventType, new Object[]{payload});
        queue.publish(event);
    }

    @When("the {string} event is sent with the same token, merchant {string}, amount {double}, payment id {string}")
    public void the_payment_initiated_event_is_sent_with_same_token(String eventType,
                                                                     String merchantId,
                                                                     double amount,
                                                                     String paymentId) {
        the_payment_initiated_event_is_sent_with_generated_token(eventType, merchantId, amount, paymentId);
    }

    @Then("the last event should be {string}")
    public void the_last_event_should_be(String expectedEventType) {
        Event lastEvent = queue.getLastPublishedEvent();
        assertNotNull("An event should have been published", lastEvent);
        assertEquals(expectedEventType, lastEvent.getType());
    }

    @Then("a {string} event was published")
    public void aEventWasPublished(String expectedEventType) {
        Event lastEvent = queue.getLastPublishedEvent();
        assertNotNull("An event should have been published", lastEvent);
        assertEquals(expectedEventType, lastEvent.getType());
    }

    @Then("the response should contain {int} tokens")
    public void the_response_should_contain_tokens(int expectedCount) {
        Event lastEvent = queue.getLastPublishedEvent();
        TokenGenerationResponse response = lastEvent.getArgument(0, TokenGenerationResponse.class);
        assertNotNull("Response should not be null", response);
        assertEquals(expectedCount, response.getTokens().size());
    }

    @Then("{int} tokens are generated for customer {string}")
    public void tokensAreGeneratedForCustomer(int expectedCount, String customerId) {
        Event lastEvent = queue.getLastPublishedEvent();
        TokenGenerationResponse response = lastEvent.getArgument(0, TokenGenerationResponse.class);
        assertNotNull("Response should not be null", response);
        assertEquals(expectedCount, response.getTokens().size());
    }

    @Then("the error message should be {string}")
    public void the_error_message_should_be(String expectedMessage) {
        Event lastEvent = queue.getLastPublishedEvent();
        String eventType = lastEvent.getType();

        if ("TokenGenerationDenied".equals(eventType)) {
            TokenGenerationResponse response = lastEvent.getArgument(0, TokenGenerationResponse.class);
            assertEquals(expectedMessage, response.getErrorMessage());
        } else if ("TokenValidationFailed".equals(eventType)) {
            TokenValidationResponse response = lastEvent.getArgument(0, TokenValidationResponse.class);
            assertEquals(expectedMessage, response.getErrorMessage());
        } else {
            fail("Unexpected event type: " + eventType);
        }
    }

    @Then("the error message contains {string}")
    public void theErrorMessageContains(String expectedMessage) {
        Event lastEvent = queue.getLastPublishedEvent();
        String eventType = lastEvent.getType();

        if ("TokenGenerationDenied".equals(eventType)) {
            TokenGenerationResponse response = lastEvent.getArgument(0, TokenGenerationResponse.class);
            assertNotNull("Error message should not be null", response.getErrorMessage());
            assertTrue("Error message should contain '" + expectedMessage + "', but was: " + response.getErrorMessage(),
                    response.getErrorMessage().contains(expectedMessage));
        } else if ("TokenValidationFailed".equals(eventType)) {
            TokenValidationResponse response = lastEvent.getArgument(0, TokenValidationResponse.class);
            assertNotNull("Error message should not be null", response.getErrorMessage());
            assertTrue("Error message should contain '" + expectedMessage + "', but was: " + response.getErrorMessage(),
                    response.getErrorMessage().contains(expectedMessage));
        } else {
            fail("Unexpected event type: " + eventType);
        }
    }

    @Then("the token is marked as used for customer {string}")
    public void theTokenIsMarkedAsUsedForCustomer(String customerId) {
        Event lastEvent = queue.getLastPublishedEvent();
        TokenValidationResponse response = lastEvent.getArgument(0, TokenValidationResponse.class);
        assertNotNull("Response should not be null", response);
        assertEquals(customerId, response.getCustomerId());
    }

    @Then("the validation response should contain customer id {string}")
    public void the_validation_response_should_contain_customer_id(String expectedCustomerId) {
        Event lastEvent = queue.getLastPublishedEvent();
        TokenValidationResponse response = lastEvent.getArgument(0, TokenValidationResponse.class);
        assertNotNull("Response should not be null", response);
        assertEquals(expectedCustomerId, response.getCustomerId());
    }
}
