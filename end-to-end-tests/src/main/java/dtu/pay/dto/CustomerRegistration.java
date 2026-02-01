package dtu.pay.dto;

public record CustomerRegistration(
        String firstName,
        String lastName,
        String cpr,
        String bankAccountNumber
) {}
