package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/theme")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Theme", description = "Brand colors and typography tokens")
public class ThemeResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get brand theme tokens")
    public Response get() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_theme()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
