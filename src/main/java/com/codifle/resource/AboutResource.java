package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/about")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "About", description = "About section content")
public class AboutResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get about content")
    public Response get() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_about()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/milestones")
    @Operation(summary = "Get company milestones")
    public Response getMilestones() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_about_milestones()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/milestones/count")
    @Operation(summary = "Count milestones")
    public Response countMilestones() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_about_milestones()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Get about stat cards")
    public Response getStats() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_about_stats()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/stats/count")
    @Operation(summary = "Count about stat cards")
    public Response countStats() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_about_stats()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
