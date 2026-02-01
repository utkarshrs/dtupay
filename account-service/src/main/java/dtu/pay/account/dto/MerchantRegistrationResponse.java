package dtu.pay.account.dto;

public class MerchantRegistrationResponse {
    private final String requestId;
    private final String merchantId;
    private final boolean success;
    private final String errorMessage;

    public MerchantRegistrationResponse(String requestId, String merchantId, boolean success, String errorMessage) {
        this.requestId = requestId;
        this.merchantId = merchantId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static MerchantRegistrationResponse success(String requestId, String merchantId) {
        return new MerchantRegistrationResponse(requestId, merchantId, true, null);
    }

    public static MerchantRegistrationResponse failure(String requestId, String errorMessage) {
        return new MerchantRegistrationResponse(requestId, null, false, errorMessage);
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
