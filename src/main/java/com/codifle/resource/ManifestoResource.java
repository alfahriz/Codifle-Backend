package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/manifesto")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Manifesto", description = "Manifesto section content")
public class ManifestoResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get manifesto content")
    public Response get() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_manifesto()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
