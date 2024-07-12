package org.example.request;

import org.example.rate_limiter.LeakyBucketRateLimiter;
import org.example.rate_limiter.types.RateLimitStatus;
import org.example.rate_limiter.types.RateLimiter;

import java.time.Instant;

public class RequestHandler {
    private final RateLimiter rateLimiter;
    private final Request request;

    public RequestHandler(RateLimiter rateLimiter, Request request) {
        this.rateLimiter = rateLimiter;
        this.request = request;
    }

    public void execute() {

        RateLimitStatus rateLimitStatus = rateLimiter.shouldAllowRequest(request);

        switch (rateLimitStatus) {
            case ALLOWED -> {
                System.out.printf("Request Executed: %s%n", Instant.now().toEpochMilli());
            }
            case QUEUED -> {
                System.out.printf("Request Queued: %s%n", Instant.now().toEpochMilli());
            }
            case REJECTED -> {
                System.out.printf("Rate limited: %s%n", Instant.now().toEpochMilli());
            }
        }
    }
}
