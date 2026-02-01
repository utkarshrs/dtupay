package dtu.pay.token.dto;

public class TokenValidationResponse {

    private final String customerId;      
    private final String paymentId;   
    private final boolean success;        
    private final String errorMessage;    

    private TokenValidationResponse(
            String customerId,
            String paymentId,
            boolean success,
            String errorMessage
    ) {
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static TokenValidationResponse success(
            String customerId,
            String paymentId
    ) {
        return new TokenValidationResponse(
                customerId,
                paymentId,
                true,
                null
        );
    }

    public static TokenValidationResponse failure(
            String paymentId,
            String errorMessage
    ) {
        return new TokenValidationResponse(
                null,
                paymentId,
                false,
                errorMessage
        );
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

