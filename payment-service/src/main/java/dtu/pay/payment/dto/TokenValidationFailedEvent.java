package dtu.pay.payment.dto;

public class TokenValidationFailedEvent {
    private String paymentId;
    private String token;
    private String reason;

    // Default constructor for Gson
    public TokenValidationFailedEvent() {
    }

    public TokenValidationFailedEvent(String paymentId, String token, String reason) {
        this.paymentId = paymentId;
        this.token = token;
        this.reason = reason;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getToken() {
        return token;
    }

    public String getReason() {
        return reason;
    }
}
