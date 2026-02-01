package dtu.pay.facade.service;

import java.util.List;

import dtu.pay.facade.config.CorrelationManager;
import dtu.pay.facade.dto.api.request.CustomerRegistrationRequest;
import dtu.pay.facade.dto.api.response.CustomerReport;
import dtu.pay.facade.dto.api.response.CustomerResponse;
import dtu.pay.facade.dto.api.response.PaymentInfo;
import dtu.pay.facade.dto.api.request.TokenRequest;
import dtu.pay.facade.dto.api.response.TokenResponse;
import dtu.pay.facade.dto.messaging.events.CustomerRegistrationRequestEvent;
import dtu.pay.facade.dto.messaging.events.CustomerDeregistrationRequestEvent;
import dtu.pay.facade.dto.messaging.events.TokenGenerationRequestEvent;
import dtu.pay.facade.dto.messaging.events.CustomerReportRequestEvent;
import dtu.pay.facade.dto.messaging.responses.CustomerRegistrationResponse;
import dtu.pay.facade.dto.messaging.responses.CustomerDeregistrationResponse;
import dtu.pay.facade.dto.messaging.responses.TokenGenerationResponse;
import dtu.pay.facade.dto.messaging.responses.CustomerReportResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import messaging.Event;

@ApplicationScoped
public class CustomerFacadeService {

    @Inject
    CorrelationManager correlationManager;

    public CustomerResponse registerCustomer(CustomerRegistrationRequest registration) {
        String requestId = correlationManager.generateCorrelationId();

        CustomerRegistrationRequestEvent request = new CustomerRegistrationRequestEvent(
            requestId,
            registration.getFirstName(),
            registration.getLastName(),
            registration.getCpr(),
            registration.getBankAccountNumber()
        );

        Event event = new Event("CustomerRegistrationRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            if ("CustomerRegistered".equals(response.getType())) {
                // Backend sends CustomerRegistrationResponse
                var responseData = response.getArgument(0, CustomerRegistrationResponse.class);
                return CustomerResponse.success(responseData.getCustomerId());
            } else {
                // CustomerRegistrationFailed
                var responseData = response.getArgument(0, CustomerRegistrationResponse.class);
                return CustomerResponse.failure(responseData.getErrorMessage());
            }
        } catch (Exception e) {
            return CustomerResponse.failure("Request timed out or failed: " + e.getMessage());
        }
    }

    public CustomerResponse deregisterCustomer(String customerId) {
        String requestId = correlationManager.generateCorrelationId();

        CustomerDeregistrationRequestEvent request = new CustomerDeregistrationRequestEvent(
            requestId,
            customerId
        );

        Event event = new Event("CustomerDeregistrationRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            if ("CustomerDeregistered".equals(response.getType())) {
                return CustomerResponse.success(customerId);
            } else {
                // CustomerDeregistrationFailed
                var responseData = response.getArgument(0, CustomerDeregistrationResponse.class);
                return CustomerResponse.failure(responseData.getErrorMessage());
            }
        } catch (Exception e) {
            return CustomerResponse.failure("Request timed out or failed: " + e.getMessage());
        }
    }

    public TokenResponse requestTokens(String customerId, TokenRequest tokenRequest) {
        String requestId = correlationManager.generateCorrelationId();

        TokenGenerationRequestEvent request = new TokenGenerationRequestEvent(
            requestId,
            customerId,
            tokenRequest.getTokenCount()
        );

        Event event = new Event("TokensRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            if ("TokenGenerationSucceeded".equals(response.getType())) {
                // Backend sends TokenGenerationResponse
                var responseData = response.getArgument(0, TokenGenerationResponse.class);
                List<String> tokenIds = responseData.getTokens().stream()
                    .map(TokenGenerationResponse.TokenDTO::getValue)
                    .toList();
                return TokenResponse.success(tokenIds);
            } else {
                // TokenGenerationDenied
                var responseData = response.getArgument(0, TokenGenerationResponse.class);
                return TokenResponse.failure(responseData.getErrorMessage());
            }
        } catch (Exception e) {
            return TokenResponse.failure("Request timed out or failed: " + e.getMessage());
        }
    }

    public CustomerReport getReport(String customerId) {
        String requestId = correlationManager.generateCorrelationId();

        CustomerReportRequestEvent request = new CustomerReportRequestEvent(
            requestId,
            customerId
        );

        Event event = new Event("CustomerReportRequested", new Object[]{request});

        try {
            Event response = correlationManager.publishAndWait(event, requestId);
            
            // Backend sends CustomerReportResponse
            var responseData = response.getArgument(0, CustomerReportResponse.class);
            
            List<PaymentInfo> payments = responseData.getPayments().stream()
                .map(view -> {
                    PaymentInfo info = new PaymentInfo();
                    info.setAmount(view.getAmount());
                    info.setMerchantId(view.getMerchantId());
                    info.setDescription(view.getDescription());
                    info.setToken(view.getToken());
                    return info;
                })
                .toList();
            
            return CustomerReport.success(payments);
        } catch (Exception e) {
            return CustomerReport.failure("Request timed out or failed: " + e.getMessage());
        }
    }
}
