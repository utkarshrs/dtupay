package dtu.pay.account.dto;

public class MerchantLookupResponse {
    private final String requestId;
    private final String merchantId;
    private final String bankAccountNumber;
    private final boolean success;
    private final String errorMessage;

    public MerchantLookupResponse(String requestId, String merchantId, String bankAccountNumber,
                                   boolean success, String errorMessage) {
        this.requestId = requestId;
        this.merchantId = merchantId;
        this.bankAccountNumber = bankAccountNumber;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static MerchantLookupResponse success(String requestId, String merchantId, String bankAccountNumber) {
        return new MerchantLookupResponse(requestId, merchantId, bankAccountNumber, true, null);
    }

    public static MerchantLookupResponse failure(String requestId, String errorMessage) {
        return new MerchantLookupResponse(requestId, null, null, false, errorMessage);
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
