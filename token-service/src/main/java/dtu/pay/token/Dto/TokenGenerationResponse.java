package dtu.pay.token.dto;

import java.util.Collections;
import java.util.List;

import dtu.pay.token.Models.Token;

public class TokenGenerationResponse {

    private final String requestId;
    private final String customerId;
    private final List<Token> tokens;
    private final boolean success;
    private final String errorMessage;

    private TokenGenerationResponse(
            String requestId,
            String customerId,
            List<Token> tokens,
            boolean success,
            String errorMessage
    ) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.tokens = tokens;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static TokenGenerationResponse success(
            String requestId,
            String customerId,
            List<Token> tokens
    ) {
        return new TokenGenerationResponse(requestId, customerId, tokens, true, null);
    }

    public static TokenGenerationResponse failure(
            String requestId,
            String customerId,
            String errorMessage
    ) {
        return new TokenGenerationResponse(requestId, customerId, List.of(), false, errorMessage);
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
