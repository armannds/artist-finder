package com.armannds.artistfinder.rest;

import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.CoverIconService;
import com.armannds.artistfinder.service.DescriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.armannds.artistfinder.utils.JsonUtils.*;

@RestController
public class ArtistFinderController {

    private static final int VALID_MBID_LENGTH = 36;
    private static final String WIKIPEDIA = "wikipedia";

    private ArtistService artistService;
    private DescriptionService descriptionService;
    private CoverIconService coverIconService;

    @Autowired
    public ArtistFinderController(ArtistService artistService, DescriptionService descriptionService,
                                  CoverIconService coverIconService) {
        this.artistService = artistService;
        this.descriptionService = descriptionService;
        this.coverIconService = coverIconService;
    }

    @Async
    @RequestMapping(value = "/async/{mbid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public CompletableFuture<JsonNode> getArtistByMbidAsync(@PathVariable("mbid") String mbid) {
        return artistService.getArtistByIdAsync(mbid)
                .thenCompose(artist -> getDescriptionAsync(artist)
                        .thenCombine(getCoverArtIcons(artist),
                        (description, album) -> createResponse(
                                artist, description.orElse("Description not found!"), album)));
    }

    @RequestMapping(value = "/{mbid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public JsonNode getArtistByMbid(@PathVariable("mbid") String mbid) {
        JsonNode artist = artistService.getArtistById(mbid);
        Optional<String> description = getDescription(artist);
        List<JsonNode> albums = extractAlbums(artist)
                .map(album -> coverIconService.getCoverIconForAlbum(album))
                .collect(Collectors.toList());

        return createResponse(artist, description.orElse("Description not found!"), albums);
    }

    private CompletableFuture<Optional<String>> getDescriptionAsync(JsonNode artist) {
        return extractDescriptionName(artist)
                .map(descName -> descriptionService.getDescriptionAsync(descName))
                .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    private Optional<String> getDescription(JsonNode artist) {
        return extractDescriptionName(artist).flatMap(
                descName -> descriptionService.getDescriptionByName(descName));
    }

    private Optional<String> extractDescriptionName(JsonNode artist) {
        return createStream(artist.at("/relations"))
                .filter(relation -> WIKIPEDIA.equals(relation.at("/type").textValue()))
                .map(relation -> relation.at("/url/resource").textValue())
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .findFirst();
    }

    private CompletableFuture<List<JsonNode>> getCoverArtIcons(JsonNode artist) {
        return sequence(extractAlbums(artist)
                .map(album -> coverIconService.getCoverIconForAlbumAsync(album))
                .collect(Collectors.toList()));
    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    private Stream<JsonNode> extractAlbums(JsonNode artist) {
        return createStream(artist.at("/release-groups"))
                .filter(rg -> !StringUtils.isEmpty(rg.at("/id").textValue()))
                .filter(rg -> rg.at("/id").textValue().length() == 36)
                .filter(rg -> rg.at("/secondary-types").size() == 0);
    }

    private JsonNode createResponse(JsonNode artist, String description, List<JsonNode> album) {
        return createObjectNode()
                .put("id", getId(artist))
                .put("description", description)
                .set("albums", createArrayNode().addAll(album));
    }
}
