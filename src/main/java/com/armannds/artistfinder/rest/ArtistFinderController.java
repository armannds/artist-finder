package com.armannds.artistfinder.rest;

import com.armannds.artistfinder.finder.ArtistFinder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class ArtistFinderController {

    private ArtistFinder artistFinder;

    @Autowired
    public ArtistFinderController(ArtistFinder artistFinder) {
        this.artistFinder = artistFinder;
    }

    @Async
    @RequestMapping(value = "/async/{mbid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public CompletableFuture<JsonNode> getArtistByMbidAsync(@PathVariable("mbid") String mbid) {
        return artistFinder.getArtistByIdAsync(mbid);
    }

    @RequestMapping(value = "/{mbid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public JsonNode getArtistByMbid(@PathVariable("mbid") String mbid) {
        return artistFinder.getArtistById(mbid);
    }
}
