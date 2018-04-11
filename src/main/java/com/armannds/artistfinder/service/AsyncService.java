package com.armannds.artistfinder.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public abstract class AsyncService {

    protected final RestTemplate restTemplate;

    public AsyncService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected CompletableFuture<JsonNode> getAsync(final String url) {
        return supplyAsync(() -> restTemplate.getForObject(url, JsonNode.class));
    }
}
