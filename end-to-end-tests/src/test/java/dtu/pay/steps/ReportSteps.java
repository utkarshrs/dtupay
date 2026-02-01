package dtu.pay.steps;

import dtu.pay.dto.*;
import dtu.pay.service.FacadeClient;
import io.cucumber.java.en.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ReportSteps {

    private final FacadeClient client = SharedSteps.getClient();

    // Report state
    private CustomerReport customerReport;
    private MerchantReport merchantReport;
    private ManagerReport managerReport;

    @Given("the customer has made a successful payment of {int} kr to the merchant")
    public void theCustomerHasMadePayment(int amount) {
        makePayment(SharedSteps.customerId, SharedSteps.merchantId, amount, "Test payment");
    }

    @Given("the customer has made a successful payment of {int} kr to the merchant with description {string}")
    public void theCustomerHasMadePaymentWithDescription(int amount, String description) {
        makePayment(SharedSteps.customerId, SharedSteps.merchantId, amount, description);
    }

    @Given("customer {string} {string} has made a successful payment of {int} kr to merchant {string} {string} with description {string}")
    public void namedCustomerHasMadePaymentToNamedMerchant(String firstName, String lastName, int amount, 
                                                           String merchantFirstName, String merchantLastName, String description) {
        String customerId = SharedSteps.getNamedCustomerId(firstName, lastName);
        String merchantId = SharedSteps.getNamedMerchantId(merchantFirstName, merchantLastName);
        
        assertNotNull(customerId, "Customer " + firstName + " " + lastName + " should be registered");
        assertNotNull(merchantId, "Merchant " + merchantFirstName + " " + merchantLastName + " should be registered");
        
        makePayment(customerId, merchantId, amount, description);
    }

    private void makePayment(String customerId, String merchantId, int amount, String description) {
        // Get token for customer
        TokenResponse tokenResponse = client.requestTokens(customerId, 1);
        assertTrue(tokenResponse.isSuccess(), "Token request should succeed");
        String token = tokenResponse.getTokens().get(0);
        
        // Make payment
        PaymentRequest request = new PaymentRequest(
                token,
                BigDecimal.valueOf(amount),
                description
        );
        PaymentResponse paymentResponse = client.initiatePayment(merchantId, request);
        assertTrue(paymentResponse.isSuccess(), "Payment should succeed: " + paymentResponse.getErrorMessage());
    }

    @When("the customer requests their payment report")
    public void theCustomerRequestsTheirReport() {
        customerReport = client.getCustomerReport(SharedSteps.customerId);
    }

    @When("the merchant requests their payment report")
    public void theMerchantRequestsTheirReport() {
        merchantReport = client.getMerchantReport(SharedSteps.merchantId);
    }

    @When("the manager requests the overall report")
    public void theManagerRequestsOverallReport() {
        managerReport = client.getManagerReport();
    }

    // Customer report assertions
    @Then("the customer report contains {int} payment(s)")
    public void theCustomerReportContainsPayments(int expectedCount) {
        assertTrue(customerReport.isSuccess(), "Customer report should be successful");
        assertNotNull(customerReport.getPayments(), "Payments list should not be null");
        assertEquals(expectedCount, customerReport.getPayments().size(),
                "Report should contain " + expectedCount + " payment(s)");
    }

    @Then("the customer report shows payment with amount {int} kr, description {string}")
    public void theCustomerReportShowsPaymentWithDetails(int amount, String description) {
        boolean found = customerReport.getPayments().stream()
                .anyMatch(p -> p.getAmount().compareTo(BigDecimal.valueOf(amount)) == 0 
                        && description.equals(p.getDescription()));
        assertTrue(found, "Customer report should contain payment with amount " + amount + " kr and description '" + description + "'");
    }

    @Then("the customer report includes merchant ID for each payment")
    public void theCustomerReportIncludesMerchantId() {
        assertFalse(customerReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : customerReport.getPayments()) {
            assertNotNull(payment.getMerchantId(), "Customer report should include merchant ID");
            assertFalse(payment.getMerchantId().isEmpty(), "Merchant ID should not be empty");
        }
    }

    @Then("the customer report includes token for each payment")
    public void theCustomerReportIncludesToken() {
        assertFalse(customerReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : customerReport.getPayments()) {
            assertNotNull(payment.getToken(), "Customer report should include token");
            assertFalse(payment.getToken().isEmpty(), "Token should not be empty");
        }
    }

    @Then("the customer report does not include customer ID")
    public void theCustomerReportDoesNotIncludeCustomerId() {
        // Customer ID should be null in customer reports (they already know their own ID)
        for (PaymentInfo payment : customerReport.getPayments()) {
            assertNull(payment.getCustomerId(), "Customer report should not include customer ID");
        }
    }

    // Merchant report assertions
    @Then("the merchant report contains {int} payment(s)")
    public void theMerchantReportContainsPayments(int expectedCount) {
        assertTrue(merchantReport.isSuccess(), "Merchant report should be successful");
        assertNotNull(merchantReport.getPayments(), "Payments list should not be null");
        assertEquals(expectedCount, merchantReport.getPayments().size(),
                "Report should contain " + expectedCount + " payment(s)");
    }

    @Then("the merchant report shows payment with amount {int} kr, description {string}")
    public void theMerchantReportShowsPaymentWithDetails(int amount, String description) {
        boolean found = merchantReport.getPayments().stream()
                .anyMatch(p -> p.getAmount().compareTo(BigDecimal.valueOf(amount)) == 0 
                        && description.equals(p.getDescription()));
        assertTrue(found, "Merchant report should contain payment with amount " + amount + " kr and description '" + description + "'");
    }

    @Then("the merchant report includes token for each payment")
    public void theMerchantReportIncludesToken() {
        assertFalse(merchantReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : merchantReport.getPayments()) {
            assertNotNull(payment.getToken(), "Merchant report should include token");
            assertFalse(payment.getToken().isEmpty(), "Token should not be empty");
        }
    }

    @Then("the merchant report does not include customer ID")
    public void theMerchantReportDoesNotIncludeCustomerId() {
        // Merchant should not see customer IDs (privacy)
        for (PaymentInfo payment : merchantReport.getPayments()) {
            assertNull(payment.getCustomerId(), "Merchant report should not include customer ID");
        }
    }

    @Then("the merchant report does not include merchant ID")
    public void theMerchantReportDoesNotIncludeMerchantId() {
        // Merchant ID should be null in merchant reports (they already know their own ID)
        for (PaymentInfo payment : merchantReport.getPayments()) {
            assertNull(payment.getMerchantId(), "Merchant report should not include merchant ID");
        }
    }

    // Manager report assertions
    @Then("the manager report contains at least {int} payment(s)")
    public void theManagerReportContainsAtLeastPayments(int minCount) {
        assertTrue(managerReport.isSuccess(), "Manager report should be successful");
        assertNotNull(managerReport.getPayments(), "Payments list should not be null");
        assertTrue(managerReport.getPayments().size() >= minCount,
                "Report should contain at least " + minCount + " payment(s), but found " + managerReport.getPayments().size());
    }

    @Then("the manager report includes customer ID for each payment")
    public void theManagerReportIncludesCustomerId() {
        assertFalse(managerReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : managerReport.getPayments()) {
            assertNotNull(payment.getCustomerId(), "Manager report should include customer ID");
            assertFalse(payment.getCustomerId().isEmpty(), "Customer ID should not be empty");
        }
    }

    @Then("the manager report includes merchant ID for each payment")
    public void theManagerReportIncludesMerchantId() {
        assertFalse(managerReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : managerReport.getPayments()) {
            assertNotNull(payment.getMerchantId(), "Manager report should include merchant ID");
            assertFalse(payment.getMerchantId().isEmpty(), "Merchant ID should not be empty");
        }
    }

    @Then("the manager report includes token for each payment")
    public void theManagerReportIncludesToken() {
        assertFalse(managerReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : managerReport.getPayments()) {
            assertNotNull(payment.getToken(), "Manager report should include token");
            assertFalse(payment.getToken().isEmpty(), "Token should not be empty");
        }
    }

    @Then("the manager report includes amount for each payment")
    public void theManagerReportIncludesAmount() {
        assertFalse(managerReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : managerReport.getPayments()) {
            assertNotNull(payment.getAmount(), "Manager report should include amount");
            assertTrue(payment.getAmount().compareTo(BigDecimal.ZERO) > 0, "Amount should be positive");
        }
    }

    @Then("the manager report includes description for each payment")
    public void theManagerReportIncludesDescription() {
        assertFalse(managerReport.getPayments().isEmpty(), "Should have payments");
        for (PaymentInfo payment : managerReport.getPayments()) {
            assertNotNull(payment.getDescription(), "Manager report should include description field (can be empty)");
        }
    }

    // Legacy step definitions for backward compatibility
    @Then("the report contains {int} payment")
    public void theReportContainsPayments(int expectedCount) {
        theCustomerReportContainsPayments(expectedCount);
    }

    @Then("the payment shows amount {int} kr")
    public void thePaymentShowsAmount(int expectedAmount) {
        assertFalse(customerReport.getPayments().isEmpty(), "Should have at least one payment");
        PaymentInfo payment = customerReport.getPayments().get(0);
        assertEquals(0, BigDecimal.valueOf(expectedAmount).compareTo(payment.getAmount()),
                "Payment amount should be " + expectedAmount + " kr");
    }

    @Then("the merchant report contains {int} payment")
    public void theMerchantReportContainsSinglePayment(int expectedCount) {
        theMerchantReportContainsPayments(expectedCount);
    }

    @Then("the merchant payment shows amount {int} kr")
    public void theMerchantPaymentShowsAmount(int expectedAmount) {
        assertFalse(merchantReport.getPayments().isEmpty(), "Should have at least one payment");
        PaymentInfo payment = merchantReport.getPayments().get(0);
        assertEquals(0, BigDecimal.valueOf(expectedAmount).compareTo(payment.getAmount()),
                "Payment amount should be " + expectedAmount + " kr");
    }

    @Then("the manager report contains at least {int} payment")
    public void theManagerReportContainsAtLeastSinglePayment(int minCount) {
        theManagerReportContainsAtLeastPayments(minCount);
    }
}
