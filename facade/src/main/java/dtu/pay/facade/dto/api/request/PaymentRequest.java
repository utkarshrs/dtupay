package dtu.pay.facade.dto.api.request;

import java.math.BigDecimal;

public class PaymentRequest {
    private String token;
    private BigDecimal amount;
    private String description;

    public PaymentRequest() {}

    public PaymentRequest(String token, BigDecimal amount, String description) {
        this.token = token;
        this.amount = amount;
        this.description = description;
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
}
