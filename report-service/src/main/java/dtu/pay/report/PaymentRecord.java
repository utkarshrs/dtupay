package dtu.pay.report;

import java.math.BigDecimal;

public class PaymentRecord {
    private String token;
    private String customerId;
    private String merchantId;
    private BigDecimal amount;
    private String description;
    private String timestamp;

    public PaymentRecord() {
    }

    public PaymentRecord(String token,
                         String customerId,
                         String merchantId,
                         BigDecimal amount,
                         String description,
                         String timestamp) {
        this.token = token;
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
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
