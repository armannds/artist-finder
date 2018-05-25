package com.armannds.artistfinder.finder;

import com.armannds.artistfinder.api.musicbrainz.MusicBrainzResponse;
import com.armannds.artistfinder.api.musicbrainz.Relation;
import com.armannds.artistfinder.api.musicbrainz.ReleaseGroup;
import com.armannds.artistfinder.dto.Album;
import com.armannds.artistfinder.dto.Artist;
import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.CoverIconService;
import com.armannds.artistfinder.service.DescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toSet;

@Component
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

    public CompletableFuture<Artist> getArtistByIdAsync(String id) {
        return artistService.getArtistByIdAsync(id)
                .thenCompose(musicBrainzResponse ->
                        getDescriptionAsync(musicBrainzResponse.getRelations())
                                .thenCombine(getCoverArtIconsAsync(musicBrainzResponse.getAlbums()),
                                        (description, album) -> createResponse(
                                                musicBrainzResponse.getId(),
                                                description.orElse("Description not found!"),
                                                album)));
    }

    public Artist getArtistById(String id) {
        MusicBrainzResponse musicBrainzResponse = artistService.getArtistById(id);
        Optional<String> description = getDescription(musicBrainzResponse.getRelations());
        Set<Album> albums = getCoverArtIcons(musicBrainzResponse.getAlbums());
        return createResponse(musicBrainzResponse.getId(), description.orElse("Description not found!"), albums);
    }

    private CompletableFuture<Optional<String>> getDescriptionAsync(Set<Relation> relations) {
        return extractDescriptionName(relations)
                .map(descName -> descriptionService.getDescriptionAsync(descName))
                .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    private Optional<String> getDescription(Set<Relation> relations) {
        return extractDescriptionName(relations)
                .flatMap(artistName -> descriptionService.getDescription(artistName));
    }

    private Optional<String> extractDescriptionName(Set<Relation> relations) {
        return relations.stream()
                .filter(relation -> WIKIPEDIA.equals(relation.getType()))
                .map(Relation::getResource)
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .findFirst();
    }

    private CompletableFuture<Set<Album>> getCoverArtIconsAsync(Set<ReleaseGroup> releaseGroups) {
        return sequence(
                releaseGroups
                        .stream()
                        .map(rg -> coverIconService.getCoverIconForAlbumAsync(rg.getId(), rg.getTitle()))
                        .collect(toSet())
        );
    }

    private static <T> CompletableFuture<Set<T>> sequence(Set<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allDoneFuture.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(toSet()));
    }

    private Set<Album> getCoverArtIcons(Set<ReleaseGroup> releaseGroups) {
        return releaseGroups
                .parallelStream()
                .map(rg -> coverIconService.getCoverIconForAlbum(rg.getId(), rg.getTitle()))
                .collect(toSet());
    }

    private Artist createResponse(String id, String description, Collection<Album> album) {
        return new Artist.Builder()
                .id(id)
                .description(description)
                .albums(album)
                .build();
    }
}
