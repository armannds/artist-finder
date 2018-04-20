package com.armannds.artistfinder.service;

import com.armannds.artistfinder.api.musicbrainz.MusicBrainzResponse;

import java.util.concurrent.CompletableFuture;

public interface ArtistService {

	CompletableFuture<MusicBrainzResponse> getArtistByIdAsync(String id);

	MusicBrainzResponse getArtistById(String id);
}
