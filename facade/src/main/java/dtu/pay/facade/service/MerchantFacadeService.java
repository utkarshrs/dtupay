package dtu.pay.facade.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import dtu.pay.facade.config.CorrelationManager;
import dtu.pay.facade.dto.api.request.MerchantRegistrationRequest;
import dtu.pay.facade.dto.api.response.MerchantReport;
import dtu.pay.facade.dto.api.response.MerchantResponse;
import dtu.pay.facade.dto.api.response.PaymentInfo;
import dtu.pay.facade.dto.api.request.PaymentRequest;
import dtu.pay.facade.dto.api.response.PaymentResponse;
import dtu.pay.facade.dto.messaging.events.MerchantRegistrationRequestEvent;
import dtu.pay.facade.dto.messaging.events.MerchantDeregistrationRequestEvent;
import dtu.pay.facade.dto.messaging.events.PaymentInitiatedEvent;
import dtu.pay.facade.dto.messaging.events.MerchantReportRequestEvent;
import dtu.pay.facade.dto.messaging.responses.MerchantRegistrationResponse;
import dtu.pay.facade.dto.messaging.responses.MerchantDeregistrationResponse;
import dtu.pay.facade.dto.messaging.responses.PaymentCompletedEvent;
import dtu.pay.facade.dto.messaging.responses.PaymentFailedEvent;
import dtu.pay.facade.dto.messaging.responses.TokenValidationResponse;
import dtu.pay.facade.dto.messaging.responses.MerchantReportResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import messaging.Event;

@ApplicationScoped
public class MerchantFacadeService {

    @Inject
    CorrelationManager correlationManager;

    public MerchantResponse registerMerchant(MerchantRegistrationRequest registration) {
        String requestId = correlationManager.generateCorrelationId();

        MerchantRegistrationRequestEvent request = new MerchantRegistrationRequestEvent(
            requestId,
            registration.getFirstName(),
            registration.getLastName(),
            registration.getCpr(),
            registration.getBankAccountNumber()
        );

        Event event = new Event("MerchantRegistrationRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            if ("MerchantRegistered".equals(response.getType())) {
                // Backend sends MerchantRegistrationResponse
                var responseData = response.getArgument(0, MerchantRegistrationResponse.class);
                return MerchantResponse.success(responseData.getMerchantId());
            } else {
                // MerchantRegistrationFailed
                var responseData = response.getArgument(0, MerchantRegistrationResponse.class);
                return MerchantResponse.failure(responseData.getErrorMessage());
            }
        } catch (Exception e) {
            return MerchantResponse.failure("Request timed out or failed: " + e.getMessage());
        }
    }

    public MerchantResponse deregisterMerchant(String merchantId) {
        String requestId = correlationManager.generateCorrelationId();

        MerchantDeregistrationRequestEvent request = new MerchantDeregistrationRequestEvent(
            requestId,
            merchantId
        );

        Event event = new Event("MerchantDeregistrationRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            if ("MerchantDeregistered".equals(response.getType())) {
                return MerchantResponse.success(merchantId);
            } else {
                // MerchantDeregistrationFailed
                var responseData = response.getArgument(0, MerchantDeregistrationResponse.class);
                return MerchantResponse.failure(responseData.getErrorMessage());
            }
        } catch (Exception e) {
            return MerchantResponse.failure("Request timed out or failed: " + e.getMessage());
        }
    }

    public PaymentResponse initiatePayment(String merchantId, PaymentRequest paymentRequest) {
        String paymentId = correlationManager.generateCorrelationId();

        String token = paymentRequest.getToken() != null ? paymentRequest.getToken() : "";
        BigDecimal amount = paymentRequest.getAmount() != null ? paymentRequest.getAmount() : BigDecimal.ZERO;
        String description = paymentRequest.getDescription() != null ? paymentRequest.getDescription() : "";

        PaymentInitiatedEvent request = new PaymentInitiatedEvent(
            merchantId,
            token,
            amount,
            paymentId,
            description,
            Instant.now().toString()
        );

        Event event = new Event("PaymentInitiated", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, paymentId);
            
            if ("PaymentCompleted".equals(response.getType())) {
                // Backend sends PaymentCompletedEvent
                var responseData = response.getArgument(0, PaymentCompletedEvent.class);
                return PaymentResponse.success(paymentId, responseData.getAmount());
            } else if ("PaymentFailed".equals(response.getType())) {
                // Backend sends PaymentFailedEvent
                var responseData = response.getArgument(0, PaymentFailedEvent.class);
                return PaymentResponse.failure(responseData.getErrorMessage());
            } else if ("TokenValidationFailed".equals(response.getType())) {
                // TokenValidationFailed
                var responseData = response.getArgument(0, TokenValidationResponse.class);
                return PaymentResponse.failure(responseData.getErrorMessage());
            } else {
                // Unknown response type
                return PaymentResponse.failure("Unknown response type: " + response.getType());
            }
        } catch (Exception e) {
            return PaymentResponse.failure("Request timed out or failed: " + e.getMessage());
        }
    }

    public MerchantReport getReport(String merchantId) {
        String requestId = correlationManager.generateCorrelationId();

        MerchantReportRequestEvent request = new MerchantReportRequestEvent(
            requestId,
            merchantId
        );

        Event event = new Event("MerchantReportRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            // Backend sends MerchantReportResponse
            var responseData = response.getArgument(0, MerchantReportResponse.class);
            
            List<PaymentInfo> payments = responseData.getPayments().stream()
                .map(view -> {
                    PaymentInfo info = new PaymentInfo();
                    info.setToken(view.getToken());
                    info.setAmount(view.getAmount());
                    info.setDescription(view.getDescription());
                    return info;
                })
                .toList();
            
            return MerchantReport.success(payments);
        } catch (Exception e) {
            return MerchantReport.failure("Request timed out or failed: " + e.getMessage());
        }
    }
}
