package dtu.pay.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String token,
        BigDecimal amount,
        String description
) {}
