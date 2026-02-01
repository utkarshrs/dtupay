package dtu.pay.token;

public class CustomerCreatedEvent {

    private String requestId;
    private String customerId;
    private boolean success;
    private String errorMessage;

    // Default constructor required for JSON deserialization
    public CustomerCreatedEvent() {}

    public CustomerCreatedEvent(String requestId, String customerId) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.success = true;
        this.errorMessage = null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
