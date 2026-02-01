package dtu.pay.facade.dto.api.response;

import java.math.BigDecimal;
import java.util.List;

public class ManagerReport {
    private List<PaymentInfo> payments;
    private BigDecimal totalAmount;
    private boolean success;
    private String errorMessage;

    public ManagerReport() {}

    public ManagerReport(List<PaymentInfo> payments, BigDecimal totalAmount, boolean success, String errorMessage) {
        this.payments = payments;
        this.totalAmount = totalAmount;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ManagerReport success(List<PaymentInfo> payments, BigDecimal totalAmount) {
        return new ManagerReport(payments, totalAmount, true, null);
    }

    public static ManagerReport failure(String errorMessage) {
        return new ManagerReport(List.of(), BigDecimal.ZERO, false, errorMessage);
    }

    public List<PaymentInfo> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentInfo> payments) {
        this.payments = payments;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
