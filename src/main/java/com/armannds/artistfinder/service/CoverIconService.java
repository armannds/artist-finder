package com.armannds.artistfinder.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.CompletableFuture;

public interface CoverIconService {

	CompletableFuture<JsonNode> getCoverIconForAlbumAsync(String id, String title);

	JsonNode getCoverIconForAlbum(String id, String title);
}
