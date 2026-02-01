package dtu.pay.account.dto;

public class MerchantDeregistrationRequest {
    private final String requestId;
    private final String merchantId;

    public MerchantDeregistrationRequest(String requestId, String merchantId) {
        this.requestId = requestId;
        this.merchantId = merchantId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMerchantId() {
        return merchantId;
    }
}
