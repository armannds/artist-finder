package com.armannds.artistfinder.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface ArtistService {

	CompletableFuture<JsonNode> getArtistByIdAsync(String id);

	JsonNode getArtistById(String id);
}
