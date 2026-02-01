package dtu.pay.facade.dto.messaging.responses;

import java.math.BigDecimal;
import java.util.List;

public class CustomerReportResponse {
    private String requestId;
    private List<CustomerPaymentView> payments;

    // Default constructor for Gson
    public CustomerReportResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<CustomerPaymentView> getPayments() {
        return payments;
    }

    public void setPayments(List<CustomerPaymentView> payments) {
        this.payments = payments;
    }

    public static class CustomerPaymentView {
        private BigDecimal amount;
        private String merchantId;
        private String description;
        private String token;

        public CustomerPaymentView() {
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
