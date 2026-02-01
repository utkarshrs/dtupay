package dtu.pay.payment.infrastructure;

import dtu.pay.payment.domain.BankTransferPort;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankService_Service;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;

import java.math.BigDecimal;

public class BankServiceAdapter implements BankTransferPort {

    private static final String BANK_API_KEY = "image7712";

    private final BankService bank = new BankService_Service().getBankServicePort();

    public String createAccount(String firstName, String lastName, String cpr) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);

        try {
            return bank.createAccountWithBalance(
                    BANK_API_KEY,
                    user,
                    BigDecimal.valueOf(1000)
            );
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void retireAccount(String accountId) {
        try {
            bank.retireAccount(BANK_API_KEY, accountId);
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) {
        try {
            bank.transferMoneyFromTo(
                    fromAccountId,
                    toAccountId,
                    amount,
                    "DTU Pay payment"
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
