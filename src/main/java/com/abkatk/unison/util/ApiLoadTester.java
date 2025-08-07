package com.abkatk.unison.util;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class ApiLoadTester {

    private static final String API_URL = "http://localhost:30090/user/find/id/1";
    private static final String AUTH_HEADER = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRJZCI6InNpbmNkZXYiLCJ1c2VybmFtZSI6ImFkbWluIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3NTQ1NjUxNzYsImV4cCI6MTc1NDcwOTE3Nn0.0_ci5ffHAe4617YlMm88xAVEXcH1Bfcoy88KbNLN6EI"; // truncated

    public static void main(String[] args) {
        WebClient webClient = WebClient.builder()
                .defaultHeader("Authorization", AUTH_HEADER)
                .defaultHeader("accept", "*/*")
                .build();

        int rps = 5000000;
        int concurrency = 1000; // control how many requests in parallel
        Duration period = Duration.ofSeconds(1);

        Flux.range(0, Integer.MAX_VALUE)
                .delayElements(period.dividedBy(rps))
                .flatMap(i -> webClient.get()
                        .uri(API_URL)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnNext(response -> {
                            // Optional: log success
                        })
                        .doOnError(error -> {
                            System.err.println("Request failed: " + error.getMessage());
                        })
                        .subscribeOn(Schedulers.boundedElastic()), concurrency)
                .subscribe();

        // Keep running
        try {
            Thread.sleep(60_000); // Run for 60 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
