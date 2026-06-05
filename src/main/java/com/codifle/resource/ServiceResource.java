package com.codifle.resource;

import com.codifle.service.DbService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/services")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Services", description = "Services and sub-services")
public class ServiceResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get all active services")
    public Response getAll() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_services()")).build();
        } catch (Exception e) {
            return DbService.serverError(e);
        }
    }

    @GET
    @Path("/count")
    @Operation(summary = "Count active services")
    public Response count() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_services()")).build();
        } catch (Exception e) {
            return DbService.serverError(e);
        }
    }

    @GET
    @Path("/{key}")
    @Operation(summary = "Get service by key")
    public Response getByKey(@PathParam("key") String key) {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_service(?)", key);
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return DbService.serverError(e);
        }
    }

    @GET
    @Path("/{key}/subs")
    @Operation(summary = "Get sub-services by service key")
    public Response getSubs(@PathParam("key") String key) {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_service_subs(?)", key)).build();
        } catch (Exception e) {
            return DbService.serverError(e);
        }
    }

    @GET
    @Path("/{key}/subs/count")
    @Operation(summary = "Count sub-services by service key")
    public Response countSubs(@PathParam("key") String key) {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_service_subs(?)", key)).build();
        } catch (Exception e) {
            return DbService.serverError(e);
        }
    }
}
