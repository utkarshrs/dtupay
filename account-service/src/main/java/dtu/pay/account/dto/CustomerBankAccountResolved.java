package dtu.pay.account.dto;

public class CustomerBankAccountResolved {
    private final String paymentId;
    private final String customerId;
    private final String fromAccountId;

    public CustomerBankAccountResolved(String paymentId,
                                       String customerId,
                                       String fromAccountId) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.fromAccountId = fromAccountId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }
}
