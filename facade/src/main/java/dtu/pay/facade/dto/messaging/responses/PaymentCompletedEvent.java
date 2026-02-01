package dtu.pay.facade.dto.messaging.responses;

import java.math.BigDecimal;

public class PaymentCompletedEvent {
    private String paymentId;
    private String token;
    private String customerId;
    private String merchantId;
    private BigDecimal amount;
    private String description;
    private String timestamp;

    // Default constructor for Gson
    public PaymentCompletedEvent() {
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getToken() {
        return token;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
