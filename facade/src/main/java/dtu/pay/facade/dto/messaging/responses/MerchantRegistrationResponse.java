package dtu.pay.facade.dto.messaging.responses;

public class MerchantRegistrationResponse {
    private String requestId;
    private String merchantId;
    private boolean success;
    private String errorMessage;

    // Default constructor for Gson
    public MerchantRegistrationResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
