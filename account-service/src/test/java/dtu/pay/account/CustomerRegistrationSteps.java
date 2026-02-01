package dtu.pay.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import dtu.pay.account.dto.CustomerDeregistrationRequest;
import dtu.pay.account.dto.CustomerDeregistrationResponse;
import dtu.pay.account.dto.CustomerLookupRequest;
import dtu.pay.account.dto.CustomerLookupResponse;
import dtu.pay.account.dto.CustomerRegistrationRequest;
import dtu.pay.account.dto.CustomerRegistrationResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;

public class CustomerRegistrationSteps {

    private final AccountTestContext.InMemoryMessageQueue queue = AccountTestContext.getQueue();
    private CustomerRegistrationResponse lastRegistrationResponse;
    private CustomerDeregistrationResponse lastDeregistrationResponse;
    private CustomerLookupResponse lastLookupResponse;

    @When("a {string} event is sent with firstName {string}, lastName {string}, cpr {string}, bankAccount {string}, requestId {string}")
    public void a_customer_registration_event_is_sent(String eventType, String firstName, String lastName,
                                                       String cpr, String bankAccount, String requestId) {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                requestId, firstName, lastName, cpr, bankAccount
        );
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastRegistrationResponse = responseEvent.getArgument(0, CustomerRegistrationResponse.class);
    }

    @Then("a {string} event is published")
    public void a_response_event_is_published(String expectedType) {
        Event last = queue.getLastPublishedEvent();
        assertNotNull("No event was published", last);
        assertEquals(expectedType, last.getType());
    }

    @Then("the customer registration is successful")
    public void the_customer_registration_is_successful() {
        assertNotNull("Customer registration response should exist", lastRegistrationResponse);
        assertTrue("Customer registration should be successful", lastRegistrationResponse.isSuccess());
        assertNotNull("Customer ID should be assigned", lastRegistrationResponse.getCustomerId());
    }

    @Then("the customer registration response contains a customer ID")
    public void the_customer_registration_response_contains_a_customer_id() {
        assertNotNull("Customer ID should not be null", lastRegistrationResponse.getCustomerId());
        assertFalse("Customer ID should not be empty", lastRegistrationResponse.getCustomerId().isEmpty());
    }

    @When("a {string} event is sent for customer {string} with requestId {string}")
    public void a_customer_deregistration_event_is_sent(String eventType, String customerId, String requestId) {
        CustomerDeregistrationRequest request = new CustomerDeregistrationRequest(requestId, customerId);
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastDeregistrationResponse = responseEvent.getArgument(0, CustomerDeregistrationResponse.class);
    }

    @When("the registered customer is deregistered with requestId {string}")
    public void the_registered_customer_is_deregistered(String requestId) {
        assertNotNull("Must have a registered customer first", lastRegistrationResponse);
        String customerId = lastRegistrationResponse.getCustomerId();
        CustomerDeregistrationRequest request = new CustomerDeregistrationRequest(requestId, customerId);
        Event requestEvent = new Event("CustomerDeregistrationRequested", new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastDeregistrationResponse = responseEvent.getArgument(0, CustomerDeregistrationResponse.class);
    }

    @Then("the customer deregistration is successful")
    public void the_customer_deregistration_is_successful() {
        assertNotNull("Customer deregistration response should exist", lastDeregistrationResponse);
        assertTrue("Customer deregistration should be successful", lastDeregistrationResponse.isSuccess());
    }

    @Then("the customer deregistration fails with error containing {string}")
    public void the_customer_deregistration_fails_with_error(String errorFragment) {
        assertNotNull("Customer deregistration response should exist", lastDeregistrationResponse);
        assertFalse("Customer deregistration should fail", lastDeregistrationResponse.isSuccess());
        assertTrue("Error message should contain: " + errorFragment,
                lastDeregistrationResponse.getErrorMessage().contains(errorFragment));
    }

    @When("a {string} event is sent to lookup customer {string} with requestId {string}")
    public void a_customer_lookup_event_is_sent(String eventType, String customerId, String requestId) {
        CustomerLookupRequest request = new CustomerLookupRequest(requestId, customerId);
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastLookupResponse = responseEvent.getArgument(0, CustomerLookupResponse.class);
    }

    @When("the registered customer is looked up with requestId {string}")
    public void the_registered_customer_is_looked_up(String requestId) {
        assertNotNull("Must have a registered customer first", lastRegistrationResponse);
        String customerId = lastRegistrationResponse.getCustomerId();
        CustomerLookupRequest request = new CustomerLookupRequest(requestId, customerId);
        Event requestEvent = new Event("CustomerLookupRequested", new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastLookupResponse = responseEvent.getArgument(0, CustomerLookupResponse.class);
    }

    @Then("the customer lookup is successful with bankAccount {string}")
    public void the_customer_lookup_is_successful_with_bank_account(String expectedBankAccount) {
        assertNotNull("Customer lookup response should exist", lastLookupResponse);
        assertTrue("Customer lookup should be successful", lastLookupResponse.isSuccess());
        assertEquals("Bank account should match", expectedBankAccount, lastLookupResponse.getBankAccountNumber());
    }

    @Then("the customer lookup fails with error containing {string}")
    public void the_customer_lookup_fails_with_error(String errorFragment) {
        assertNotNull("Customer lookup response should exist", lastLookupResponse);
        assertFalse("Customer lookup should fail", lastLookupResponse.isSuccess());
        assertTrue("Error message should contain: " + errorFragment,
                lastLookupResponse.getErrorMessage().contains(errorFragment));
    }

    @Given("a customer is registered with firstName {string}, lastName {string}, cpr {string}, bankAccount {string}")
    public void a_customer_is_registered(String firstName, String lastName, String cpr, String bankAccount) {
        a_customer_registration_event_is_sent("CustomerRegistrationRequested", firstName, lastName, cpr, bankAccount, "setup-request");
    }

    public String getLastRegisteredCustomerId() {
        return lastRegistrationResponse != null ? lastRegistrationResponse.getCustomerId() : null;
    }
}
