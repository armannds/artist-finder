package com.armannds.artistfinder.api.musicbrainz;

import com.armannds.artistfinder.rest.errorhandling.ResourceNotFoundException;
import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class MusicBrainzService extends AsyncService<MusicBrainzResponse> implements ArtistService {

    private static final String MUSICBRAINZ_ARTIST_URL = "http://musicBrainz.org/ws/2/artist/{mbid}";

    @Autowired
    public MusicBrainzService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public Mono<MusicBrainzResponse> getArtistByIdRx(String id) {
        return WebClient.create().get().uri(createUrl(id)).accept(APPLICATION_JSON).exchange().then(re);

    }

    @Override
    public CompletableFuture<MusicBrainzResponse> getArtistByIdAsync(String id) {
        try {
            return getAsync(() -> restTemplate.getForObject(createUrl(id), MusicBrainzResponse.class));
        } catch (RestClientException e) {
            throw new ResourceNotFoundException("Artist with mbid " + id + " not found");
        }
    }

    @Override
    public MusicBrainzResponse getArtistById(String id) {
        try {
            return restTemplate.getForObject(createUrl(id), MusicBrainzResponse.class);
        } catch (RestClientException e) {
            throw new ResourceNotFoundException("Artist with mbid " + id + " not found");
        }
    }

    private String createUrl(String id) {
        return fromHttpUrl(MUSICBRAINZ_ARTIST_URL)
                .queryParam("fmt", "json")
                .queryParam("inc", "url-rels+release-groups")
                .buildAndExpand(id)
                .toUriString();
    }

    private URI createUrl2(String id) {
        return fromHttpUrl(MUSICBRAINZ_ARTIST_URL)
                .queryParam("fmt", "json")
                .queryParam("inc", "url-rels+release-groups")
                .buildAndExpand(id)
                .toUri();
    }
}