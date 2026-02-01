package dtu.pay.report.dto;

public class MerchantReportRequest {
    private String requestId;
    private String merchantId;

    public MerchantReportRequest() {
    }

    public MerchantReportRequest(String requestId, String merchantId) {
        this.requestId = requestId;
        this.merchantId = merchantId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMerchantId() {
        return merchantId;
    }
}
