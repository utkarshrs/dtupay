package dtu.pay.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import dtu.pay.account.dto.MerchantDeregistrationRequest;
import dtu.pay.account.dto.MerchantDeregistrationResponse;
import dtu.pay.account.dto.MerchantLookupRequest;
import dtu.pay.account.dto.MerchantLookupResponse;
import dtu.pay.account.dto.MerchantRegistrationRequest;
import dtu.pay.account.dto.MerchantRegistrationResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;

public class MerchantRegistrationSteps {

    private final AccountTestContext.InMemoryMessageQueue queue = AccountTestContext.getQueue();
    private MerchantRegistrationResponse lastRegistrationResponse;
    private MerchantDeregistrationResponse lastDeregistrationResponse;
    private MerchantLookupResponse lastLookupResponse;

    @When("a {string} event is sent with merchant firstName {string}, lastName {string}, cpr {string}, bankAccount {string}, requestId {string}")
    public void a_merchant_registration_event_is_sent(String eventType, String firstName, String lastName,
                                                       String cpr, String bankAccount, String requestId) {
        MerchantRegistrationRequest request = new MerchantRegistrationRequest(
                requestId, firstName, lastName, cpr, bankAccount
        );
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastRegistrationResponse = responseEvent.getArgument(0, MerchantRegistrationResponse.class);
    }

    @Then("the merchant registration is successful")
    public void the_merchant_registration_is_successful() {
        assertNotNull("Merchant registration response should exist", lastRegistrationResponse);
        assertTrue("Merchant registration should be successful", lastRegistrationResponse.isSuccess());
        assertNotNull("Merchant ID should be assigned", lastRegistrationResponse.getMerchantId());
    }

    @Then("the merchant registration response contains a merchant ID")
    public void the_merchant_registration_response_contains_a_merchant_id() {
        assertNotNull("Merchant ID should not be null", lastRegistrationResponse.getMerchantId());
        assertFalse("Merchant ID should not be empty", lastRegistrationResponse.getMerchantId().isEmpty());
    }

    @When("a {string} event is sent for merchant {string} with requestId {string}")
    public void a_merchant_deregistration_event_is_sent(String eventType, String merchantId, String requestId) {
        MerchantDeregistrationRequest request = new MerchantDeregistrationRequest(requestId, merchantId);
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastDeregistrationResponse = responseEvent.getArgument(0, MerchantDeregistrationResponse.class);
    }

    @When("the registered merchant is deregistered with requestId {string}")
    public void the_registered_merchant_is_deregistered(String requestId) {
        assertNotNull("Must have a registered merchant first", lastRegistrationResponse);
        String merchantId = lastRegistrationResponse.getMerchantId();
        MerchantDeregistrationRequest request = new MerchantDeregistrationRequest(requestId, merchantId);
        Event requestEvent = new Event("MerchantDeregistrationRequested", new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastDeregistrationResponse = responseEvent.getArgument(0, MerchantDeregistrationResponse.class);
    }

    @Then("the merchant deregistration is successful")
    public void the_merchant_deregistration_is_successful() {
        assertNotNull("Merchant deregistration response should exist", lastDeregistrationResponse);
        assertTrue("Merchant deregistration should be successful", lastDeregistrationResponse.isSuccess());
    }

    @Then("the merchant deregistration fails with error containing {string}")
    public void the_merchant_deregistration_fails_with_error(String errorFragment) {
        assertNotNull("Merchant deregistration response should exist", lastDeregistrationResponse);
        assertFalse("Merchant deregistration should fail", lastDeregistrationResponse.isSuccess());
        assertTrue("Error message should contain: " + errorFragment,
                lastDeregistrationResponse.getErrorMessage().contains(errorFragment));
    }

    @When("a {string} event is sent to lookup merchant {string} with requestId {string}")
    public void a_merchant_lookup_event_is_sent(String eventType, String merchantId, String requestId) {
        MerchantLookupRequest request = new MerchantLookupRequest(requestId, merchantId);
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastLookupResponse = responseEvent.getArgument(0, MerchantLookupResponse.class);
    }

    @When("the registered merchant is looked up with requestId {string}")
    public void the_registered_merchant_is_looked_up(String requestId) {
        assertNotNull("Must have a registered merchant first", lastRegistrationResponse);
        String merchantId = lastRegistrationResponse.getMerchantId();
        MerchantLookupRequest request = new MerchantLookupRequest(requestId, merchantId);
        Event requestEvent = new Event("MerchantLookupRequested", new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastLookupResponse = responseEvent.getArgument(0, MerchantLookupResponse.class);
    }

    @Then("the merchant lookup is successful with bankAccount {string}")
    public void the_merchant_lookup_is_successful_with_bank_account(String expectedBankAccount) {
        assertNotNull("Merchant lookup response should exist", lastLookupResponse);
        assertTrue("Merchant lookup should be successful", lastLookupResponse.isSuccess());
        assertEquals("Bank account should match", expectedBankAccount, lastLookupResponse.getBankAccountNumber());
    }

    @Then("the merchant lookup fails with error containing {string}")
    public void the_merchant_lookup_fails_with_error(String errorFragment) {
        assertNotNull("Merchant lookup response should exist", lastLookupResponse);
        assertFalse("Merchant lookup should fail", lastLookupResponse.isSuccess());
        assertTrue("Error message should contain: " + errorFragment,
                lastLookupResponse.getErrorMessage().contains(errorFragment));
    }

    @Given("a merchant is registered with firstName {string}, lastName {string}, cpr {string}, bankAccount {string}")
    public void a_merchant_is_registered(String firstName, String lastName, String cpr, String bankAccount) {
        a_merchant_registration_event_is_sent("MerchantRegistrationRequested", firstName, lastName, cpr, bankAccount, "setup-request");
    }

    public String getLastRegisteredMerchantId() {
        return lastRegistrationResponse != null ? lastRegistrationResponse.getMerchantId() : null;
    }
}
