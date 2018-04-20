package com.armannds.artistfinder.finder;

import com.armannds.artistfinder.api.musicbrainz.MusicBrainzResponse;
import com.armannds.artistfinder.data.Album;
import com.armannds.artistfinder.data.Relation;
import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.CoverIconService;
import com.armannds.artistfinder.service.DescriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.armannds.artistfinder.utils.JsonUtils.createArrayNode;
import static com.armannds.artistfinder.utils.JsonUtils.createObjectNode;
import static java.util.stream.Collectors.toSet;

public class ArtistFinder {

    private static final String WIKIPEDIA = "wikipedia";

    private ArtistService artistService;
    private DescriptionService descriptionService;
    private CoverIconService coverIconService;

    @Autowired
    public ArtistFinder(ArtistService artistService, DescriptionService descriptionService,
                        CoverIconService coverIconService) {
        this.artistService = artistService;
        this.descriptionService = descriptionService;
        this.coverIconService = coverIconService;
    }

    public CompletableFuture<JsonNode> getArtistByIdAsync(String id) {
        return artistService.getArtistByIdAsync(id)
                .thenCompose(artist -> getDescriptionAsync(artist.getRelations())
                        .thenCombine(getCoverArtIconsAsync(artist.getAlbums()),
                                (description, album) -> createResponse(
                                        artist.getId(), description.orElse("Description not found!"), album)));
    }

    public JsonNode getArtistById(String id) {
        MusicBrainzResponse artist = artistService.getArtistById(id);
        Optional<String> description = getDescription(artist.getRelations());
        Set<JsonNode> albums = getCoverArtIcons(artist.getAlbums());
        return createResponse(artist.getId(), description.orElse("Description not found!"), albums);
    }

    private CompletableFuture<Optional<String>> getDescriptionAsync(Set<Relation> relations) {
        return extractDescriptionName(relations)
                .map(descName -> descriptionService.getDescriptionAsync(descName))
                .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    private Optional<String> getDescription(Set<Relation> relations) {
        return extractDescriptionName(relations)
                .flatMap(artistName -> descriptionService.getDescriptionByName(artistName));
    }

    private Optional<String> extractDescriptionName(Set<Relation> relations) {
        return relations.stream()
                .filter(relation -> WIKIPEDIA.equals(relation.getType()))
                .map(Relation::getResource)
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .findFirst();
    }

    private CompletableFuture<Set<JsonNode>> getCoverArtIconsAsync(Set<Album> albums) {
        return sequence(albums.stream()
                .map(album -> coverIconService.getCoverIconForAlbumAsync(album.getId(), album.getTitle()))
                .collect(toSet()));
    }

    private static <T> CompletableFuture<Set<T>> sequence(Set<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(CompletableFuture::join)
                        .collect(toSet()));
    }

    private Set<JsonNode> getCoverArtIcons(Set<Album> albums) {
        return albums
                .parallelStream()
                .map(album -> coverIconService.getCoverIconForAlbum(album.getId(), album.getTitle()))
                .collect(toSet());
    }

    private JsonNode createResponse(String artist, String description, Collection<JsonNode> album) {
        return createObjectNode()
                .put("id", artist)
                .put("description", description)
                .set("albums", createArrayNode().addAll(album));
    }
}
