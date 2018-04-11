package com.armannds.artistfinder.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DescriptionService {

    CompletableFuture<Optional<String>> getDescriptionAsync(String artistName);

    Optional<String> getDescriptionByName(String artistName);
}
