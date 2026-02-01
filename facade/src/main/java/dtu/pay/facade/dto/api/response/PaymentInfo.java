package dtu.pay.facade.dto.api.response;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentInfo {
    private String token;
    private BigDecimal amount;
    private String description;
    private Instant timestamp;
    private String merchantId;  // For customer reports
    private String customerId;  // For manager reports (not visible to merchants)

    public PaymentInfo() {}

    public PaymentInfo(String token, BigDecimal amount, String description, Instant timestamp, 
                       String merchantId, String customerId) {
        this.token = token;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.merchantId = merchantId;
        this.customerId = customerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
