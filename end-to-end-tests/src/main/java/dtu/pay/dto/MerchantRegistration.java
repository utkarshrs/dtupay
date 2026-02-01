package dtu.pay.dto;

public record MerchantRegistration(
        String firstName,
        String lastName,
        String cpr,
        String bankAccountNumber
) {}
