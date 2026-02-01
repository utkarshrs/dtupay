package dtu.pay.payment;

import dtu.pay.payment.dto.CustomerBankAccountResolved;
import dtu.pay.payment.dto.MerchantBankAccountResolved;
import dtu.pay.payment.dto.PaymentInitiatedEvent;
import dtu.pay.payment.dto.TokenValidationFailedEvent;
import dtu.pay.payment.service.PaymentService;
import messaging.Event;
import messaging.MessageQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServiceMessageHandler {

    private final MessageQueue queue;
    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceMessageHandler.class);
    public PaymentServiceMessageHandler(MessageQueue queue, PaymentService paymentService) {
        this.queue = queue;
        this.paymentService = paymentService;
        this.queue.addHandler("PaymentInitiated", this::handlePaymentInitiated);
        this.queue.addHandler("CustomerBankAccountResolved", this::handleCustomerBankAccountResolved);
        this.queue.addHandler("MerchantBankAccountResolved", this::handleMerchantBankAccountResolved);
        this.queue.addHandler("TokenValidationFailed", this::handleTokenValidationFailed);
        logger.info("[PaymentService] Handlers registered for PaymentInitiated, CustomerBankAccountResolved, MerchantBankAccountResolved, TokenValidationFailed");
    }

    private void handlePaymentInitiated(Event event) {
        try {
            PaymentInitiatedEvent initiated = event.getArgument(0, PaymentInitiatedEvent.class);
            logger.info("[PaymentService] PaymentInitiated: paymentId=" + initiated.getPaymentId() + 
                ", merchantId=" + initiated.getMerchantId() + ", amount=" + initiated.getAmount());
            PaymentCompletedEvent completedEvent = paymentService.registerPaymentInitiated(initiated);
            logger.info("[PaymentService] Payment registered");
            if (completedEvent != null) {
                Event response = new Event("PaymentCompleted", new Object[]{completedEvent});
                logger.info("[PaymentService] Publishing PaymentCompleted for paymentId=" + completedEvent.getPaymentId());
                queue.publish(response);
            } else {
                // Check if this is a failure (all prerequisites met but transfer failed)
                PaymentFailedEvent failedEvent = paymentService.checkForBankTransferFailure(initiated.getPaymentId());
                if (failedEvent != null) {
                    Event response = new Event("PaymentFailed", new Object[]{failedEvent});
                    logger.info("[PaymentService] Publishing PaymentFailed for paymentId=" + failedEvent.getPaymentId() + 
                        ", error=" + failedEvent.getErrorMessage());
                    queue.publish(response);
                } else {
                    logger.info("[PaymentService] PaymentInitiated processed, waiting for bank account resolutions");
                }
            }
        } catch (Exception e) {
            logger.error("[PaymentService] ERROR handling PaymentInitiated: " + e.getClass().getName() + ": " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void handleCustomerBankAccountResolved(Event event) {
        try {
            CustomerBankAccountResolved customer = event.getArgument(0, CustomerBankAccountResolved.class);
            logger.info("[PaymentService] CustomerBankAccountResolved: paymentId=" + customer.getPaymentId() + 
                ", customerId=" + customer.getCustomerId() + ", fromAccount=" + customer.getFromAccountId());
            PaymentCompletedEvent completedEvent = paymentService.handleCustomerBankAccountResolved(customer);
            if (completedEvent != null) {
                Event response = new Event("PaymentCompleted", new Object[]{completedEvent});
                logger.info("[PaymentService] Publishing PaymentCompleted for paymentId=" + completedEvent.getPaymentId());
                queue.publish(response);
            } else {
                // Check if this is a failure (all prerequisites met but transfer failed)
                PaymentFailedEvent failedEvent = paymentService.checkForBankTransferFailure(customer.getPaymentId());
                if (failedEvent != null) {
                    Event response = new Event("PaymentFailed", new Object[]{failedEvent});
                    logger.info("[PaymentService] Publishing PaymentFailed for paymentId=" + failedEvent.getPaymentId() + 
                        ", error=" + failedEvent.getErrorMessage());
                    queue.publish(response);
                } else {
                    logger.info("[PaymentService] CustomerBankAccountResolved processed, waiting for merchant account");
                }
            }
        } catch (Exception e) {
            logger.error("[PaymentService] ERROR handling CustomerBankAccountResolved: " + e.getClass().getName() + ": " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void handleMerchantBankAccountResolved(Event event) {
        try {
            MerchantBankAccountResolved merchant = event.getArgument(0, MerchantBankAccountResolved.class);
            logger.info("[PaymentService] MerchantBankAccountResolved: paymentId=" + merchant.getPaymentId() + 
                ", merchantId=" + merchant.getMerchantId() + ", toAccount=" + merchant.getToAccountId());
            PaymentCompletedEvent completedEvent = paymentService.handleMerchantBankAccountResolved(merchant);
            if (completedEvent != null) {
                Event response = new Event("PaymentCompleted", new Object[]{completedEvent});
                logger.info("[PaymentService] Publishing PaymentCompleted for paymentId=" + completedEvent.getPaymentId());
                queue.publish(response);
            } else {
                // Check if this is a failure (all prerequisites met but transfer failed)
                PaymentFailedEvent failedEvent = paymentService.checkForBankTransferFailure(merchant.getPaymentId());
                if (failedEvent != null) {
                    Event response = new Event("PaymentFailed", new Object[]{failedEvent});
                    logger.info("[PaymentService] Publishing PaymentFailed for paymentId=" + failedEvent.getPaymentId() + 
                        ", error=" + failedEvent.getErrorMessage());
                    queue.publish(response);
                } else {
                    logger.info("[PaymentService] MerchantBankAccountResolved processed, waiting for customer account");
                }
            }
        } catch (Exception e) {
            logger.error("[PaymentService] ERROR handling MerchantBankAccountResolved: " + e.getClass().getName() + ": " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void handleTokenValidationFailed(Event event) {
        try {
            TokenValidationFailedEvent tokenFailedEvent = event.getArgument(0, TokenValidationFailedEvent.class);
            logger.info("[PaymentService] TokenValidationFailed: paymentId=" + tokenFailedEvent.getPaymentId() + 
                ", token=" + tokenFailedEvent.getToken() + ", reason=" + tokenFailedEvent.getReason());
            
            PaymentFailedEvent failedEvent = paymentService.handleTokenValidationFailed(tokenFailedEvent);
            
            Event response = new Event("PaymentFailed", new Object[]{failedEvent});
            logger.info("[PaymentService] Publishing PaymentFailed for paymentId=" + failedEvent.getPaymentId() + 
                ", error=" + failedEvent.getErrorMessage());
            queue.publish(response);
        } catch (Exception e) {
            logger.error("[PaymentService] ERROR handling TokenValidationFailed: " + e.getClass().getName() + ": " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
