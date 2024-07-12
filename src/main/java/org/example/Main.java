package org.example;

import org.example.rate_limiter.*;
import org.example.rate_limiter.types.RateLimiter;
import org.example.request.Request;
import org.example.request.RequestHandler;

import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {

//        testFixedWindowCounterRateLimiter();
//        testSlidingWindowLogRateLimiter();
//        testSlidingWindowCounterRateLimiter();
//        testTokenBucketRateLimiter();
//        testLeakyBucketRateLimiter();
    }


    private static void testFixedWindowCounterRateLimiter() throws InterruptedException {
        Request request = new Request();
        RateLimiter rateLimiter = new FixedWindowCounterRateLimiter(3, 1, ChronoUnit.MILLIS);
        RequestHandler requestHandler = new RequestHandler(rateLimiter, request);
        initiateRequests(requestHandler);
    }

    private static void testSlidingWindowLogRateLimiter() throws InterruptedException {
        Request request = new Request();
        RateLimiter rateLimiter = new SlidingWindowLogRateLimiter(10, 1, ChronoUnit.SECONDS);
        RequestHandler requestHandler = new RequestHandler(rateLimiter, request);
        initiateRequests(requestHandler);
    }


    private static void testSlidingWindowCounterRateLimiter() throws InterruptedException {
        Request request = new Request();
        RateLimiter rateLimiter = new SlidingWindowCounterRateLimiter(5, 1, ChronoUnit.MILLIS);
        RequestHandler requestHandler = new RequestHandler(rateLimiter, request);
        initiateRequests(requestHandler);
    }

    private static void testTokenBucketRateLimiter() throws InterruptedException {
        Request request = new Request();
        RateLimiter rateLimiter = new TokenBucketRateLimiter(5, 1, ChronoUnit.MILLIS);
        RequestHandler requestHandler = new RequestHandler(rateLimiter, request);
        initiateRequests(requestHandler);
    }

    private static void testLeakyBucketRateLimiter() throws InterruptedException {
        Request request = new Request();
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(5, 1);
        RequestHandler requestHandler = new RequestHandler(rateLimiter, request);
        initiateRequests(requestHandler);
    }

    private static void initiateRequests(RequestHandler requestHandler) throws InterruptedException {
        var t1 = new Thread(() -> {
            for(int i = 0; i < 100; i++) {
                requestHandler.execute();
            }
        });

        var t2 = new Thread(() -> {
            for(int i = 0; i < 100; i++) {
                requestHandler.execute();
            }
        });

        t1.start();
        t2.start();

        t2.join();
        t2.join();
    }
}