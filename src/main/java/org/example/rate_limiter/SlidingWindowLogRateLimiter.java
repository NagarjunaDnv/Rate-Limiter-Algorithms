package org.example.rate_limiter;

import org.example.rate_limiter.types.RateLimitStatus;
import org.example.rate_limiter.types.RateLimiter;
import org.example.request.Request;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

public class SlidingWindowLogRateLimiter implements RateLimiter {
    private final int limit;
    private final long duration;
    PriorityQueue<Long> timeStampsOfRequests = new PriorityQueue<>();

    public SlidingWindowLogRateLimiter(int limit, int window, ChronoUnit unit) {
        this.limit = limit;
        this.duration = Duration.of(window, unit).toNanos();
    }

    @Override
    public synchronized RateLimitStatus shouldAllowRequest(Request request) {

        long currentTime = Instant.now().toEpochMilli();
        timeStampsOfRequests.add(currentTime);

        long cutOffTime = currentTime - duration;

        while(!timeStampsOfRequests.isEmpty() && timeStampsOfRequests.peek() < cutOffTime) {
            timeStampsOfRequests.poll();
        }

        return timeStampsOfRequests.size() <= limit ? RateLimitStatus.ALLOWED : RateLimitStatus.REJECTED;
    }
}
