package dtu.pay.account;

public class Customer {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String cpr;
    private final String bankAccountNumber;

    public Customer(String id, String firstName, String lastName, String cpr, String bankAccountNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpr = cpr;
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getId() {
        return id;
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
