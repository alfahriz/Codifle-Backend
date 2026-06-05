package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.UUID;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Products and categories")
public class ProductResource {

    @Inject DbService db;

    @GET
    @Operation(summary = "Get all active products, optional filter by category")
    public Response getAll(@QueryParam("category") String category) {
        try {
            return category != null && !category.isBlank()
                ? Response.ok(db.queryList("SELECT * FROM fn_get_products(?)", category)).build()
                : Response.ok(db.queryList("SELECT * FROM fn_get_products()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/count")
    @Operation(summary = "Count active products, optional filter by category")
    public Response count(@QueryParam("category") String category) {
        try {
            return category != null && !category.isBlank()
                ? Response.ok(db.queryCount("SELECT fn_count_products(?)", category)).build()
                : Response.ok(db.queryCount("SELECT fn_count_products()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/categories")
    @Operation(summary = "Get product categories")
    public Response getCategories() {
        try {
            return Response.ok(db.queryList("SELECT * FROM fn_get_product_categories()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/categories/count")
    @Operation(summary = "Count product categories")
    public Response countCategories() {
        try {
            return Response.ok(db.queryCount("SELECT fn_count_product_categories()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/{id}/features")
    @Operation(summary = "Get features for a product")
    public Response getFeatures(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return Response.ok(db.queryList("SELECT * FROM fn_get_product_features(?)", uuid)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Invalid product ID format\"}").build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/{id}/features/count")
    @Operation(summary = "Count features for a product")
    public Response countFeatures(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return Response.ok(db.queryCount("SELECT fn_count_product_features(?)", uuid)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Invalid product ID format\"}").build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
