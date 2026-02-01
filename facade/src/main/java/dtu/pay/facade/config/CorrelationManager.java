package dtu.pay.facade.config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import messaging.Event;
import messaging.MessageQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized correlation manager for async request-response messaging.
 * Handles all correlation patterns: requestId for general flows, paymentId for payments.
 */
@ApplicationScoped
public class CorrelationManager {

    private final Map<String, CompletableFuture<Event>> pendingRequests = new ConcurrentHashMap<>();
    private final MessageQueue messageQueue;
    private static final long DEFAULT_TIMEOUT_SECONDS = 30;
    private static final Logger logger = LoggerFactory.getLogger(CorrelationManager.class);

    @Inject
    public CorrelationManager(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @PostConstruct
    public void init() {
        // Register handlers for all response events once
        registerHandler("CustomerRegistered");
        registerHandler("CustomerRegistrationFailed");
        registerHandler("CustomerDeregistered");
        registerHandler("CustomerDeregistrationFailed");
        registerHandler("TokenGenerationSucceeded");
        registerHandler("TokenGenerationDenied");
        registerHandler("CustomerReportGenerated");
        registerHandler("MerchantRegistered");
        registerHandler("MerchantRegistrationFailed");
        registerHandler("MerchantDeregistered");
        registerHandler("MerchantDeregistrationFailed");
        registerHandler("PaymentCompleted");
        registerHandler("PaymentFailed");
        registerHandler("TokenValidationFailed");
        registerHandler("MerchantReportGenerated");
        registerHandler("ManagerReportGenerated");

        logger.info("[CorrelationManager] Initialized with handlers for all response events");
    }

    private void registerHandler(String topic) {
        messageQueue.addHandler(topic, this::handleResponse);
    }

    private void handleResponse(Event event) {
        String correlationId = extractCorrelationId(event);
        if (correlationId != null) {
            CompletableFuture<Event> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(event);
            }
        }
    }

    /**
     * Generates a unique correlation ID.
     */
    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Publishes an event and waits for a response.
     * 
     * @param event The event to publish
     * @param correlationId The correlation ID to track the response
     * @return The response event
     */
    public Event publishAndWait(Event event, String correlationId) {
        CompletableFuture<Event> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        messageQueue.publish(event);

        try {
            return future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            pendingRequests.remove(correlationId);
            logger.error("[CorrelationManager] Request timed out for correlationId: " + correlationId);
            throw new RuntimeException("Request timed out after " + DEFAULT_TIMEOUT_SECONDS + " seconds", e);
        } catch (Exception e) {
            pendingRequests.remove(correlationId);
            logger.error("[CorrelationManager] Error waiting for response: " + e.getMessage(), e);
            throw new RuntimeException("Error waiting for response: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts correlation ID from event payload.
     * Supports: requestId (general flows), paymentId (payment flows).
     */
    private String extractCorrelationId(Event event) {
        try {
            Object arg = event.getArgument(0, Object.class);
            
            // Handle Map (from RabbitMQ JSON deserialization)
            if (arg instanceof Map<?, ?> map) {
                String id = (String) map.get("requestId");
                if (id != null) return id;
                
                id = (String) map.get("paymentId");
                if (id != null) return id;
                
                return null;
            }
            
            // Handle strongly-typed DTOs
            try {
                var method = arg.getClass().getMethod("getRequestId");
                return (String) method.invoke(arg);
            } catch (NoSuchMethodException e) {
                try {
                    var method = arg.getClass().getMethod("getPaymentId");
                    return (String) method.invoke(arg);
                } catch (NoSuchMethodException ex) {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("[CorrelationManager] Error extracting correlation ID: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets the underlying message queue for direct access if needed.
     */
    public MessageQueue getMessageQueue() {
        return messageQueue;
    }
}
