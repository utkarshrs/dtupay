package dtu.pay.report.dto;

public class CustomerReportRequest {
    private String requestId;
    private String customerId;

    public CustomerReportRequest() {
    }

    public CustomerReportRequest(String requestId, String customerId) {
        this.requestId = requestId;
        this.customerId = customerId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }
}
