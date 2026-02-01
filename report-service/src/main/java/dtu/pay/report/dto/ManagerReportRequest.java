package dtu.pay.report.dto;

public class ManagerReportRequest {
    private String requestId;

    public ManagerReportRequest() {
    }

    public ManagerReportRequest(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }
}
