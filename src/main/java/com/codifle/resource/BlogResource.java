package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/blog")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Blog", description = "Blog articles")
public class BlogResource {

    @Inject DbService db;

    @GET
    @Path("/section")
    @Operation(summary = "Get blog section metadata")
    public Response getSection() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_blog_section()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/articles")
    @Operation(summary = "Get paginated articles, optional filter by category")
    public Response getArticles(
            @QueryParam("limit")    @DefaultValue("12") int limit,
            @QueryParam("offset")   @DefaultValue("0")  int offset,
            @QueryParam("category") String category) {
        try {
            return category != null && !category.isBlank()
                ? Response.ok(db.queryList("SELECT * FROM fn_get_articles(?, ?, ?)", limit, offset, category)).build()
                : Response.ok(db.queryList("SELECT * FROM fn_get_articles(?, ?)", limit, offset)).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/articles/count")
    @Operation(summary = "Count published articles, optional filter by category")
    public Response countArticles(@QueryParam("category") String category) {
        try {
            return category != null && !category.isBlank()
                ? Response.ok(db.queryCount("SELECT fn_count_articles(?)", category)).build()
                : Response.ok(db.queryCount("SELECT fn_count_articles()")).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @GET
    @Path("/articles/{slug}")
    @Operation(summary = "Get single article by slug")
    public Response getArticle(@PathParam("slug") String slug) {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_article(?)", slug);
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }
}
