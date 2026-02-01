package dtu.pay.account;

import dtu.pay.account.dto.CustomerBankAccountResolved;
import dtu.pay.account.dto.CustomerDeregistrationRequest;
import dtu.pay.account.dto.CustomerDeregistrationResponse;
import dtu.pay.account.dto.CustomerLookupRequest;
import dtu.pay.account.dto.CustomerLookupResponse;
import dtu.pay.account.dto.CustomerRegistrationRequest;
import dtu.pay.account.dto.CustomerRegistrationResponse;
import dtu.pay.account.dto.MerchantBankAccountResolved;
import dtu.pay.account.dto.MerchantDeregistrationRequest;
import dtu.pay.account.dto.MerchantDeregistrationResponse;
import dtu.pay.account.dto.MerchantLookupRequest;
import dtu.pay.account.dto.MerchantLookupResponse;
import dtu.pay.account.dto.MerchantRegistrationRequest;
import dtu.pay.account.dto.MerchantRegistrationResponse;
import dtu.pay.account.dto.PaymentInitiatedEvent;
import dtu.pay.account.dto.TokenValidatedEvent;
import dtu.pay.account.service.AccountService;
import messaging.Event;
import messaging.MessageQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountServiceMessageHandler {

    private final MessageQueue queue;
    private final AccountService accountService;

    private static final Logger logger =
            LoggerFactory.getLogger(AccountServiceMessageHandler.class);

    public AccountServiceMessageHandler(MessageQueue queue, AccountService accountService) {
        this.queue = queue;
        this.accountService = accountService;
        
        // Customer event handlers
        this.queue.addHandler("CustomerRegistrationRequested", this::handleCustomerRegistrationRequested);
        this.queue.addHandler("CustomerDeregistrationRequested", this::handleCustomerDeregistrationRequested);
        this.queue.addHandler("CustomerLookupRequested", this::handleCustomerLookupRequested);
        
        // Merchant event handlers
        this.queue.addHandler("MerchantRegistrationRequested", this::handleMerchantRegistrationRequested);
        this.queue.addHandler("MerchantDeregistrationRequested", this::handleMerchantDeregistrationRequested);
        this.queue.addHandler("MerchantLookupRequested", this::handleMerchantLookupRequested);

        // Payment flow event handlers
        this.queue.addHandler("TokenValidated", this::handleTokenValidatedForPayment);
        this.queue.addHandler("PaymentInitiated", this::handlePaymentInitiatedForPayment);

    }

    private void handleCustomerRegistrationRequested(Event event) {
        CustomerRegistrationRequest request = event.getArgument(0, CustomerRegistrationRequest.class);

        CustomerRegistrationResponse response = accountService.registerCustomer(request);
        
        if (response.isSuccess()) {
            Event responseEvent = new Event("CustomerRegistered", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing CustomerRegistered for requestId={}",
                        response.getRequestId());

            queue.publish(responseEvent);
        } else {
            Event responseEvent = new Event("CustomerRegistrationFailed", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing CustomerRegistrationFailed for requestId={}, error={}",
                        response.getRequestId(), response.getErrorMessage());
            queue.publish(responseEvent);
        }
    }

    private void handleCustomerDeregistrationRequested(Event event) {
        CustomerDeregistrationRequest request = event.getArgument(0, CustomerDeregistrationRequest.class);
        CustomerDeregistrationResponse response = accountService.deregisterCustomer(request);
        
        if (response.isSuccess()) {
            Event responseEvent = new Event("CustomerDeregistered", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing CustomerDeregistered for requestId={}",
                        response.getRequestId());
            queue.publish(responseEvent);
        } else {
            Event responseEvent = new Event("CustomerDeregistrationFailed", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing CustomerDeregistrationFailed for requestId={}, error={}",
                        response.getRequestId(), response.getErrorMessage());
            queue.publish(responseEvent);
        }
    }

    private void handleCustomerLookupRequested(Event event) {
        CustomerLookupRequest request = event.getArgument(0, CustomerLookupRequest.class);
        CustomerLookupResponse response = accountService.lookupCustomer(request);
        
        if (response.isSuccess()) {
            Event responseEvent = new Event("CustomerLookupCompleted", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing CustomerLookupCompleted for requestId={}",
                        response.getRequestId());
            queue.publish(responseEvent);
        } else {
            Event responseEvent = new Event("CustomerLookupFailed", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing CustomerLookupFailed for requestId={}, error={}",
                        response.getRequestId(), response.getErrorMessage());
            queue.publish(responseEvent);
        }
    }

    private void handleMerchantRegistrationRequested(Event event) {
        MerchantRegistrationRequest request = event.getArgument(0, MerchantRegistrationRequest.class);
        MerchantRegistrationResponse response = accountService.registerMerchant(request);
        
        if (response.isSuccess()) {
            Event responseEvent = new Event("MerchantRegistered", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing MerchantRegistered for requestId={}",
                        response.getRequestId());
            queue.publish(responseEvent);
        } else {
            Event responseEvent = new Event("MerchantRegistrationFailed", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing MerchantRegistrationFailed for requestId={}, error={}",
                        response.getRequestId(), response.getErrorMessage());
            queue.publish(responseEvent);
        }
    }

    private void handleMerchantDeregistrationRequested(Event event) {
        MerchantDeregistrationRequest request = event.getArgument(0, MerchantDeregistrationRequest.class);
        MerchantDeregistrationResponse response = accountService.deregisterMerchant(request);
        
        if (response.isSuccess()) {
            Event responseEvent = new Event("MerchantDeregistered", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing MerchantDeregistered for requestId={}",
                        response.getRequestId());
            queue.publish(responseEvent);
        } else {
            Event responseEvent = new Event("MerchantDeregistrationFailed", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing MerchantDeregistrationFailed for requestId={}, error={}",
                        response.getRequestId(), response.getErrorMessage());
            queue.publish(responseEvent);
        }
    }

    private void handleMerchantLookupRequested(Event event) {
        MerchantLookupRequest request = event.getArgument(0, MerchantLookupRequest.class);
        MerchantLookupResponse response = accountService.lookupMerchant(request);
        
        if (response.isSuccess()) {
            Event responseEvent = new Event("MerchantLookupCompleted", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing MerchantLookupCompleted for requestId={}",
                        response.getRequestId());
            queue.publish(responseEvent);
        } else {
            Event responseEvent = new Event("MerchantLookupFailed", new Object[]{response});
            logger.info("[AccountService][AccountServiceMessageHandler] Publishing MerchantLookupFailed for requestId={}, error={}",
                        response.getRequestId(), response.getErrorMessage());
            queue.publish(responseEvent);
        }
    }

    private void handleTokenValidatedForPayment(Event event) {
        try {
            TokenValidatedEvent validated = event.getArgument(0, TokenValidatedEvent.class);
            String paymentId = validated.getPaymentId();
            String customerId = validated.getCustomerId();

            CustomerLookupRequest lookupRequest = new CustomerLookupRequest(
                    "payment-" + paymentId + "-customerLookup",
                    customerId
            );
            CustomerLookupResponse lookupResponse = accountService.lookupCustomer(lookupRequest);

            if (lookupResponse.isSuccess()) {
                CustomerBankAccountResolved resolved = new CustomerBankAccountResolved(
                        paymentId,
                        lookupResponse.getCustomerId(),
                        lookupResponse.getBankAccountNumber()
                );
                Event resolvedEvent = new Event("CustomerBankAccountResolved", new Object[]{resolved});
                logger.info("[AccountService][AccountServiceMessageHandler] Publishing CustomerBankAccountResolved for paymentId={}", paymentId);
                queue.publish(resolvedEvent);
            } else {
                logger.info("[AccountService][AccountServiceMessageHandler] Customer lookup failed for customerId={}", customerId);
            }
        } catch (Exception e) {
            logger.error("[AccountService][AccountServiceMessageHandler] ERROR handling TokenValidated: " + e.getClass().getName() + ": " + e.getMessage(), e);
        }
    }


    private void handlePaymentInitiatedForPayment(Event event) {
        try {
            PaymentInitiatedEvent initiated = event.getArgument(0, PaymentInitiatedEvent.class);
            String paymentId = initiated.getPaymentId();
            String merchantId = initiated.getMerchantId();

            MerchantLookupRequest lookupRequest = new MerchantLookupRequest(
                    "payment-" + paymentId + "-merchantLookup",
                    merchantId
            );
            MerchantLookupResponse lookupResponse = accountService.lookupMerchant(lookupRequest);
            if (lookupResponse.isSuccess()) {
                MerchantBankAccountResolved resolved = new MerchantBankAccountResolved(
                        paymentId,
                        lookupResponse.getMerchantId(),
                        lookupResponse.getBankAccountNumber()
                );
                Event resolvedEvent = new Event("MerchantBankAccountResolved", new Object[]{resolved});
                logger.info("[AccountService] Publishing MerchantBankAccountResolved for paymentId={}", paymentId);
                queue.publish(resolvedEvent);
            } else {
                logger.info("[AccountService] Merchant lookup failed for merchantId={}", merchantId);
            }
        } catch (Exception e) {
            logger.error("[AccountService] ERROR handling PaymentInitiated: " + e.getClass().getName() + ": " + e.getMessage(), e);
        }
    }
}
