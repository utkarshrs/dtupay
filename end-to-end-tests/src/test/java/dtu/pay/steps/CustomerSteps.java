package dtu.pay.steps;

import dtu.pay.dto.*;
import dtu.pay.helper.BankHelper;
import dtu.pay.service.FacadeClient;
import io.cucumber.java.After;
import io.cucumber.java.en.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerSteps {

    private final FacadeClient client = new FacadeClient();
    private final BankHelper bankHelper = new BankHelper();

    // Test state
    private String customerFirstName;
    private String customerLastName;
    private String customerCpr;
    private String customerBankAccount;
    private String customerId;
    private CustomerResponse customerResponse;

    // Cleanup tracking
    private final List<String> registeredCustomerIds = new ArrayList<>();
    private final List<String> bankAccountIds = new ArrayList<>();

    @Given("a customer with name {string} {string} and CPR {string}")
    public void aCustomerWithNameAndCpr(String firstName, String lastName, String cpr) {
        customerFirstName = firstName;
        customerLastName = lastName;
        customerCpr = cpr;
    }

    @Given("the customer has a bank account with balance {int} kr")
    public void theCustomerHasABankAccountWithBalance(int balance) {
        customerBankAccount = bankHelper.createAccount(
                customerFirstName,
                customerLastName,
                customerCpr,
                balance
        );
        bankAccountIds.add(customerBankAccount);
    }

    @When("the customer registers with DTU Pay")
    public void theCustomerRegistersWithDtuPay() {
        CustomerRegistration registration = new CustomerRegistration(
                customerFirstName,
                customerLastName,
                customerCpr,
                customerBankAccount
        );
        customerResponse = client.registerCustomer(registration);
        if (customerResponse.isSuccess()) {
            customerId = customerResponse.getCustomerId();
            registeredCustomerIds.add(customerId);
        }
    }

    @Then("the customer registration is successful")
    public void theCustomerRegistrationIsSuccessful() {
        assertTrue(customerResponse.isSuccess(), 
                "Customer registration should be successful but got error: " + customerResponse.getErrorMessage());
    }

    @Then("the customer receives a DTU Pay customer ID")
    public void theCustomerReceivesADtuPayCustomerId() {
        assertNotNull(customerId, "Customer ID should not be null");
        assertFalse(customerId.isEmpty(), "Customer ID should not be empty");
    }

    @Given("a registered customer with name {string} {string} and CPR {string}")
    public void aRegisteredCustomerWithNameAndCpr(String firstName, String lastName, String cpr) {
        customerFirstName = firstName;
        customerLastName = lastName;
        customerCpr = cpr;
        
        // Create bank account
        customerBankAccount = bankHelper.createAccount(firstName, lastName, cpr, 1000);
        bankAccountIds.add(customerBankAccount);
        
        // Register with DTU Pay
        CustomerRegistration registration = new CustomerRegistration(
                firstName, lastName, cpr, customerBankAccount
        );
        customerResponse = client.registerCustomer(registration);
        customerId = customerResponse.getCustomerId();
        registeredCustomerIds.add(customerId);
    }

    @When("the customer deregisters from DTU Pay")
    public void theCustomerDeregistersFromDtuPay() {
        customerResponse = client.deregisterCustomer(customerId);
        if (customerResponse.isSuccess()) {
            registeredCustomerIds.remove(customerId);
        }
    }

    @Then("the customer deregistration is successful")
    public void theCustomerDeregistrationIsSuccessful() {
        assertTrue(customerResponse.isSuccess(),
                "Customer deregistration should be successful but got error: " + customerResponse.getErrorMessage());
    }

    // Getters for shared state with other step classes
    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerBankAccount() {
        return customerBankAccount;
    }

    public List<String> getRegisteredCustomerIds() {
        return registeredCustomerIds;
    }

    public List<String> getBankAccountIds() {
        return bankAccountIds;
    }

    @After(order = 1) // Run before SharedSteps cleanup
    public void cleanup() {
        // Deregister customers
        for (String id : registeredCustomerIds) {
            try {
                client.deregisterCustomer(id);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        registeredCustomerIds.clear();

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
