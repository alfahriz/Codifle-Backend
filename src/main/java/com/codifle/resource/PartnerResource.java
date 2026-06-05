package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/partners")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Partners", description = "Partners section")
public class PartnerResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get all active partners")
    public Response getAll() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_partners()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/count")
    @Operation(summary = "Count active partners")
    public Response count() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_partners()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/section")
    @Operation(summary = "Get partners section metadata")
    public Response getSection() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_partners_section()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
