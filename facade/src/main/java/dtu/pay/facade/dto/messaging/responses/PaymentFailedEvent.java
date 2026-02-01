package dtu.pay.facade.dto.messaging.responses;

public class PaymentFailedEvent {
    private String paymentId;
    private String errorMessage;
    private String timestamp;

    // Default constructor for Gson
    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(String paymentId, String errorMessage, String timestamp) {
        this.paymentId = paymentId;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
