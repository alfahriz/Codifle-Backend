package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

@Path("/api/contact")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Contact", description = "Contact section and form submission")
public class ContactResource {

    @Inject DbService db;
    @Inject DataSource dataSource;

    @GET
    @Operation(summary = "Get contact section content")
    public Response getSection() {
        try {
            var data = db.querySingle("SELECT * FROM fn_get_contact_section()");
            if (data == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(data).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    @POST
    @Path("/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Submit contact form")
    public Response submit(ContactRequest req) {
        if (req == null || req.fullName == null || req.email == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"fullName and email are required\"}").build();
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT fn_submit_contact(?, ?, ?, ?, ?)")) {
            ps.setString(1, req.fullName);
            ps.setString(2, req.email);
            ps.setString(3, req.company);
            ps.setString(4, req.serviceInterest);
            ps.setString(5, req.message);
            ResultSet rs = ps.executeQuery();
            String newId = rs.next() ? rs.getString(1) : null;
            return Response.status(Response.Status.CREATED)
                .entity(java.util.Map.of("id", newId)).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    public static class ContactRequest {
        @NotBlank
        @Size(max = 100)
        public String fullName;

        @NotBlank
        @Email
        @Size(max = 254)
        public String email;

        @Size(max = 100)
        public String company;

        @Size(max = 100)
        public String serviceInterest;

        @Size(max = 2000)
        public String message;
    }
}
