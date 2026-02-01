package dtu.pay.account.dto;

public class CustomerDeregistrationResponse {
    private final String requestId;
    private final boolean success;
    private final String errorMessage;

    public CustomerDeregistrationResponse(String requestId, boolean success, String errorMessage) {
        this.requestId = requestId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CustomerDeregistrationResponse success(String requestId) {
        return new CustomerDeregistrationResponse(requestId, true, null);
    }

    public static CustomerDeregistrationResponse failure(String requestId, String errorMessage) {
        return new CustomerDeregistrationResponse(requestId, false, errorMessage);
    }

    public String getRequestId() {
        return requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
