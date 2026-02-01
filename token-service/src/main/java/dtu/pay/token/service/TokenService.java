package dtu.pay.token.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dtu.pay.token.CustomerCreatedEvent;
import dtu.pay.token.PaymentInitiatedEvent;
import dtu.pay.token.Exceptions.TokenGenerationException;
import dtu.pay.token.Exceptions.TokenValidationException;
import dtu.pay.token.Models.Customer;
import dtu.pay.token.Models.Token;
import dtu.pay.token.dto.TokenGenerationRequest;
import dtu.pay.token.dto.TokenGenerationResponse;
import dtu.pay.token.dto.TokenValidationResponse;

public class TokenService {

    private final Map<String, Customer> customers = new HashMap<>();

    // Maximum allowed unused tokens per customer
    private static final int MAX_UNUSED_TOKENS = 6;

    public Customer onCustomerCreated(CustomerCreatedEvent event) { 
        String customerId = event.getCustomerId();
        return customers.computeIfAbsent(customerId, Customer::new);
    }

    public TokenGenerationResponse generateTokens(TokenGenerationRequest request) {
        String requestId = request.getRequestId();
        String customerId = request.getCustomerId();
        int requestedCount = request.getRequestedTokenCount();

        try {
            if (requestedCount < 1 || requestedCount > 5) {
                throw new TokenGenerationException("Can only request 1 to 5 tokens per request");
            }

            Customer customer = customers.get(customerId);
            if (customer == null) {
                throw new TokenGenerationException("Customer not found: " + customerId);
            }

            List<Token> activeTokens = customer.getActiveTokens();
            int currentUnused = activeTokens.size();

            if (currentUnused > 1) {
                throw new TokenGenerationException(
                        "Request denied: customer has more than 1 unused token");
            }
            if (currentUnused + requestedCount > MAX_UNUSED_TOKENS) {
                throw new TokenGenerationException(
                        "Request denied: cannot exceed 6 unused tokens");
            }

            List<Token> newTokens = new ArrayList<>();
            for (int i = 0; i < requestedCount; i++) {
                Token token = new Token();
                newTokens.add(token); 
            }

            customer.addTokens(newTokens);

            return TokenGenerationResponse.success(requestId, customerId, newTokens);

        } catch (TokenGenerationException e) {
            return TokenGenerationResponse.failure(requestId, customerId, e.getMessage());
        }
    }


    public TokenValidationResponse validateAndConsumeToken(PaymentInitiatedEvent request) {
        String tokenValue = request.getToken();
        String paymentId = request.getPaymentId();

        try {
            if (tokenValue == null || tokenValue.isEmpty()) {
                throw new TokenValidationException("Token is required");
            }

            Customer tokenOwner = null;
            Token matchingToken = null;

            for (Customer customer : customers.values()) {
                for (Token token : customer.getActiveTokens()) {
                    if (token.getValue().toString().equals(tokenValue)) {
                        tokenOwner = customer;
                        matchingToken = token;
                        break;
                    }
                }
                if (tokenOwner != null) break;
            }

            if (tokenOwner == null || matchingToken == null) {
                throw new TokenValidationException("Invalid Token: Token not found or already used");
            }

            // Consume the token
            tokenOwner.removeToken(matchingToken);

            return TokenValidationResponse.success(tokenOwner.getId(), paymentId);

        } catch (TokenValidationException e) {
            return TokenValidationResponse.failure(paymentId, e.getMessage());
        }
    }
}

