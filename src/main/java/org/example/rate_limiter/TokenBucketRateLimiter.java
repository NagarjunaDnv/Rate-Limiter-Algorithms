package org.example.rate_limiter;

import org.example.rate_limiter.types.RateLimitStatus;
import org.example.rate_limiter.types.RateLimiter;
import org.example.request.Request;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TokenBucketRateLimiter implements RateLimiter {
    private final long bucketCapacity;
    private final long refillRate;
    private final long refillRateUnitInMilli;
    private long tokensInBucket;
    private volatile long lastRefillTimeStamp;

    public TokenBucketRateLimiter(long bucketCapacity, long refillRate, ChronoUnit refillRateUnit) {
        this.bucketCapacity = bucketCapacity;
        this.refillRateUnitInMilli = Duration.of(1, refillRateUnit).toMillis();
        this.refillRate = refillRate;
        this.tokensInBucket = bucketCapacity;
        this.lastRefillTimeStamp = Instant.now().toEpochMilli();
    }

    private void refill(long refillCount) {
        tokensInBucket = Math.min(tokensInBucket + refillCount, bucketCapacity);
        lastRefillTimeStamp = Instant.now().toEpochMilli();
    }

    @Override
    public synchronized RateLimitStatus shouldAllowRequest(Request request) {

        long currentTime = Instant.now().toEpochMilli();
        long refillCount = ((currentTime - lastRefillTimeStamp) / refillRateUnitInMilli)*refillRate;

        if(refillCount > 0) {
            refill(refillCount);
        }

        if(tokensInBucket > 0) {
            tokensInBucket--;
            return RateLimitStatus.ALLOWED;
        }

        return RateLimitStatus.REJECTED;
    }
}
