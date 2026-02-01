package dtu.pay.facade.dto.messaging.events;

public class ManagerReportRequestEvent {
    private final String requestId;

    public ManagerReportRequestEvent(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }
}
