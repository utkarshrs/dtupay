package dtu.pay.payment.dto;

public class CustomerBankAccountResolved {
    private String paymentId;
    private String customerId;
    private String fromAccountId;

    // Default constructor for Gson
    public CustomerBankAccountResolved() {
    }

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
