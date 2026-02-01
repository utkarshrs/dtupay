package dtu.pay.token;

import dtu.pay.token.dto.TokenGenerationRequest;
import dtu.pay.token.dto.TokenGenerationResponse;
import dtu.pay.token.dto.TokenValidationResponse;
import dtu.pay.token.service.TokenService;
import messaging.Event;
import messaging.MessageQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenServiceMessageHandler {
    
    private final MessageQueue queue;
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceMessageHandler.class);
    public TokenServiceMessageHandler(MessageQueue queue, TokenService tokenService){
        this.queue = queue;
        this.tokenService = tokenService;
        this.queue.addHandler("CustomerRegistered", this::handleCustomerCreated);
        this.queue.addHandler("TokensRequested", this::handleTokenGenerationRequested);
        this.queue.addHandler("PaymentInitiated", this::handleTokenValidation);
        logger.info("[TokenService] Handlers registered for CustomerRegistered, TokensRequested, PaymentInitiated");
    }

    private void handleCustomerCreated(Event event){
        logger.info("[TokenService] Received CustomerRegistered event");
        CustomerCreatedEvent customerEvent = event.getArgument(0, CustomerCreatedEvent.class);
        logger.info("[TokenService] Customer event: customerId=" + customerEvent.getCustomerId() + ", success=" + customerEvent.isSuccess());
        tokenService.onCustomerCreated(customerEvent);
        logger.info("[TokenService] Customer registered in token service: " + customerEvent.getCustomerId());
    }

    private void handleTokenGenerationRequested(Event event) {
        logger.info("[TokenService] Received TokensRequested event");
        TokenGenerationRequest request =
                event.getArgument(0, TokenGenerationRequest.class);
        logger.info("[TokenService] Token request: customerId=" + request.getCustomerId() + ", count=" + request.getRequestedTokenCount());

        TokenGenerationResponse response = tokenService.generateTokens(request);
        logger.info("[TokenService] Token generation result: success=" + response.isSuccess() + ", error=" + response.getErrorMessage());

        if (response.isSuccess()) {
            Event successEvent = new Event(
                    "TokenGenerationSucceeded",
                    new Object[]{ response }
            );
            queue.publish(successEvent);
        } else {
            Event failureEvent = new Event(
                    "TokenGenerationDenied",
                    new Object[]{ response }
            );
            queue.publish(failureEvent);
        }
    }

    private void handleTokenValidation(Event event) {
        logger.info("[TokenService] Received PaymentInitiated event for token validation");
        try {
            PaymentInitiatedEvent request =
                    event.getArgument(0, PaymentInitiatedEvent.class);

            logger.info("[TokenService] Token validation request: token=" + request.getToken() +
                ", merchantId=" + request.getMerchantId() + ", paymentId=" + request.getPaymentId());
            TokenValidationResponse response = tokenService.validateAndConsumeToken(request);
            logger.info("[TokenService] Token validation result: success=" + response.isSuccess() + 
                ", customerId=" + response.getCustomerId() + ", error=" + response.getErrorMessage());

            if (response.isSuccess()) {
                Event successEvent = new Event(
                        "TokenValidated",
                        new Object[]{ response }
                );
                logger.info("[TokenService] Publishing TokenValidated");
                queue.publish(successEvent);
            } else {
                Event failureEvent = new Event(
                        "TokenValidationFailed",
                        new Object[]{ response }
                );
                logger.info("[TokenService] Publishing TokenValidationFailed: " + response.getErrorMessage());
                queue.publish(failureEvent);
            }
        } catch (Exception e) {
            logger.error("[TokenService] ERROR handling PaymentInitiated: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }



   
}
