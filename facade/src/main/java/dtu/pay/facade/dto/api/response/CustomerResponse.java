package dtu.pay.facade.dto.api.response;

public class CustomerResponse {
    private String customerId;
    private boolean success;
    private String errorMessage;

    public CustomerResponse() {}

    public CustomerResponse(String customerId, boolean success, String errorMessage) {
        this.customerId = customerId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CustomerResponse success(String customerId) {
        return new CustomerResponse(customerId, true, null);
    }

    public static CustomerResponse failure(String errorMessage) {
        return new CustomerResponse(null, false, errorMessage);
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
