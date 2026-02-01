package dtu.pay.report;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import dtu.pay.report.dto.CustomerReport;
import dtu.pay.report.dto.CustomerReportRequest;
import messaging.Event;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CustomerReportSteps {

    private final ReportTestContext.InMemoryMessageQueue queue = ReportTestContext.getQueue();
    private CustomerReport lastCustomerReport;

    @When("the {string} event is sent for customer {string} with id {string}")
    public void the_customer_report_requested_event_is_sent_for_customer_with_id(String eventType,
                                                                                  String customerId,
                                                                                  String requestId) {
        CustomerReportRequest request = new CustomerReportRequest(requestId, customerId);
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastCustomerReport = responseEvent.getArgument(0, CustomerReport.class);
    }

    @Then("the customer report contains {int} payments")
    public void the_customer_report_contains_payments(Integer expectedCount) {
        assertNotNull("Customer report should have been generated", lastCustomerReport);
        assertEquals(expectedCount.intValue(), lastCustomerReport.getPayments().size());
    }

    @Then("the customer report contains a payment with amount {double}, merchant {string}, description {string}")
    public void the_customer_report_contains_a_payment_with_details(double amount,
                                                                    String merchantId,
                                                                    String description) {
        assertNotNull("Customer report should have been generated", lastCustomerReport);
        BigDecimal expectedAmount = BigDecimal.valueOf(amount);
        boolean found = lastCustomerReport.getPayments().stream().anyMatch(p ->
                p.getAmount().compareTo(expectedAmount) == 0 &&
                        p.getMerchantId().equals(merchantId) &&
                        p.getDescription().equals(description)
        );
        assertTrue("Expected payment not found in customer report", found);
    }
}
