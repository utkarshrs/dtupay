package dtu.pay.report;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import dtu.pay.report.dto.ManagerReport;
import dtu.pay.report.dto.ManagerReportRequest;
import dtu.pay.report.dto.MerchantReport;
import dtu.pay.report.dto.MerchantReportRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;



public class ManagerReportSteps {

    private final ReportTestContext.InMemoryMessageQueue queue = ReportTestContext.getQueue();
    private ManagerReport lastReport;
    private MerchantReport lastMerchantReport;

    @When("the {string} event is sent for token {string}, customer {string}, merchant {string}, amount {double}, description {string}, timestamp {string}")
    public void the_payment_completed_event_is_sent(String eventType,
                                                    String token,
                                                    String customerId,
                                                    String merchantId,
                                                    double amount,
                                                    String description,
                                                    String timestamp) {
        String paymentId = "payment-" + Instant.now().toEpochMilli();
        PaymentCompletedEvent payload = new PaymentCompletedEvent(
                paymentId,
                token,
                customerId,
                merchantId,
                BigDecimal.valueOf(amount),
                description,
                timestamp
        );
        Event event = new Event(eventType, new Object[]{payload});
        queue.publish(event);
    }

    @When("the {string} event is sent with id {string}")
    public void the_manager_report_requested_event_is_sent_with_id(String eventType, String requestId) {
        ManagerReportRequest request = new ManagerReportRequest(requestId);
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastReport = responseEvent.getArgument(0, ManagerReport.class);
    }

    @When("the {string} event is sent for merchant {string} with id {string}")
    public void the_merchant_report_requested_event_is_sent_for_merchant_with_id(String eventType,
                                                                                String merchantId,
                                                                                String requestId) {
        MerchantReportRequest request = new MerchantReportRequest(requestId, merchantId);
        Event requestEvent = new Event(eventType, new Object[]{request});
        queue.publish(requestEvent);
        Event responseEvent = queue.getLastPublishedEvent();
        lastMerchantReport = responseEvent.getArgument(0, MerchantReport.class);
    }

    @Then("the manager report contains {int} payments")
    public void the_manager_report_contains_payments(Integer expectedCount) {
        assertNotNull("Manager report should have been generated", lastReport);
        assertEquals(expectedCount.intValue(), lastReport.getPayments().size());

        System.out.println("--- Manager report payments ---");
        lastReport.getPayments().forEach(p -> System.out.println(
                "token=" + p.getToken()
                        + ", customerId=" + p.getCustomerId()
                        + ", merchantId=" + p.getMerchantId()
                        + ", amount=" + p.getAmount()
                        + ", description=" + p.getDescription()
                        + ", timestamp=" + p.getTimestamp()
        ));
        System.out.println("-------------------------------");
    }

    @Then("the merchant report contains {int} payments")
    public void the_merchant_report_contains_payments(Integer expectedCount) {
        assertNotNull("Merchant report should have been generated", lastMerchantReport);
        assertEquals(expectedCount.intValue(), lastMerchantReport.getPayments().size());
    }

    @Then("a {string} event was published")
    public void an_event_was_published(String expectedType) {
        Event last = queue.getLastPublishedEvent();
        assertNotNull("No event was published", last);
        assertEquals(expectedType, last.getType());
    }
}
