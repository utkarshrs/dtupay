package dtu.pay.token.dto;

public class TokenGenerationRequest {

    private final String requestId;
    private final String customerId;
    private final int requestedTokenCount;

    public TokenGenerationRequest(String requestId, String customerId, int requestedTokenCount) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.requestedTokenCount = requestedTokenCount;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getRequestedTokenCount() {
        return requestedTokenCount;
    }
}
