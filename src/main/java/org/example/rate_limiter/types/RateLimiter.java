package org.example.rate_limiter.types;

import org.example.request.Request;

public interface RateLimiter {

    public RateLimitStatus shouldAllowRequest(Request request);
}
