package dtu.pay.report;

import dtu.pay.report.dto.CustomerReport;
import dtu.pay.report.dto.CustomerReportRequest;
import dtu.pay.report.dto.ManagerReport;
import dtu.pay.report.dto.ManagerReportRequest;
import dtu.pay.report.dto.MerchantReport;
import dtu.pay.report.dto.MerchantReportRequest;
import dtu.pay.report.service.ReportService;
import messaging.Event;
import messaging.MessageQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportServiceMessageHandler {

    private final MessageQueue queue;
    private final ReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceMessageHandler.class);
    public ReportServiceMessageHandler(MessageQueue queue, ReportService reportService) {
        this.queue = queue;
        this.reportService = reportService;
        this.queue.addHandler("PaymentCompleted", this::handlePaymentCompleted);
        this.queue.addHandler("ManagerReportRequested", this::handleManagerReportRequested);
        this.queue.addHandler("MerchantReportRequested", this::handleMerchantReportRequested);
        this.queue.addHandler("CustomerReportRequested", this::handleCustomerReportRequested);
    }

    private void handlePaymentCompleted(Event event) {
        try {
            PaymentCompletedEvent payload = event.getArgument(0, PaymentCompletedEvent.class);
            logger.info("[ReportService] PaymentCompleted: paymentId=" + payload.getPaymentId() 
                + ", customerId=" + payload.getCustomerId() 
                + ", merchantId=" + payload.getMerchantId()
                + ", amount=" + payload.getAmount());
            reportService.onPaymentCompleted(payload);
            logger.info("[ReportService] Payment recorded successfully");
        } catch (Exception e) {
            logger.error("[ReportService] Error processing PaymentCompleted: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void handleManagerReportRequested(Event event) {
        try {
            ManagerReportRequest request = event.getArgument(0, ManagerReportRequest.class);
            logger.info("[ReportService] Request ID: " + request.getRequestId());
            ManagerReport report = reportService.handleManagerReportRequested(request);
            logger.info("[ReportService] Generated report with " + report.getPayments().size() + " payments");
            Event response = new Event("ManagerReportGenerated", new Object[]{report});
            queue.publish(response);
            logger.info("[ReportService] Published ManagerReportGenerated");
        } catch (Exception e) {
            logger.error("[ReportService] Error processing ManagerReportRequested: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void handleMerchantReportRequested(Event event) {
        MerchantReportRequest request = event.getArgument(0, MerchantReportRequest.class);
        MerchantReport report = reportService.handleMerchantReportRequested(request);
        Event response = new Event("MerchantReportGenerated", new Object[]{report});
        queue.publish(response);
    }

    private void handleCustomerReportRequested(Event event) {
        CustomerReportRequest request = event.getArgument(0, CustomerReportRequest.class);
        CustomerReport report = reportService.handleCustomerReportRequested(request);
        Event response = new Event("CustomerReportGenerated", new Object[]{report});
        queue.publish(response);
    }
}
