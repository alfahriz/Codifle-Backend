package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/hero")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Hero", description = "Hero section content")
public class HeroResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get hero content")
    public Response get() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_hero()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Get hero stat blocks")
    public Response getStats() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_hero_stats()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/stats/count")
    @Operation(summary = "Count hero stat blocks")
    public Response countStats() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_hero_stats()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
