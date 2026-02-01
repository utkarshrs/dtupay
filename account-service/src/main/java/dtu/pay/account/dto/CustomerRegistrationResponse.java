package dtu.pay.account.dto;

public class CustomerRegistrationResponse {
    private final String requestId;
    private final String customerId;
    private final boolean success;
    private final String errorMessage;

    public CustomerRegistrationResponse(String requestId, String customerId, boolean success, String errorMessage) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CustomerRegistrationResponse success(String requestId, String customerId) {
        return new CustomerRegistrationResponse(requestId, customerId, true, null);
    }

    public static CustomerRegistrationResponse failure(String requestId, String errorMessage) {
        return new CustomerRegistrationResponse(requestId, null, false, errorMessage);
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
