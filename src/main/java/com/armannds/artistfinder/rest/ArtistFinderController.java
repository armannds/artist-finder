package com.armannds.artistfinder.rest;

import com.armannds.artistfinder.data.Artist;
import com.armannds.artistfinder.finder.ArtistFinder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
public class ArtistFinderController {

    private static final int VALID_MBID_LENGTH = 36;
    private ArtistFinder artistFinder;

    @Autowired
    public ArtistFinderController(ArtistFinder artistFinder) {
        this.artistFinder = artistFinder;
    }

    @GetMapping(value = "/rx/{mbid}")
    public Mono<JsonNode> getArtistByMbidRx(@PathVariable("mbid") String mbid) {
        validateParameter(mbid);
        return Mono.empty();
    }

    @Async
    @GetMapping(value = "/async/{mbid}")
    public CompletableFuture<Artist> getArtistByMbidAsync(@PathVariable("mbid") String mbid) {
        validateParameter(mbid);
        return artistFinder.getArtistByIdAsync(mbid);
    }

    @GetMapping(value = "/{mbid}")
    public Artist getArtistByMbid(@PathVariable("mbid") String mbid) {
        validateParameter(mbid);
        return artistFinder.getArtistById(mbid);
    }

    private void validateParameter(String mbid) {
        if (StringUtils.isEmpty(mbid) || mbid.length() != VALID_MBID_LENGTH) {
            throw new IllegalArgumentException("MBID " +  mbid + " is not valid");
        }
    }
}
