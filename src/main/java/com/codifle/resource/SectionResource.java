package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/sections")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Sections", description = "Section visibility and order")
public class SectionResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get all sections with visibility and order")
    public Response getAll() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_sections()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/count")
    @Operation(summary = "Count sections")
    public Response count() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_sections()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/nav")
    @Operation(summary = "Get visible nav links")
    public Response getNav() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_nav()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/nav/count")
    @Operation(summary = "Count visible nav links")
    public Response countNav() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_nav()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
