package dtu.pay.payment.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import dtu.pay.payment.PaymentCompletedEvent;
import dtu.pay.payment.PaymentFailedEvent;
import dtu.pay.payment.domain.BankTransferPort;
import dtu.pay.payment.dto.CustomerBankAccountResolved;
import dtu.pay.payment.dto.MerchantBankAccountResolved;
import dtu.pay.payment.dto.PaymentInitiatedEvent;
import dtu.pay.payment.dto.TokenValidationFailedEvent;

public class PaymentService {

    private static final String ERROR_INSUFFICIENT_BALANCE = "Bank transfer failed: Insufficient balance";
    private static final String ERROR_INVALID_TOKEN = "Invalid Token: Token not found or already used";
    private static final String ERROR_BANK_TRANSFER = "Bank transfer failed";

    private final BankTransferPort bankTransferPort;
    private final Map<String, PaymentInitiatedEvent> pendingPayments = new HashMap<>();
    private final Map<String, CustomerBankAccountResolved> customerAccounts = new HashMap<>();
    private final Map<String, MerchantBankAccountResolved> merchantAccounts = new HashMap<>();

    public PaymentService(BankTransferPort bankTransferPort) {
        this.bankTransferPort = bankTransferPort;
    }

    public PaymentCompletedEvent registerPaymentInitiated(PaymentInitiatedEvent request) {
        pendingPayments.put(request.getPaymentId(), request);
        return tryComplete(request.getPaymentId());
    }

    public PaymentCompletedEvent handleCustomerBankAccountResolved(CustomerBankAccountResolved event) {
        customerAccounts.put(event.getPaymentId(), event);
        return tryComplete(event.getPaymentId());
    }

    public PaymentCompletedEvent handleMerchantBankAccountResolved(MerchantBankAccountResolved event) {
        merchantAccounts.put(event.getPaymentId(), event);
        return tryComplete(event.getPaymentId());
    }

    private PaymentCompletedEvent tryComplete(String paymentId) {
        PaymentInitiatedEvent request = pendingPayments.get(paymentId);
        CustomerBankAccountResolved customer = customerAccounts.get(paymentId);
        MerchantBankAccountResolved merchant = merchantAccounts.get(paymentId);

        if (request == null || customer == null || merchant == null) {
            return null;
        }

        boolean transferred = bankTransferPort.transfer(
                customer.getFromAccountId(),
                merchant.getToAccountId(),
                request.getAmount(),
                "DTU Pay payment"
        );

        if (!transferred) {
            // Return null on failure - failure will be handled by caller
            return null;
        }

        return new PaymentCompletedEvent(
                paymentId,
                request.getToken(),
                customer.getCustomerId(),
                merchant.getMerchantId(),
                request.getAmount(),
                request.getDescription(),
                request.getTimestamp()
        );
    }

    private PaymentFailedEvent createFailureEvent(String paymentId, String errorMessage) {
        String timestamp = Instant.now().toString();
        return new PaymentFailedEvent(paymentId, errorMessage, timestamp);
    }

    public PaymentFailedEvent handleTokenValidationFailed(TokenValidationFailedEvent event) {
        return createFailureEvent(event.getPaymentId(), ERROR_INVALID_TOKEN);
    }

    public PaymentFailedEvent checkForBankTransferFailure(String paymentId) {
        PaymentInitiatedEvent request = pendingPayments.get(paymentId);
        CustomerBankAccountResolved customer = customerAccounts.get(paymentId);
        MerchantBankAccountResolved merchant = merchantAccounts.get(paymentId);

        // If all prerequisites are met, it means bank transfer failed
        if (request != null && customer != null && merchant != null) {
            return createFailureEvent(paymentId, ERROR_INSUFFICIENT_BALANCE);
        }
        
        return null;
    }
}
