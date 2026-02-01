package dtu.pay.report.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MerchantReport {
    private String requestId;
    private List<MerchantPaymentView> payments;

    public MerchantReport() {
        this.payments = new ArrayList<>();
    }

    public MerchantReport(String requestId, List<MerchantPaymentView> payments) {
        this.requestId = requestId;
        this.payments = new ArrayList<>(payments);
    }

    public String getRequestId() {
        return requestId;
    }

    public List<MerchantPaymentView> getPayments() {
        return payments == null ? List.of() : Collections.unmodifiableList(payments);
    }
}
