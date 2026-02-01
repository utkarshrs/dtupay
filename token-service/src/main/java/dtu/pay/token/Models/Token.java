package dtu.pay.token.Models;

import java.util.UUID;

public class Token {

    private final UUID value;

    public Token() {
        this.value = UUID.randomUUID();
    }

    public UUID getValue() {
        return value;
    }
    
}
