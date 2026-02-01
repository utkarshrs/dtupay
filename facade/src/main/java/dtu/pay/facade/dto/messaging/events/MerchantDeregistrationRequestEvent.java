package dtu.pay.facade.dto.messaging.events;

public class MerchantDeregistrationRequestEvent {
    private final String requestId;
    private final String merchantId;

    public MerchantDeregistrationRequestEvent(String requestId, String merchantId) {
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
