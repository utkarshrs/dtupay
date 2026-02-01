package dtu.pay.facade.dto.messaging.responses;

import java.math.BigDecimal;
import java.util.List;

public class MerchantReportResponse {
    private String requestId;
    private List<MerchantPaymentView> payments;

    // Default constructor for Gson
    public MerchantReportResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<MerchantPaymentView> getPayments() {
        return payments;
    }

    public void setPayments(List<MerchantPaymentView> payments) {
        this.payments = payments;
    }

    public static class MerchantPaymentView {
        private BigDecimal amount;
        private String token;
        private String description;

        public MerchantPaymentView() {
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
