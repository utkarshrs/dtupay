package dtu.pay.facade.service;

import java.math.BigDecimal;
import java.util.List;

import dtu.pay.facade.config.CorrelationManager;
import dtu.pay.facade.dto.api.response.ManagerReport;
import dtu.pay.facade.dto.api.response.PaymentInfo;
import dtu.pay.facade.dto.messaging.events.ManagerReportRequestEvent;
import dtu.pay.facade.dto.messaging.responses.ManagerReportResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import messaging.Event;

@ApplicationScoped
public class ManagerFacadeService {

    @Inject
    CorrelationManager correlationManager;

    public ManagerReport getReport() {
        String requestId = correlationManager.generateCorrelationId();

        ManagerReportRequestEvent request = new ManagerReportRequestEvent(requestId);

        Event event = new Event("ManagerReportRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            // Backend sends ManagerReportResponse
            var responseData = response.getArgument(0, ManagerReportResponse.class);
            
            List<PaymentInfo> payments = responseData.getPayments().stream()
                .map(record -> {
                    PaymentInfo info = new PaymentInfo();
                    info.setToken(record.getToken());
                    info.setCustomerId(record.getCustomerId());
                    info.setMerchantId(record.getMerchantId());
                    info.setAmount(record.getAmount());
                    info.setDescription(record.getDescription());
                    return info;
                })
                .toList();
            
            // Calculate total amount
            BigDecimal totalAmount = payments.stream()
                .map(PaymentInfo::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            return ManagerReport.success(payments, totalAmount);
        } catch (Exception e) {
            return ManagerReport.failure("Request timed out or failed: " + e.getMessage());
        }
    }
}
