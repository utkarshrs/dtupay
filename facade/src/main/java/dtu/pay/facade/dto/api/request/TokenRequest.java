package dtu.pay.facade.dto.api.request;

public class TokenRequest {
    private int tokenCount;

    public TokenRequest() {}

    public TokenRequest(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }
}
