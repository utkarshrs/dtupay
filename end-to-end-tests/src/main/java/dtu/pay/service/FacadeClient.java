package dtu.pay.service;

import dtu.pay.dto.CustomerRegistration;
import dtu.pay.dto.CustomerReport;
import dtu.pay.dto.CustomerResponse;
import dtu.pay.dto.ManagerReport;
import dtu.pay.dto.MerchantRegistration;
import dtu.pay.dto.MerchantReport;
import dtu.pay.dto.MerchantResponse;
import dtu.pay.dto.PaymentRequest;
import dtu.pay.dto.PaymentResponse;
import dtu.pay.dto.TokenRequest;
import dtu.pay.dto.TokenResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * HTTP client for communicating with the DTU Pay Facade Service.
 * Provides methods for all customer, merchant, and manager operations.
 */
public class FacadeClient implements AutoCloseable {

    private final Client client;
    private final String baseUrl;

    public FacadeClient() {
        this("http://localhost:8080");
    }

    public FacadeClient(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.baseUrl = baseUrl;
    }

    // ==================== Customer Operations ====================

    /**
     * Register a new customer with DTU Pay.
     */
    public CustomerResponse registerCustomer(CustomerRegistration registration) {
        Response response = client.target(baseUrl)
                .path("/customers")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(registration, MediaType.APPLICATION_JSON));
        
        CustomerResponse result = response.readEntity(CustomerResponse.class);
        response.close();
        return result;
    }

    /**
     * Register a customer and return the raw HTTP response for status checking.
     */
    public Response registerCustomerRaw(CustomerRegistration registration) {
        return client.target(baseUrl)
                .path("/customers")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(registration, MediaType.APPLICATION_JSON));
    }

    /**
     * Deregister an existing customer.
     */
    public CustomerResponse deregisterCustomer(String customerId) {
        Response response = client.target(baseUrl)
                .path("/customers/{id}")
                .resolveTemplate("id", customerId)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        
        CustomerResponse result = response.readEntity(CustomerResponse.class);
        response.close();
        return result;
    }

    /**
     * Request tokens for a customer.
     */
    public TokenResponse requestTokens(String customerId, int tokenCount) {
        TokenRequest request = new TokenRequest(tokenCount);
        Response response = client.target(baseUrl)
                .path("/customers/{id}/tokens")
                .resolveTemplate("id", customerId)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
        
        TokenResponse result = response.readEntity(TokenResponse.class);
        response.close();
        return result;
    }

    /**
     * Get the payment report for a customer.
     */
    public CustomerReport getCustomerReport(String customerId) {
        Response response = client.target(baseUrl)
                .path("/customers/{id}/report")
                .resolveTemplate("id", customerId)
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        CustomerReport result = response.readEntity(CustomerReport.class);
        response.close();
        return result;
    }

    // ==================== Merchant Operations ====================

    /**
     * Register a new merchant with DTU Pay.
     */
    public MerchantResponse registerMerchant(MerchantRegistration registration) {
        Response response = client.target(baseUrl)
                .path("/merchants")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(registration, MediaType.APPLICATION_JSON));
        
        MerchantResponse result = response.readEntity(MerchantResponse.class);
        response.close();
        return result;
    }

    /**
     * Register a merchant and return the raw HTTP response for status checking.
     */
    public Response registerMerchantRaw(MerchantRegistration registration) {
        return client.target(baseUrl)
                .path("/merchants")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(registration, MediaType.APPLICATION_JSON));
    }

    /**
     * Deregister an existing merchant.
     */
    public MerchantResponse deregisterMerchant(String merchantId) {
        Response response = client.target(baseUrl)
                .path("/merchants/{id}")
                .resolveTemplate("id", merchantId)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        
        MerchantResponse result = response.readEntity(MerchantResponse.class);
        response.close();
        return result;
    }

    /**
     * Initiate a payment from a customer to a merchant.
     */
    public PaymentResponse initiatePayment(String merchantId, PaymentRequest paymentRequest) {
        Response response = client.target(baseUrl)
                .path("/merchants/{id}/payments")
                .resolveTemplate("id", merchantId)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON));
        
        PaymentResponse result = response.readEntity(PaymentResponse.class);
        response.close();
        return result;
    }

    /**
     * Initiate a payment and return raw HTTP response.
     */
    public Response initiatePaymentRaw(String merchantId, PaymentRequest paymentRequest) {
        return client.target(baseUrl)
                .path("/merchants/{id}/payments")
                .resolveTemplate("id", merchantId)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON));
    }

    /**
     * Get the payment report for a merchant.
     */
    public MerchantReport getMerchantReport(String merchantId) {
        Response response = client.target(baseUrl)
                .path("/merchants/{id}/report")
                .resolveTemplate("id", merchantId)
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        MerchantReport result = response.readEntity(MerchantReport.class);
        response.close();
        return result;
    }

    // ==================== Manager Operations ====================

    /**
     * Get the manager report with all payments.
     */
    public ManagerReport getManagerReport() {
        Response response = client.target(baseUrl)
                .path("/manager/report")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        ManagerReport result = response.readEntity(ManagerReport.class);
        response.close();
        return result;
    }

    /**
     * Close the client and release resources.
     */
    public void close() {
        client.close();
    }
}
