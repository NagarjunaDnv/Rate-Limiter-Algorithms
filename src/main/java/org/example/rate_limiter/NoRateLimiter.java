package org.example.rate_limiter;

import org.example.rate_limiter.types.RateLimitStatus;
import org.example.rate_limiter.types.RateLimiter;
import org.example.request.Request;

public class NoRateLimiter implements RateLimiter {
    @Override
    public RateLimitStatus shouldAllowRequest(Request request) {
        return RateLimitStatus.ALLOWED;
    }
}
