package dtu.pay.facade.resource;

import dtu.pay.facade.dto.api.request.CustomerRegistrationRequest;
import dtu.pay.facade.dto.api.response.CustomerReport;
import dtu.pay.facade.dto.api.response.CustomerResponse;
import dtu.pay.facade.dto.api.request.TokenRequest;
import dtu.pay.facade.dto.api.response.TokenResponse;
import dtu.pay.facade.service.CustomerFacadeService;
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

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer", description = "Customer registration, token management, and payment reporting")
public class CustomerResource {

    @Inject
    CustomerFacadeService customerService;

    @POST
    @Operation(
        summary = "Register a new customer",
        description = "Register a customer with DTU Pay using their bank account details. Returns a unique customer ID for future transactions."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Customer registered successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid registration data (e.g., missing required fields, invalid bank account)",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
        ),
        @APIResponse(
            responseCode = "409",
            description = "Customer with this bank account already exists",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
        )
    })
    public Response registerCustomer(CustomerRegistrationRequest registration) {
        CustomerResponse response = customerService.registerCustomer(registration);
        
        if (response.isSuccess()) {
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } else {
            // Return CONFLICT (409) for duplicate registration, BAD_REQUEST otherwise
            Response.Status status = response.getErrorMessage() != null 
                    && response.getErrorMessage().contains("already exists")
                    ? Response.Status.CONFLICT
                    : Response.Status.BAD_REQUEST;
            return Response.status(status)
                    .entity(response)
                    .build();
        }
    }

    @DELETE
    @Path("/{customerId}")
    @Operation(
        summary = "Deregister a customer",
        description = "Remove a customer from DTU Pay. All unused tokens will be invalidated."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Customer deregistered successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
        ),
        @APIResponse(
            responseCode = "404",
            description = "Customer not found",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
        )
    })
    public Response deregisterCustomer(
        @Parameter(description = "The customer's DTU Pay ID", required = true)
        @PathParam("customerId") String customerId
    ) {
        CustomerResponse response = customerService.deregisterCustomer(customerId);
        
        if (response.isSuccess()) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .build();
        }
    }

    @POST
    @Path("/{customerId}/tokens")
    @Operation(
        summary = "Request payment tokens",
        description = "Generate anonymous, one-time payment tokens for a customer. " +
                     "Business rules: Maximum 6 unused tokens per customer. " +
                     "Can only request when ≤1 unused token remains. " +
                     "Request 1-5 tokens at a time."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Tokens generated successfully",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid request (e.g., too many tokens requested, customer has too many unused tokens, customer not found)",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        )
    })
    public Response requestTokens(
        @Parameter(description = "The customer's DTU Pay ID", required = true)
        @PathParam("customerId") String customerId,
        TokenRequest tokenRequest
    ) {
        TokenResponse response = customerService.requestTokens(customerId, tokenRequest);
        
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
    @Path("/{customerId}/report")
    @Operation(
        summary = "Get customer payment report",
        description = "Retrieve payment history for a customer. Shows all payments made by this customer including merchant IDs, amounts, and descriptions."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Report retrieved successfully",
            content = @Content(schema = @Schema(implementation = CustomerReport.class))
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error while retrieving report",
            content = @Content(schema = @Schema(implementation = CustomerReport.class))
        )
    })
    public Response getReport(
        @Parameter(description = "The customer's DTU Pay ID", required = true)
        @PathParam("customerId") String customerId
    ) {
        CustomerReport report = customerService.getReport(customerId);
        
        if (report.isSuccess()) {
            return Response.ok(report).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(report)
                    .build();
        }
    }
}
