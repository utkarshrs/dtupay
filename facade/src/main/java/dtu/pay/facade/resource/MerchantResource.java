package dtu.pay.facade.resource;

import dtu.pay.facade.dto.api.response.ErrorResponse;
import dtu.pay.facade.dto.api.request.MerchantRegistrationRequest;
import dtu.pay.facade.dto.api.response.MerchantReport;
import dtu.pay.facade.dto.api.response.MerchantResponse;
import dtu.pay.facade.dto.api.request.PaymentRequest;
import dtu.pay.facade.dto.api.response.PaymentResponse;
import dtu.pay.facade.service.MerchantFacadeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/merchants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Merchant", description = "Merchant registration, payment processing, and reporting")
public class MerchantResource {

    @Inject
    MerchantFacadeService merchantService;

    @POST
    @Operation(
        summary = "Register a new merchant",
        description = "Register a merchant with DTU Pay using their bank account details. Returns a unique merchant ID for receiving payments."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Merchant registered successfully",
            content = @Content(schema = @Schema(implementation = MerchantResponse.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid registration data (e.g., missing required fields, invalid bank account)",
            content = @Content(schema = @Schema(implementation = MerchantResponse.class))
        )
    })
    public Response registerMerchant(MerchantRegistrationRequest registration) {
        MerchantResponse response = merchantService.registerMerchant(registration);
        
        if (response.isSuccess()) {
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
        }
    }

    @DELETE
    @Path("/{merchantId}")
    @Operation(
        summary = "Deregister a merchant",
        description = "Remove a merchant from DTU Pay. The merchant will no longer be able to receive payments."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Merchant deregistered successfully",
            content = @Content(schema = @Schema(implementation = MerchantResponse.class))
        ),
        @APIResponse(
            responseCode = "404",
            description = "Merchant not found",
            content = @Content(schema = @Schema(implementation = MerchantResponse.class))
        )
    })
    public Response deregisterMerchant(
        @Parameter(description = "The merchant's DTU Pay ID", required = true)
        @PathParam("merchantId") String merchantId
    ) {
        MerchantResponse response = merchantService.deregisterMerchant(merchantId);
        
        if (response.isSuccess()) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .build();
        }
    }

    @POST
    @Path("/{merchantId}/payments")
    @Operation(
        summary = "Process a payment",
        description = "Initiate a payment from a customer to this merchant using a one-time token. " +
                     "The token is provided by the customer during checkout. " +
                     "The payment transfers money from the customer's bank account to the merchant's bank account via the external bank service."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Payment processed successfully",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Payment failed (e.g., invalid token, token already used, insufficient funds, merchant not found)",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))
        )
    })
    public Response initiatePayment(
        @Parameter(description = "The merchant's DTU Pay ID", required = true)
        @PathParam("merchantId") String merchantId,
        PaymentRequest paymentRequest
    ) {
        PaymentResponse response = merchantService.initiatePayment(merchantId, paymentRequest);
        
        if (response.isSuccess()) {
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
        }
    }

    @GET
    @Path("/{merchantId}/report")
    @Operation(
        summary = "Get merchant payment report",
        description = "Retrieve payment history for a merchant. Shows all payments received by this merchant including amounts and descriptions. " +
                     "Note: Customer identity is NOT included in merchant reports for privacy."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Report retrieved successfully",
            content = @Content(schema = @Schema(implementation = MerchantReport.class))
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error while retrieving report",
            content = @Content(schema = @Schema(implementation = MerchantReport.class))
        )
    })
    public Response getReport(
        @Parameter(description = "The merchant's DTU Pay ID", required = true)
        @PathParam("merchantId") String merchantId
    ) {
        MerchantReport report = merchantService.getReport(merchantId);
        
        if (report.isSuccess()) {
            return Response.ok(report).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(report)
                    .build();
        }
    }
}
