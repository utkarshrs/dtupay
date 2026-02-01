package dtu.pay.facade.resource;

import dtu.pay.facade.dto.api.response.ErrorResponse;
import dtu.pay.facade.dto.api.response.ManagerReport;
import dtu.pay.facade.service.ManagerFacadeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/manager")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Manager", description = "Manager reporting and system-wide payment overview")
public class ManagerResource {

    @Inject
    ManagerFacadeService managerService;

    @GET
    @Path("/report")
    @Operation(
        summary = "Get complete system payment report",
        description = "Retrieve all payments in the DTU Pay system. Shows complete payment history including customer IDs, merchant IDs, amounts, descriptions, and timestamps. " +
                     "Also includes the total amount transferred through the system. This endpoint is for system administrators only."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Report retrieved successfully",
            content = @Content(schema = @Schema(implementation = ManagerReport.class))
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error while retrieving report",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public Response getReport() {
        ManagerReport report = managerService.getReport();
        
        if (report.isSuccess()) {
            return Response.ok(report).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(report.getErrorMessage(), 500))
                    .build();
        }
    }
}
