package dtu.pay.report.dto;

import java.math.BigDecimal;

public class CustomerPaymentView {
    private BigDecimal amount;
    private String merchantId;
    private String description;
    private String token;

    public CustomerPaymentView() {
    }

    public CustomerPaymentView(BigDecimal amount, String merchantId, String description, String token) {
        this.amount = amount;
        this.merchantId = merchantId;
        this.description = description;
        this.token = token;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
