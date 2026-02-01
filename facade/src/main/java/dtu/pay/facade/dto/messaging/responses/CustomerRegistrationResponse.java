package dtu.pay.facade.dto.messaging.responses;

public class CustomerRegistrationResponse {
    private String requestId;
    private String customerId;
    private boolean success;
    private String errorMessage;

    // Default constructor for Gson
    public CustomerRegistrationResponse() {
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
