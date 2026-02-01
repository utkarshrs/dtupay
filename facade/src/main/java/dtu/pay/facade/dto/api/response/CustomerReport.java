package dtu.pay.facade.dto.api.response;

import java.util.List;

public class CustomerReport {
    private List<PaymentInfo> payments;
    private boolean success;
    private String errorMessage;

    public CustomerReport() {}

    public CustomerReport(List<PaymentInfo> payments, boolean success, String errorMessage) {
        this.payments = payments;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CustomerReport success(List<PaymentInfo> payments) {
        return new CustomerReport(payments, true, null);
    }

    public static CustomerReport failure(String errorMessage) {
        return new CustomerReport(List.of(), false, errorMessage);
    }

    public List<PaymentInfo> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentInfo> payments) {
        this.payments = payments;
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
