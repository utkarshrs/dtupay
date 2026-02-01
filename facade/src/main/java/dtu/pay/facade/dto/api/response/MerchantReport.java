package dtu.pay.facade.dto.api.response;

import java.util.List;

public class MerchantReport {
    private List<PaymentInfo> payments;
    private boolean success;
    private String errorMessage;

    public MerchantReport() {}

    public MerchantReport(List<PaymentInfo> payments, boolean success, String errorMessage) {
        this.payments = payments;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static MerchantReport success(List<PaymentInfo> payments) {
        return new MerchantReport(payments, true, null);
    }

    public static MerchantReport failure(String errorMessage) {
        return new MerchantReport(List.of(), false, errorMessage);
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
