package dtu.pay.facade.dto.api.request;

public class CustomerRegistrationRequest {
    private String firstName;
    private String lastName;
    private String cpr;
    private String bankAccountNumber;

    public CustomerRegistrationRequest() {}

    public CustomerRegistrationRequest(String firstName, String lastName, String cpr, String bankAccountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpr = cpr;
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
}
