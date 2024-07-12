package org.example.rate_limiter;

import org.example.rate_limiter.types.RateLimitStatus;
import org.example.rate_limiter.types.RateLimiter;
import org.example.request.Request;
import org.example.request.RequestHandler;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeakyBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final ConcurrentLinkedDeque<Request> queue;
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public LeakyBucketRateLimiter(int capacity, long leakRateInMilli) {
        this.capacity = capacity;
        this.queue = new ConcurrentLinkedDeque<>();

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            Request request = queue.poll();
            if(request != null) {
                RequestHandler requestHandler = new RequestHandler(new NoRateLimiter(), request);
                requestHandler.execute();
            }

        }, 0, leakRateInMilli, TimeUnit.MILLISECONDS);
    };

    @Override
    public RateLimitStatus shouldAllowRequest(Request request) {

        if(queue.size() >= capacity) {
            return RateLimitStatus.REJECTED;
        }

        queue.offer(request);
        return RateLimitStatus.QUEUED;
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
    }
}
