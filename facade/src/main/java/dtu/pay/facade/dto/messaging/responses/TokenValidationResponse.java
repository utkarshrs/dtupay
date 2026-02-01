package dtu.pay.facade.dto.messaging.responses;

public class TokenValidationResponse {
    private String customerId;
    private String paymentId;
    private boolean success;
    private String errorMessage;

    // Default constructor for Gson
    public TokenValidationResponse() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
