package com.codifle.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Provider
public class RateLimitFilter implements ContainerRequestFilter {

    private static final int GENERAL_RPM   = 120;
    private static final int SENSITIVE_RPM = 5;

    private final ConcurrentHashMap<String, Bucket> generalBuckets   = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> sensitiveBuckets = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext ctx) {
        String ip   = resolveIp(ctx);
        String path = ctx.getUriInfo().getPath();

        Bucket bucket = isSensitive(path)
            ? sensitiveBuckets.computeIfAbsent(ip, k -> newBucket(SENSITIVE_RPM))
            : generalBuckets.computeIfAbsent(ip, k -> newBucket(GENERAL_RPM));

        if (!bucket.tryConsume(1)) {
            ctx.abortWith(Response.status(429)
                .entity("{\"error\":\"Too many requests. Please slow down.\"}")
                .header("Content-Type", "application/json")
                .header("Retry-After", "60")
                .build());
        }
    }

    private boolean isSensitive(String path) {
        return path.contains("/contact/submit")
            || path.contains("/auth/login");
    }

    private String resolveIp(ContainerRequestContext ctx) {
        String forwarded = ctx.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = ctx.getHeaderString("X-Real-IP");
        return realIp != null ? realIp : "unknown";
    }

    private Bucket newBucket(int rpm) {
        Bandwidth limit = Bandwidth.classic(rpm, Refill.intervally(rpm, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
