package dtu.pay.account.dto;

public class CustomerDeregistrationRequest {
    private final String requestId;
    private final String customerId;

    public CustomerDeregistrationRequest(String requestId, String customerId) {
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
