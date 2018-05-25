package com.armannds.artistfinder.service;

import com.armannds.artistfinder.data.Album;

import java.util.concurrent.CompletableFuture;

public interface CoverIconService {

	CompletableFuture<Album> getCoverIconForAlbumAsync(String id, String title);

	Album getCoverIconForAlbum(String id, String title);
}
