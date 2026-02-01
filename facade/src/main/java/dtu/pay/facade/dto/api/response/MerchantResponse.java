package dtu.pay.facade.dto.api.response;

public class MerchantResponse {
    private String merchantId;
    private boolean success;
    private String errorMessage;

    public MerchantResponse() {}

    public MerchantResponse(String merchantId, boolean success, String errorMessage) {
        this.merchantId = merchantId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static MerchantResponse success(String merchantId) {
        return new MerchantResponse(merchantId, true, null);
    }

    public static MerchantResponse failure(String errorMessage) {
        return new MerchantResponse(null, false, errorMessage);
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
