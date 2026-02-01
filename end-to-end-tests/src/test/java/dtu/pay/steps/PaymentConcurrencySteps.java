package dtu.pay.steps;

import dtu.pay.dto.*;
import dtu.pay.helper.BankHelper;
import dtu.pay.service.FacadeClient;
import io.cucumber.java.After;
import io.cucumber.java.en.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentConcurrencySteps {

    private final FacadeClient client = new FacadeClient();
    private final BankHelper bankHelper = new BankHelper();
    private static final Logger logger = LoggerFactory.getLogger(PaymentConcurrencySteps.class);
    // Test state
    private final List<String> customerIds = new ArrayList<>();
    private final List<String> customerBankAccounts = new ArrayList<>();
    private final List<List<String>> customerTokens = new ArrayList<>();
    private final List<String> merchantIds = new ArrayList<>();
    private final List<String> merchantBankAccounts = new ArrayList<>();
    private final List<String> allBankAccounts = new ArrayList<>();
    
    private List<PaymentResponse> paymentResponses;
    private int expectedSuccessCount;

    @Given("{int} registered customers with {int} kr balance and {int} token each")
    public void registeredCustomersWithBalanceAndTokens(int count, int balance, int tokenCount) {
        for (int i = 0; i < count; i++) {
            String firstName = "Customer" + i;
            String lastName = "Test";
            String cpr = String.format("0101%02d-%04d", 90 + i, 1000 + i);
            
            // Create bank account
            String bankAccount = bankHelper.createAccount(firstName, lastName, cpr, balance);
            customerBankAccounts.add(bankAccount);
            allBankAccounts.add(bankAccount);
            
            // Register customer
            CustomerRegistration registration = new CustomerRegistration(
                    firstName, lastName, cpr, bankAccount
            );
            CustomerResponse response = client.registerCustomer(registration);
            assertTrue(response.isSuccess(), "Customer registration should succeed");
            customerIds.add(response.getCustomerId());
            
            // Request tokens
            TokenResponse tokenResponse = client.requestTokens(response.getCustomerId(), tokenCount);
            assertTrue(tokenResponse.isSuccess(), "Token request should succeed");
            customerTokens.add(new ArrayList<>(tokenResponse.getTokens()));
        }
    }

    @Given("{int} registered merchants with {int} kr balance")
    public void registeredMerchantsWithBalance(int count, int balance) {
        for (int i = 0; i < count; i++) {
            String firstName = "Merchant" + i;
            String lastName = "Test";
            String cpr = String.format("0202%02d-%04d", 90 + i, 2000 + i);
            
            // Create bank account
            String bankAccount = bankHelper.createAccount(firstName, lastName, cpr, balance);
            merchantBankAccounts.add(bankAccount);
            allBankAccounts.add(bankAccount);
            
            // Register merchant
            MerchantRegistration registration = new MerchantRegistration(
                    firstName, lastName, cpr, bankAccount
            );
            MerchantResponse response = client.registerMerchant(registration);
            assertTrue(response.isSuccess(), "Merchant registration should succeed");
            merchantIds.add(response.getMerchantId());
        }
    }

    @Given("{int} registered customer with {int} kr balance and {int} tokens")
    public void registeredCustomerWithBalanceAndTokens(int count, int balance, int tokenCount) {
        registeredCustomersWithBalanceAndTokens(count, balance, tokenCount);
    }

    @Given("{int} registered merchant with {int} kr balance")
    public void registeredMerchantWithBalance(int count, int balance) {
        registeredMerchantsWithBalance(count, balance);
    }

    @When("all customers initiate payments of {int} kr to their respective merchants concurrently")
    public void allCustomersInitiatePaymentsToRespectiveMerchantsConcurrently(int amount) throws Exception {
        expectedSuccessCount = customerIds.size();
        paymentResponses = executeConcurrentPayments(amount, false);
    }

    @When("all {int} customers initiate payments of {int} kr to the same merchant concurrently")
    public void allCustomersInitiatePaymentsToSameMerchantConcurrently(int customerCount, int amount) throws Exception {
        expectedSuccessCount = customerCount;
        paymentResponses = executeConcurrentPayments(amount, true);
    }

    @When("the customer initiates {int} concurrent payments of {int} kr using different tokens")
    public void customerInitiatesConcurrentPaymentsUsingDifferentTokens(int paymentCount, int amount) throws Exception {
        expectedSuccessCount = paymentCount;
        
        ExecutorService executor = Executors.newFixedThreadPool(paymentCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(paymentCount);
        
        List<Future<PaymentResponse>> futures = new ArrayList<>();
        String customerId = customerIds.get(0);
        List<String> tokens = customerTokens.get(0);
        
        for (int i = 0; i < paymentCount; i++) {
            final int index = i;
            Future<PaymentResponse> future = executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    
                    PaymentRequest request = new PaymentRequest(
                            tokens.get(index),
                            BigDecimal.valueOf(amount),
                            "Concurrent payment " + index
                    );
                    return client.initiatePayment(merchantIds.get(index), request);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
            futures.add(future);
        }
        
        startLatch.countDown(); // Start all threads
        doneLatch.await(30, TimeUnit.SECONDS); // Wait for completion
        
        paymentResponses = new ArrayList<>();
        for (Future<PaymentResponse> future : futures) {
            paymentResponses.add(future.get(5, TimeUnit.SECONDS));
        }
        
        executor.shutdown();
    }

    @Then("all {int} payments should succeed")
    public void allPaymentsShouldSucceed(int expectedCount) {
        assertNotNull(paymentResponses, "Payment responses should not be null");
        assertEquals(expectedCount, paymentResponses.size(), "Should have " + expectedCount + " responses");
        
        long successCount = paymentResponses.stream()
                .filter(PaymentResponse::isSuccess)
                .count();
        
        // Log failures for debugging
        paymentResponses.stream()
                .filter(r -> !r.isSuccess())
                .forEach(r -> logger.error("Payment failed: " + r.getErrorMessage()));
        
        assertEquals(expectedCount, successCount, 
                "All " + expectedCount + " payments should succeed");
    }

    @Then("all customers should have {int} kr balance")
    public void allCustomersShouldHaveBalance(int expectedBalance) {
        for (String bankAccount : customerBankAccounts) {
            BigDecimal actualBalance = bankHelper.getBalance(bankAccount);
            assertEquals(0, BigDecimal.valueOf(expectedBalance).compareTo(actualBalance),
                    "Customer " + bankAccount + " should have " + expectedBalance + " kr");
        }
    }

    @Then("all merchants should have {int} kr balance")
    public void allMerchantsShouldHaveBalance(int expectedBalance) {
        for (String bankAccount : merchantBankAccounts) {
            BigDecimal actualBalance = bankHelper.getBalance(bankAccount);
            assertEquals(0, BigDecimal.valueOf(expectedBalance).compareTo(actualBalance),
                    "Merchant " + bankAccount + " should have " + expectedBalance + " kr");
        }
    }

    @Then("the merchant should have {int} kr balance")
    public void theMerchantShouldHaveBalance(int expectedBalance) {
        String bankAccount = merchantBankAccounts.get(0);
        BigDecimal actualBalance = bankHelper.getBalance(bankAccount);
        assertEquals(0, BigDecimal.valueOf(expectedBalance).compareTo(actualBalance),
                "Merchant should have " + expectedBalance + " kr");
    }

    @Then("the customer should have {int} kr balance")
    public void theCustomerShouldHaveBalance(int expectedBalance) {
        String bankAccount = customerBankAccounts.get(0);
        BigDecimal actualBalance = bankHelper.getBalance(bankAccount);
        assertEquals(0, BigDecimal.valueOf(expectedBalance).compareTo(actualBalance),
                "Customer should have " + expectedBalance + " kr");
    }

    /**
     * Execute concurrent payments with synchronized start
     */
    private List<PaymentResponse> executeConcurrentPayments(int amount, boolean sameMerchant) throws Exception {
        int threadCount = customerIds.size();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        
        List<Future<PaymentResponse>> futures = new ArrayList<>();
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            Future<PaymentResponse> future = executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    
                    String token = customerTokens.get(index).get(0);
                    String merchantId = sameMerchant ? merchantIds.get(0) : merchantIds.get(index);
                    
                    PaymentRequest request = new PaymentRequest(
                            token,
                            BigDecimal.valueOf(amount),
                            "Concurrent payment " + index
                    );
                    return client.initiatePayment(merchantId, request);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
            futures.add(future);
        }
        
        startLatch.countDown(); // Start all threads simultaneously
        doneLatch.await(30, TimeUnit.SECONDS); // Wait for all to complete
        
        List<PaymentResponse> responses = new ArrayList<>();
        for (Future<PaymentResponse> future : futures) {
            responses.add(future.get(5, TimeUnit.SECONDS));
        }
        
        executor.shutdown();
        return responses;
    }

    @After(order = 1) // Run before SharedSteps cleanup
    public void cleanup() {
        // Deregister customers
        for (String id : customerIds) {
            try {
                client.deregisterCustomer(id);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }

        // Deregister merchants
        for (String id : merchantIds) {
            try {
                client.deregisterMerchant(id);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }

        // Retire bank accounts
        for (String accountId : allBankAccounts) {
            try {
                bankHelper.retireAccount(accountId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }

        // Close HTTP client (instance-specific)
        try {
            client.close();
        } catch (Exception e) {
            // Ignore cleanup errors
        }

        // Clear state
        customerIds.clear();
        customerBankAccounts.clear();
        customerTokens.clear();
        merchantIds.clear();
        merchantBankAccounts.clear();
        allBankAccounts.clear();
        paymentResponses = null;
    }
}
