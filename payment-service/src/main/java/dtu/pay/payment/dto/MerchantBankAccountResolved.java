package dtu.pay.payment.dto;

public class MerchantBankAccountResolved {
    private String paymentId;
    private String merchantId;
    private String toAccountId;

    // Default constructor for Gson
    public MerchantBankAccountResolved() {
    }

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
