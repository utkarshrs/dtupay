package dtu.pay.account.dto;

public class MerchantBankAccountResolved {
    private final String paymentId;
    private final String merchantId;
    private final String toAccountId;

    public MerchantBankAccountResolved(String paymentId,
                                       String merchantId,
                                       String toAccountId) {
        this.paymentId = paymentId;
        this.merchantId = merchantId;
        this.toAccountId = toAccountId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getToAccountId() {
        return toAccountId;
    }
}
