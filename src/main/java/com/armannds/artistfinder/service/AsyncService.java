package com.armannds.artistfinder.service;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public abstract class AsyncService <T> {

    protected final RestTemplate restTemplate;

    public AsyncService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected CompletableFuture<T> getAsync(Supplier<T> supplier) {
        return supplyAsync(supplier);
    }
}
