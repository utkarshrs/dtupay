package dtu.pay.helper;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankService_Service;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Helper class for interacting with the external bank service in tests.
 * Manages bank account creation, balance queries, and cleanup.
 */
public class BankHelper {

    private static final String BANK_API_KEY = "image7712";
    
    private static final BankService_Service service = new BankService_Service();
    private static final BankService bank = service.getBankServicePort();
    
    // Track created accounts for cleanup
    private static final List<String> createdAccounts = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(BankHelper.class);
    /**
     * Create a bank account with the specified balance.
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param cpr User's CPR number
     * @param balance Initial balance in DKK
     * @return The account ID
     */
    public String createAccount(String firstName, String lastName, String cpr, int balance) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);

        try {
            // Ensure no leftover account exists for this CPR from previous test runs
            String existingAccountId = getAccountByCpr(cpr);
            if (existingAccountId != null) {
                try {
                    bank.retireAccount(BANK_API_KEY, existingAccountId);
                } catch (BankServiceException_Exception e) {
                    logger.warn("Warning: Could not retire existing account for CPR " + cpr + ": " + e.getMessage());
                }
            }

            String accountId = bank.createAccountWithBalance(
                    BANK_API_KEY,
                    user,
                    BigDecimal.valueOf(balance)
            );
            createdAccounts.add(accountId);
            return accountId;
        } catch (BankServiceException_Exception e) {
            logger.error("Failed to create bank account: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create bank account: " + e.getMessage(), e);
        }
    }

    /**
     * Get the balance of a bank account.
     * 
     * @param accountId The account ID
     * @return The current balance
     */
    public BigDecimal getBalance(String accountId) {
        try {
            return bank.getAccount(accountId).getBalance();
        } catch (BankServiceException_Exception e) {
            logger.error("Failed to get account balance: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get account balance: " + e.getMessage(), e);
        }
    }

    /**
     * Retire (delete) a bank account.
     * 
     * @param accountId The account ID to retire
     */
    public void retireAccount(String accountId) {
        try {
            bank.retireAccount(BANK_API_KEY, accountId);
            createdAccounts.remove(accountId);
        } catch (BankServiceException_Exception e) {
            // Log but don't fail - account may already be retired
            logger.warn("Warning: Could not retire account " + accountId + ": " + e.getMessage());
        }
    }

    /**
     * Cleanup all accounts created during tests.
     * Call this in @After hooks.
     */
    public static void cleanupAllAccounts() {
        for (String accountId : new ArrayList<>(createdAccounts)) {
            try {
                bank.retireAccount(BANK_API_KEY, accountId);
            } catch (BankServiceException_Exception e) {
                logger.warn("Warning: Could not retire account " + accountId + ": " + e.getMessage());
            }
        }
        createdAccounts.clear();
    }

    /**
     * Get bank account by CPR number.
     * 
     * @param cpr The CPR number
     * @return The account ID, or null if not found
     */
    public String getAccountByCpr(String cpr) {
        try {
            return bank.getAccountByCprNumber(cpr).getId();
        } catch (BankServiceException_Exception e) {
            return null;
        }
    }

    /**
     * Transfer money between accounts directly (for test setup).
     * 
     * @param fromAccountId Source account
     * @param toAccountId Destination account
     * @param amount Amount to transfer
     */
    public void transferMoney(String fromAccountId, String toAccountId, BigDecimal amount) {
        try {
            bank.transferMoneyFromTo(fromAccountId, toAccountId, amount, "Test transfer");
        } catch (BankServiceException_Exception e) {
            logger.error("Failed to transfer money: " + e.getMessage(), e);
            throw new RuntimeException("Failed to transfer money: " + e.getMessage(), e);
        }
    }
}
