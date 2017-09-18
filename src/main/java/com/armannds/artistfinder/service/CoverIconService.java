package com.armannds.artistfinder.service;

import com.armannds.artistfinder.data.Album;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface CoverIconService {

	CompletableFuture<Set<Album>> getCoverIconForAlbums(Set<Album> albums);
}
