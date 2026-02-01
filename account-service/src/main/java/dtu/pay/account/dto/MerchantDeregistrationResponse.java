package dtu.pay.account.dto;

public class MerchantDeregistrationResponse {
    private final String requestId;
    private final boolean success;
    private final String errorMessage;

    public MerchantDeregistrationResponse(String requestId, boolean success, String errorMessage) {
        this.requestId = requestId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static MerchantDeregistrationResponse success(String requestId) {
        return new MerchantDeregistrationResponse(requestId, true, null);
    }

    public static MerchantDeregistrationResponse failure(String requestId, String errorMessage) {
        return new MerchantDeregistrationResponse(requestId, false, errorMessage);
    }

    public String getRequestId() {
        return requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
