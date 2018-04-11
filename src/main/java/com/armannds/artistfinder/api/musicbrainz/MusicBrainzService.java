package com.armannds.artistfinder.api.musicbrainz;

import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.AsyncService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class MusicBrainzService extends AsyncService implements ArtistService {

    private static final String MUSICBRAINZ_ARTIST_URL = "http://musicBrainz.org/ws/2/artist/{mbid}";

    @Autowired
    public MusicBrainzService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public CompletableFuture<JsonNode> getArtistByIdAsync(String id) {
        return fetchArtistFromMusicBrainz(id);
    }

    @Override
    public JsonNode getArtistById(String id) {
        return restTemplate.getForObject(createUrl(id), JsonNode.class);
    }

    private CompletableFuture<JsonNode> fetchArtistFromMusicBrainz(String id) {
        return getAsync(createUrl(id));
    }

    private String createUrl(String id) {
        return fromHttpUrl(MUSICBRAINZ_ARTIST_URL)
                .queryParam("fmt", "json")
                .queryParam("inc", "url-rels+release-groups")
                .buildAndExpand(id)
                .toUriString();
    }
}