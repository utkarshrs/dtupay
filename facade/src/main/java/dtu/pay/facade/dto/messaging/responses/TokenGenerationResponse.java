package dtu.pay.facade.dto.messaging.responses;

import java.util.List;

public class TokenGenerationResponse {
    private String requestId;
    private String customerId;
    private List<TokenDTO> tokens;
    private boolean success;
    private String errorMessage;

    // Default constructor for Gson
    public TokenGenerationResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<TokenDTO> getTokens() {
        return tokens;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class TokenDTO {
        private String value;

        public TokenDTO() {
        }

        public String getValue() {
            return value;
        }
    }
}
