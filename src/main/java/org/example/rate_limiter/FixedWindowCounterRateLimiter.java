package org.example.rate_limiter;

import org.example.rate_limiter.types.RateLimitStatus;
import org.example.rate_limiter.types.RateLimiter;
import org.example.request.Request;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FixedWindowCounterRateLimiter implements RateLimiter {
    private final int limit;
    private final long duration;
    private final AtomicInteger totalHits;
    private final AtomicLong resetTime;

    public FixedWindowCounterRateLimiter(int limit, long window, TemporalUnit unit) {
        this.limit = limit;
        this.duration = Duration.of(window, unit).toMillis();
        this.totalHits = new AtomicInteger(0);
        this.resetTime = new AtomicLong(Instant.now().toEpochMilli() + duration);
    }

    private synchronized void reset() {
        long currentTime = Instant.now().toEpochMilli();
        totalHits.set(0);
        resetTime.set(currentTime + duration);
    }

    @Override
    public synchronized RateLimitStatus shouldAllowRequest(Request request) {
        long currentTime = Instant.now().toEpochMilli();

        if(resetTime.get() < currentTime) {
            reset();
        }

        return totalHits.incrementAndGet() <= limit ? RateLimitStatus.ALLOWED : RateLimitStatus.REJECTED;
    }
}
