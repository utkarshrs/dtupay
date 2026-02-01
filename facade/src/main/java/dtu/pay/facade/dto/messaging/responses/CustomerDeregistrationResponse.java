package dtu.pay.facade.dto.messaging.responses;

public class CustomerDeregistrationResponse {
    private String requestId;
    private boolean success;
    private String errorMessage;

    // Default constructor for Gson
    public CustomerDeregistrationResponse() {
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
