package dtu.pay.account.dto;

public class TokenValidatedEvent {
    private String customerId;
    private String paymentId;

    // Default constructor for Gson
    public TokenValidatedEvent() {
    }

    public TokenValidatedEvent(String customerId, String paymentId) {
        this.customerId = customerId;
        this.paymentId = paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
