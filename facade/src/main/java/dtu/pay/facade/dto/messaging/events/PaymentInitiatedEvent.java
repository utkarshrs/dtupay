package dtu.pay.facade.dto.messaging.events;

import java.math.BigDecimal;

public class PaymentInitiatedEvent {
    private final String merchantId;
    private final String token;
    private final BigDecimal amount;
    private final String paymentId;
    private final String description;
    private final String timestamp;

    public PaymentInitiatedEvent(
            String merchantId,
            String token,
            BigDecimal amount,
            String paymentId,
            String description,
            String timestamp) {
        this.merchantId = merchantId;
        this.token = token;
        this.amount = amount;
        this.paymentId = paymentId;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getToken() {
        return token;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
