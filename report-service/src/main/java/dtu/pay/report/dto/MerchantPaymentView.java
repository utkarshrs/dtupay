package dtu.pay.report.dto;

import java.math.BigDecimal;

public class MerchantPaymentView {
    private BigDecimal amount;
    private String token;
    private String description;

    public MerchantPaymentView() {
    }

    public MerchantPaymentView(BigDecimal amount, String token, String description) {
        this.amount = amount;
        this.token = token;
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getToken() {
        return token;
    }

    public String getDescription() {
        return description;
    }
}
