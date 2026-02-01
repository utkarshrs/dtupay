package dtu.pay.token.Models;


import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final String customerId;
    private final List<Token> activeTokens;

    public Customer(String customerId) {
        this.customerId = customerId;
        this.activeTokens = new ArrayList<>();
    }

    public String getId() {
        return customerId;
    }

    public List<Token> getActiveTokens() {
        return activeTokens;
    }

    public int getActiveTokenCount() {
        return activeTokens.size();
    }

    public void addTokens(List<Token> tokens) {
        activeTokens.addAll(tokens);
    }

    public void removeToken(Token token) {
        activeTokens.remove(token);
    }
}
