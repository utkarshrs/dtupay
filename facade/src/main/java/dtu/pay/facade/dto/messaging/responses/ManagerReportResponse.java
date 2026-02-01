package dtu.pay.facade.dto.messaging.responses;

import java.math.BigDecimal;
import java.util.List;

public class ManagerReportResponse {
    private String requestId;
    private List<PaymentRecord> payments;

    // Default constructor for Gson
    public ManagerReportResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<PaymentRecord> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentRecord> payments) {
        this.payments = payments;
    }

    public static class PaymentRecord {
        private String token;
        private String customerId;
        private String merchantId;
        private BigDecimal amount;
        private String description;
        private String timestamp;

        public PaymentRecord() {
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
