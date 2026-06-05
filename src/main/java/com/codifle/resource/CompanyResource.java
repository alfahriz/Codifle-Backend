package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/company")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Company", description = "Company identity and socials")
public class CompanyResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get company info")
    public Response get() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_company()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/socials")
    @Operation(summary = "Get company social links")
    public Response getSocials() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_company_socials()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/socials/count")
    @Operation(summary = "Count active social links")
    public Response countSocials() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_company_socials()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
