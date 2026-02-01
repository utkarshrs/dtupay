package dtu.pay.report.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerReport {
    private String requestId;
    private List<CustomerPaymentView> payments;

    public CustomerReport() {
        this.payments = new ArrayList<>();
    }

    public CustomerReport(String requestId, List<CustomerPaymentView> payments) {
        this.requestId = requestId;
        this.payments = new ArrayList<>(payments);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<CustomerPaymentView> getPayments() {
        return payments == null ? List.of() : Collections.unmodifiableList(payments);
    }

    public void setPayments(List<CustomerPaymentView> payments) {
        this.payments = payments;
    }
}
