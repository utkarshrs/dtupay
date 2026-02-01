package dtu.pay.steps;

import dtu.pay.dto.*;
import dtu.pay.helper.BankHelper;
import dtu.pay.service.FacadeClient;
import io.cucumber.java.After;
import io.cucumber.java.en.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MerchantSteps {

    private final FacadeClient client = new FacadeClient();
    private final BankHelper bankHelper = new BankHelper();

    // Test state
    private String merchantFirstName;
    private String merchantLastName;
    private String merchantCpr;
    private String merchantBankAccount;
    private String merchantId;
    private MerchantResponse merchantResponse;

    // Cleanup tracking
    private final List<String> registeredMerchantIds = new ArrayList<>();
    private final List<String> bankAccountIds = new ArrayList<>();

    @Given("a merchant with name {string} {string} and CPR {string}")
    public void aMerchantWithNameAndCpr(String firstName, String lastName, String cpr) {
        merchantFirstName = firstName;
        merchantLastName = lastName;
        merchantCpr = cpr;
    }

    @Given("the merchant has a bank account with balance {int} kr")
    public void theMerchantHasABankAccountWithBalance(int balance) {
        merchantBankAccount = bankHelper.createAccount(
                merchantFirstName,
                merchantLastName,
                merchantCpr,
                balance
        );
        bankAccountIds.add(merchantBankAccount);
    }

    @When("the merchant registers with DTU Pay")
    public void theMerchantRegistersWithDtuPay() {
        MerchantRegistration registration = new MerchantRegistration(
                merchantFirstName,
                merchantLastName,
                merchantCpr,
                merchantBankAccount
        );
        merchantResponse = client.registerMerchant(registration);
        if (merchantResponse.isSuccess()) {
            merchantId = merchantResponse.getMerchantId();
            registeredMerchantIds.add(merchantId);
        }
    }

    @Then("the merchant registration is successful")
    public void theMerchantRegistrationIsSuccessful() {
        assertTrue(merchantResponse.isSuccess(),
                "Merchant registration should be successful but got error: " + merchantResponse.getErrorMessage());
    }

    @Then("the merchant receives a DTU Pay merchant ID")
    public void theMerchantReceivesADtuPayMerchantId() {
        assertNotNull(merchantId, "Merchant ID should not be null");
        assertFalse(merchantId.isEmpty(), "Merchant ID should not be empty");
    }

    @Given("a registered merchant with name {string} {string} and CPR {string}")
    public void aRegisteredMerchantWithNameAndCpr(String firstName, String lastName, String cpr) {
        merchantFirstName = firstName;
        merchantLastName = lastName;
        merchantCpr = cpr;
        
        // Create bank account
        merchantBankAccount = bankHelper.createAccount(firstName, lastName, cpr, 1000);
        bankAccountIds.add(merchantBankAccount);
        
        // Register with DTU Pay
        MerchantRegistration registration = new MerchantRegistration(
                firstName, lastName, cpr, merchantBankAccount
        );
        merchantResponse = client.registerMerchant(registration);
        merchantId = merchantResponse.getMerchantId();
        registeredMerchantIds.add(merchantId);
    }

    @When("the merchant deregisters from DTU Pay")
    public void theMerchantDeregistersFromDtuPay() {
        merchantResponse = client.deregisterMerchant(merchantId);
        if (merchantResponse.isSuccess()) {
            registeredMerchantIds.remove(merchantId);
        }
    }

    @Then("the merchant deregistration is successful")
    public void theMerchantDeregistrationIsSuccessful() {
        assertTrue(merchantResponse.isSuccess(),
                "Merchant deregistration should be successful but got error: " + merchantResponse.getErrorMessage());
    }

    // Getters for shared state with other step classes
    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantBankAccount() {
        return merchantBankAccount;
    }

    public List<String> getRegisteredMerchantIds() {
        return registeredMerchantIds;
    }

    public List<String> getBankAccountIds() {
        return bankAccountIds;
    }

    @After(order = 1) // Run before SharedSteps cleanup
    public void cleanup() {
        // Deregister merchants
        for (String id : registeredMerchantIds) {
            try {
                client.deregisterMerchant(id);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        registeredMerchantIds.clear();

        // Retire bank accounts
        for (String accountId : bankAccountIds) {
            try {
                bankHelper.retireAccount(accountId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        bankAccountIds.clear();
        
        // Close HTTP client (instance-specific)
        try {
            client.close();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}
