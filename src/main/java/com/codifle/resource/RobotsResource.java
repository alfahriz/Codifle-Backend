package com.codifle.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/robots.txt")
public class RobotsResource {

    @ConfigProperty(name = "codifle.site.base-url", defaultValue = "https://codifle.com")
    String baseUrl;

    @GET
    @Produces("text/plain")
    public Response get() {
        String body = "User-agent: *\n"
            + "Allow: /\n"
            + "Disallow: /swagger-ui\n"
            + "Disallow: /openapi\n"
            + "Disallow: /api/contact/submit\n"
            + "\n"
            + "Sitemap: " + baseUrl + "/sitemap.xml\n";
        return Response.ok(body).build();
    }
}
