package dtu.pay.facade.dto.messaging.events;

public class CustomerDeregistrationRequestEvent {
    private final String requestId;
    private final String customerId;

    public CustomerDeregistrationRequestEvent(String requestId, String customerId) {
        this.requestId = requestId;
        this.customerId = customerId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }
}
