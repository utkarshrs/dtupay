package dtu.pay.account.dto;

public class MerchantRegistrationRequest {
    private final String requestId;
    private final String firstName;
    private final String lastName;
    private final String cpr;
    private final String bankAccountNumber;

    public MerchantRegistrationRequest(String requestId, String firstName, String lastName, 
                                        String cpr, String bankAccountNumber) {
        this.requestId = requestId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpr = cpr;
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCpr() {
        return cpr;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }
}
