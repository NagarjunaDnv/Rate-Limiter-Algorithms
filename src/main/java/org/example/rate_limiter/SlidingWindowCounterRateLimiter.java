package org.example.rate_limiter;

import org.example.rate_limiter.types.RateLimitStatus;
import org.example.rate_limiter.types.RateLimiter;
import org.example.request.Request;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SlidingWindowCounterRateLimiter implements RateLimiter {

    private final int limit;
    private final long duration;

    private int previousWindowRequestCount = 0;
    private int currentWindowRequestCount = 0;
    private long resetTime;

    public SlidingWindowCounterRateLimiter(int limit, int window, ChronoUnit unit) {
        this.limit = limit;
        this.duration = Duration.of(window, unit).toMillis();
        this.resetTime = Instant.now().toEpochMilli() + duration;
    }


    private void reset() {
        long currentTime = Instant.now().toEpochMilli();
        this.previousWindowRequestCount = currentTime - resetTime > duration ? 0 : this.previousWindowRequestCount;
        this.currentWindowRequestCount = 0;
        this.resetTime = currentTime + duration;
    }

    @Override
    public synchronized RateLimitStatus shouldAllowRequest(Request request) {

        long currentTime = Instant.now().toEpochMilli();

        if(resetTime < currentTime) {
            reset();
        }

        currentWindowRequestCount++;

        // What percent of the timeframe we are in
        final double currentWindowWeight = (currentTime - (resetTime - duration)) / (double) duration;
        final double previousWindowWeight = 1d - currentWindowWeight;

        //Calculate the weightedCurrentRequestCount
        final double weightedCurrentRequestCount = previousWindowRequestCount*previousWindowWeight + currentWindowRequestCount;

        return weightedCurrentRequestCount <= limit ? RateLimitStatus.ALLOWED : RateLimitStatus.REJECTED;
    }
}
