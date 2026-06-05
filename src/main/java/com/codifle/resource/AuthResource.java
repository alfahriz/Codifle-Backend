package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Authentication")
public class AuthResource {

    @Inject DbService db;
    @Inject JsonWebToken jwt;

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://codifle.com")
    String issuer;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Login and receive JWT")
    public Response login(LoginRequest req) {
        if (req == null || req.username == null || req.password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"username and password are required\"}").build();
        }
        try {
            var user = db.querySingle(
                "SELECT * FROM fn_authenticate(?, ?)",
                req.username, req.password
            );

            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid credentials\"}").build();
            }

            String role     = String.valueOf(user.get("outRole"));
            String username = String.valueOf(user.get("outUsername"));
            String userId   = String.valueOf(user.get("outId"));

            String token = Jwt.issuer(issuer)
                .subject(userId)
                .claim("username", username)
                .groups(Set.of(role))
                .expiresIn(Duration.ofHours(8))
                .sign();

            return Response.ok(Map.of("token", token, "username", username, "role", role)).build();

        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    // Verify JWT is working — remove or keep as health check for auth
    @GET
    @Path("/me")
    @RolesAllowed("admin")
    @Operation(summary = "Get current authenticated user info")
    public Response me() {
        return Response.ok(Map.of(
            "subject",  jwt.getSubject(),
            "username", jwt.getClaim("username").toString(),
            "role",     jwt.getGroups().iterator().next()
        )).build();
    }

    public static class LoginRequest {
        @NotBlank @Size(max = 100)
        public String username;

        @NotBlank @Size(max = 100)
        public String password;
    }
}
