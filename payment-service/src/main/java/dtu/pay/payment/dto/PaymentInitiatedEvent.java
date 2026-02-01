package dtu.pay.payment.dto;

import java.math.BigDecimal;

public class PaymentInitiatedEvent {

    private String merchantId;
    private String token;
    private BigDecimal amount;
    private String paymentId;
    private String description;
    private String timestamp;

    // Default constructor for Gson
    public PaymentInitiatedEvent() {
    }

    public PaymentInitiatedEvent(
            String merchantId,
            String token,
            BigDecimal amount,
            String paymentId
    ) {
        this(merchantId, token, amount, paymentId, "", null);
    }

    public PaymentInitiatedEvent(
            String merchantId,
            String token,
            BigDecimal amount,
            String paymentId,
            String description,
            String timestamp
    ) {
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
