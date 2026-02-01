package dtu.pay.facade.dto.messaging.events;

public class MerchantReportRequestEvent {
    private final String requestId;
    private final String merchantId;

    public MerchantReportRequestEvent(String requestId, String merchantId) {
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
