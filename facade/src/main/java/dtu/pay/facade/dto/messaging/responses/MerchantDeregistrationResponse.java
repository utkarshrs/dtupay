package dtu.pay.facade.dto.messaging.responses;

public class MerchantDeregistrationResponse {
    private String requestId;
    private boolean success;
    private String errorMessage;

    // Default constructor for Gson
    public MerchantDeregistrationResponse() {
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
