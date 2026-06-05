package com.codifle.util;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;

public final class ApiResponse {

    private ApiResponse() {}

    public static Response serverError(Exception e) {
        Log.error("Unhandled error", e);
        return Response.serverError()
            .entity("{\"error\":\"Internal server error\"}")
            .header("Content-Type", "application/json")
            .build();
    }
}
