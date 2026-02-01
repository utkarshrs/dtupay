package dtu.pay.account.dto;

public class CustomerLookupResponse {
    private final String requestId;
    private final String customerId;
    private final String bankAccountNumber;
    private final boolean success;
    private final String errorMessage;

    public CustomerLookupResponse(String requestId, String customerId, String bankAccountNumber,
                                   boolean success, String errorMessage) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.bankAccountNumber = bankAccountNumber;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CustomerLookupResponse success(String requestId, String customerId, String bankAccountNumber) {
        return new CustomerLookupResponse(requestId, customerId, bankAccountNumber, true, null);
    }

    public static CustomerLookupResponse failure(String requestId, String errorMessage) {
        return new CustomerLookupResponse(requestId, null, null, false, errorMessage);
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
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
