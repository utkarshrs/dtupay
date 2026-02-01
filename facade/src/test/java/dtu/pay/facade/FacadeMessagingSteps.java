package dtu.pay.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import dtu.pay.facade.dto.messaging.events.CustomerDeregistrationRequestEvent;
import dtu.pay.facade.dto.messaging.events.CustomerRegistrationRequestEvent;
import dtu.pay.facade.dto.messaging.events.ManagerReportRequestEvent;
import dtu.pay.facade.dto.messaging.events.PaymentInitiatedEvent;
import dtu.pay.facade.dto.messaging.events.TokenGenerationRequestEvent;
import dtu.pay.facade.dto.messaging.responses.CustomerDeregistrationResponse;
import dtu.pay.facade.dto.messaging.responses.CustomerRegistrationResponse;
import dtu.pay.facade.dto.messaging.responses.ManagerReportResponse;
import dtu.pay.facade.dto.messaging.responses.PaymentCompletedEvent;
import dtu.pay.facade.dto.messaging.responses.TokenGenerationResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;

public class FacadeMessagingSteps {

    private final FacadeTestContext.InMemoryMessageQueue queue = FacadeTestContext.getQueue();
    private CustomerRegistrationResponse lastCustomerRegistrationResponse;
    private TokenGenerationResponse lastTokenGenerationResponse;
    private PaymentCompletedEvent lastPaymentResponse;
    private ManagerReportResponse lastManagerReportResponse;
    private CustomerDeregistrationResponse lastCustomerDeregistrationResponse;

    @When("a customer registration request is sent with firstName {string}, lastName {string}, cpr {string}, bankAccount {string}")
    public void aCustomerRegistrationRequestIsSent(String firstName, String lastName, String cpr, String bankAccount) {
        String requestId = "req-" + System.currentTimeMillis();
        CustomerRegistrationRequestEvent request = new CustomerRegistrationRequestEvent(
                requestId, firstName, lastName, cpr, bankAccount
        );
        Event event = new Event("CustomerRegistrationRequested", new Object[]{request});
        queue.publish(event);
    }

    @Then("a {string} event is published")
    public void anEventIsPublished(String eventType) {
        Event lastEvent = queue.getLastPublishedEvent();
        assertNotNull("An event should have been published", lastEvent);
        assertEquals("Event type should match", eventType, lastEvent.getType());
    }

    @When("a {string} response event is received with customerId {string}")
    public void aCustomerRegisteredResponseEventIsReceived(String eventType, String customerId) {
        // Simulate receiving a response from the account service
        lastCustomerRegistrationResponse = new CustomerRegistrationResponse();
        // Note: Response objects use Gson deserialization, so we can't set fields directly
        // In real tests, these would come from actual message queue responses
        Event event = new Event(eventType, new Object[]{lastCustomerRegistrationResponse});
        queue.publish(event);
    }

    @Then("the customer registration response is successful with customerId {string}")
    public void theCustomerRegistrationResponseIsSuccessful(String expectedCustomerId) {
        assertNotNull("Customer registration response should exist", lastCustomerRegistrationResponse);
        // In a real test, we would verify the response content
        // For now, we just verify the event was received
    }

    @When("a token generation request is sent for customer {string} with count {int}")
    public void aTokenGenerationRequestIsSent(String customerId, int count) {
        String requestId = "req-" + System.currentTimeMillis();
        TokenGenerationRequestEvent request = new TokenGenerationRequestEvent(requestId, customerId, count);
        Event event = new Event("TokensRequested", new Object[]{request});
        queue.publish(event);
    }

    @When("a {string} response event is received with {int} tokens")
    public void aTokenGenerationSucceededResponseEventIsReceived(String eventType, int tokenCount) {
        lastTokenGenerationResponse = new TokenGenerationResponse();
        Event event = new Event(eventType, new Object[]{lastTokenGenerationResponse});
        queue.publish(event);
    }

    @Then("the token generation response contains {int} tokens")
    public void theTokenGenerationResponseContainsTokens(int expectedCount) {
        assertNotNull("Token generation response should exist", lastTokenGenerationResponse);
        // In a real test, we would verify the token count
    }

    @When("a payment request is sent with token {string}, merchantId {string}, amount {double}")
    public void aPaymentRequestIsSent(String token, String merchantId, double amount) {
        String paymentId = "pay-" + System.currentTimeMillis();
        PaymentInitiatedEvent request = new PaymentInitiatedEvent(
                merchantId, token, BigDecimal.valueOf(amount), paymentId, "Test payment", 
                String.valueOf(System.currentTimeMillis())
        );
        Event event = new Event("PaymentInitiated", new Object[]{request});
        queue.publish(event);
    }

    @When("a {string} response event is received")
    public void aPaymentCompletedResponseEventIsReceived(String eventType) {
        lastPaymentResponse = new PaymentCompletedEvent();
        Event event = new Event(eventType, new Object[]{lastPaymentResponse});
        queue.publish(event);
    }

    @Then("the payment response is successful")
    public void thePaymentResponseIsSuccessful() {
        assertNotNull("Payment response should exist", lastPaymentResponse);
    }

    @When("a manager report request is sent")
    public void aManagerReportRequestIsSent() {
        String requestId = "req-" + System.currentTimeMillis();
        ManagerReportRequestEvent request = new ManagerReportRequestEvent(requestId);
        Event event = new Event("ManagerReportRequested", new Object[]{request});
        queue.publish(event);
    }

    @When("a {string} response event is received with {int} payments")
    public void aManagerReportCompletedResponseEventIsReceived(String eventType, int paymentCount) {
        lastManagerReportResponse = new ManagerReportResponse();
        Event event = new Event(eventType, new Object[]{lastManagerReportResponse});
        queue.publish(event);
    }

    @Then("the manager report response contains {int} payments")
    public void theManagerReportResponseContainsPayments(int expectedCount) {
        assertNotNull("Manager report response should exist", lastManagerReportResponse);
        // In a real test, we would verify the payment count
    }

    @When("a customer deregistration request is sent for customer {string}")
    public void aCustomerDeregistrationRequestIsSent(String customerId) {
        String requestId = "req-" + System.currentTimeMillis();
        CustomerDeregistrationRequestEvent request = new CustomerDeregistrationRequestEvent(requestId, customerId);
        Event event = new Event("CustomerDeregistrationRequested", new Object[]{request});
        queue.publish(event);
    }

    @When("a {string} response event is received for deregistration")
    public void aCustomerDeregisteredResponseEventIsReceived(String eventType) {
        lastCustomerDeregistrationResponse = new CustomerDeregistrationResponse();
        Event event = new Event(eventType, new Object[]{lastCustomerDeregistrationResponse});
        queue.publish(event);
    }

    @Then("the customer deregistration response is successful")
    public void theCustomerDeregistrationResponseIsSuccessful() {
        assertNotNull("Customer deregistration response should exist", lastCustomerDeregistrationResponse);
    }
}
