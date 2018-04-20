package com.armannds.artistfinder.rest;

import com.armannds.artistfinder.finder.ArtistFinder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
public class ArtistFinderController {

    private ArtistFinder artistFinder;

    @Autowired
    public ArtistFinderController(ArtistFinder artistFinder) {
        this.artistFinder = artistFinder;
    }

    @GetMapping(value = "/rx/{mbid}")
    public Mono<JsonNode> getArtistByMbidRx(@PathVariable("mbid") String mbid) {
        return Mono.empty();
    }

    @Async
    @GetMapping(value = "/async/{mbid}")
    public CompletableFuture<JsonNode> getArtistByMbidAsync(@PathVariable("mbid") String mbid) {
        return artistFinder.getArtistByIdAsync(mbid);
    }

    @GetMapping(value = "/{mbid}")
    public JsonNode getArtistByMbid(@PathVariable("mbid") String mbid) {
        return artistFinder.getArtistById(mbid);
    }
}
