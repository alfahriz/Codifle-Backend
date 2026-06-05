package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/clients")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Clients", description = "Clients section")
public class ClientResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get all active clients ordered by track and position")
    public Response getAll() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_clients()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/count")
    @Operation(summary = "Count active clients")
    public Response count() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_clients()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/section")
    @Operation(summary = "Get clients section metadata")
    public Response getSection() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_clients_section()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
