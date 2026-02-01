package dtu.pay.account.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dtu.pay.account.Customer;
import dtu.pay.account.Merchant;
import dtu.pay.account.dto.CustomerDeregistrationRequest;
import dtu.pay.account.dto.CustomerDeregistrationResponse;
import dtu.pay.account.dto.CustomerLookupRequest;
import dtu.pay.account.dto.CustomerLookupResponse;
import dtu.pay.account.dto.CustomerRegistrationRequest;
import dtu.pay.account.dto.CustomerRegistrationResponse;
import dtu.pay.account.dto.MerchantDeregistrationRequest;
import dtu.pay.account.dto.MerchantDeregistrationResponse;
import dtu.pay.account.dto.MerchantLookupRequest;
import dtu.pay.account.dto.MerchantLookupResponse;
import dtu.pay.account.dto.MerchantRegistrationRequest;
import dtu.pay.account.dto.MerchantRegistrationResponse;

public class AccountService {

    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Merchant> merchants = new HashMap<>();
    // Index for CPR lookups to check duplicates
    private final Map<String, String> customerCprIndex = new HashMap<>();
    private final Map<String, String> merchantCprIndex = new HashMap<>();

    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request) {
        // Check for duplicate CPR
        if (customerCprIndex.containsKey(request.getCpr())) {
            return CustomerRegistrationResponse.failure(
                    request.getRequestId(),
                    "Customer with CPR " + request.getCpr() + " already exists"
            );
        }

        String customerId = UUID.randomUUID().toString();
        Customer customer = new Customer(
                customerId,
                request.getFirstName(),
                request.getLastName(),
                request.getCpr(),
                request.getBankAccountNumber()
        );
        customers.put(customerId, customer);
        customerCprIndex.put(request.getCpr(), customerId);
        return CustomerRegistrationResponse.success(request.getRequestId(), customerId);
    }

    public CustomerDeregistrationResponse deregisterCustomer(CustomerDeregistrationRequest request) {
        String customerId = request.getCustomerId();
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return CustomerDeregistrationResponse.failure(
                    request.getRequestId(),
                    "Customer not found: " + customerId
            );
        }
        customers.remove(customerId);
        customerCprIndex.remove(customer.getCpr());
        return CustomerDeregistrationResponse.success(request.getRequestId());
    }

    public CustomerLookupResponse lookupCustomer(CustomerLookupRequest request) {
        String customerId = request.getCustomerId();
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return CustomerLookupResponse.failure(
                    request.getRequestId(),
                    "Customer not found: " + customerId
            );
        }
        return CustomerLookupResponse.success(
                request.getRequestId(),
                customer.getId(),
                customer.getBankAccountNumber()
        );
    }

    public MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request) {
        // Check for duplicate CPR
        if (merchantCprIndex.containsKey(request.getCpr())) {
            return MerchantRegistrationResponse.failure(
                    request.getRequestId(),
                    "Merchant with CPR " + request.getCpr() + " already exists"
            );
        }

        String merchantId = UUID.randomUUID().toString();
        Merchant merchant = new Merchant(
                merchantId,
                request.getFirstName(),
                request.getLastName(),
                request.getCpr(),
                request.getBankAccountNumber()
        );
        merchants.put(merchantId, merchant);
        merchantCprIndex.put(request.getCpr(), merchantId);
        return MerchantRegistrationResponse.success(request.getRequestId(), merchantId);
    }

    public MerchantDeregistrationResponse deregisterMerchant(MerchantDeregistrationRequest request) {
        String merchantId = request.getMerchantId();
        Merchant merchant = merchants.get(merchantId);
        if (merchant == null) {
            return MerchantDeregistrationResponse.failure(
                    request.getRequestId(),
                    "Merchant not found: " + merchantId
            );
        }
        merchants.remove(merchantId);
        merchantCprIndex.remove(merchant.getCpr());
        return MerchantDeregistrationResponse.success(request.getRequestId());
    }

    public MerchantLookupResponse lookupMerchant(MerchantLookupRequest request) {
        String merchantId = request.getMerchantId();
        Merchant merchant = merchants.get(merchantId);
        if (merchant == null) {
            return MerchantLookupResponse.failure(
                    request.getRequestId(),
                    "Merchant not found: " + merchantId
            );
        }
        return MerchantLookupResponse.success(
                request.getRequestId(),
                merchant.getId(),
                merchant.getBankAccountNumber()
        );
    }

    // Utility methods for testing
    public boolean customerExists(String customerId) {
        return customers.containsKey(customerId);
    }

    public boolean merchantExists(String merchantId) {
        return merchants.containsKey(merchantId);
    }

    public int getCustomerCount() {
        return customers.size();
    }

    public int getMerchantCount() {
        return merchants.size();
    }
}
