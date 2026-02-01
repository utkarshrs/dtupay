package dtu.pay.payment.dto;

public class BankAccountsResolved {
    private final String paymentId;
    private final String customerId;
    private final String merchantId;
    private final String fromAccountId;
    private final String toAccountId;

    public BankAccountsResolved(String paymentId,
                                String customerId,
                                String merchantId,
                                String fromAccountId,
                                String toAccountId) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }
}
