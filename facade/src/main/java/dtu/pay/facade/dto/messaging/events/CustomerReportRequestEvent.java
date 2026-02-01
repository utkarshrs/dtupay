package dtu.pay.facade.dto.messaging.events;

public class CustomerReportRequestEvent {
    private final String requestId;
    private final String customerId;

    public CustomerReportRequestEvent(String requestId, String customerId) {
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
