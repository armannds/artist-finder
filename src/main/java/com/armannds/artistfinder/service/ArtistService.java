package com.armannds.artistfinder.service;

import com.armannds.artistfinder.api.musicbrainz.MusicBrainzResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public interface ArtistService {

	Mono<MusicBrainzResponse> getArtistByIdRx(String id);

	CompletableFuture<MusicBrainzResponse> getArtistByIdAsync(String id);

	MusicBrainzResponse getArtistById(String id);
}
