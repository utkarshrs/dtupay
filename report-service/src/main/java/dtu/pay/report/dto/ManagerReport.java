package dtu.pay.report.dto;

import java.util.Collections;
import java.util.List;

import dtu.pay.report.PaymentRecord;

public class ManagerReport {
    private String requestId;
    private List<PaymentRecord> payments;

    public ManagerReport() {
    }

    public ManagerReport(String requestId, List<PaymentRecord> payments) {
        this.requestId = requestId;
        this.payments = List.copyOf(payments);
    }

    public String getRequestId() {
        return requestId;
    }

    public List<PaymentRecord> getPayments() {
        return Collections.unmodifiableList(payments);
    }
}
