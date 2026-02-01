package dtu.pay.facade.dto.api.response;

import java.math.BigDecimal;

public class PaymentResponse {
    private String paymentId;
    private BigDecimal amount;
    private boolean success;
    private String errorMessage;

    public PaymentResponse() {}

    public PaymentResponse(String paymentId, BigDecimal amount, boolean success, String errorMessage) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static PaymentResponse success(String paymentId, BigDecimal amount) {
        return new PaymentResponse(paymentId, amount, true, null);
    }

    public static PaymentResponse failure(String errorMessage) {
        return new PaymentResponse(null, null, false, errorMessage);
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
