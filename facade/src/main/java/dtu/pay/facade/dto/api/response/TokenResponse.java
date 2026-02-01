package dtu.pay.facade.dto.api.response;

import java.util.List;

public class TokenResponse {
    private List<String> tokens;
    private boolean success;
    private String errorMessage;

    public TokenResponse() {}

    public TokenResponse(List<String> tokens, boolean success, String errorMessage) {
        this.tokens = tokens;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static TokenResponse success(List<String> tokens) {
        return new TokenResponse(tokens, true, null);
    }

    public static TokenResponse failure(String errorMessage) {
        return new TokenResponse(List.of(), false, errorMessage);
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
