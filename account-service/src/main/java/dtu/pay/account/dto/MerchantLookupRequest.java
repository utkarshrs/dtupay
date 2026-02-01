package dtu.pay.account.dto;

public class MerchantLookupRequest {
    private final String requestId;
    private final String merchantId;

    public MerchantLookupRequest(String requestId, String merchantId) {
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
