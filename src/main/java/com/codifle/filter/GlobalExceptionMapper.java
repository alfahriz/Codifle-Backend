package com.codifle.filter;

import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {
        if (e instanceof WebApplicationException wae) {
            return wae.getResponse();
        }
        Log.error("Unhandled exception", e);
        return Response.serverError()
            .entity("{\"error\":\"Internal server error\"}")
            .header("Content-Type", "application/json")
            .build();
    }
}
