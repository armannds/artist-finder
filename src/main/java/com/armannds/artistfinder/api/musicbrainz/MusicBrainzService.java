package com.armannds.artistfinder.api.musicbrainz;

import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class MusicBrainzService extends AsyncService<MusicBrainzResponse> implements ArtistService {

    private static final String MUSICBRAINZ_ARTIST_URL = "http://musicBrainz.org/ws/2/artist/{mbid}";

    @Autowired
    public MusicBrainzService(RestTemplate restTemplate) {
        super(restTemplate);
    }

//    @Override
//    protected CompletableFuture<MusicBrainzResponse> getAsync(String url) {
//        return supplyAsync(() -> restTemplate.getForObject(url, MusicBrainzResponse.class));
//    }

    @Override
    public CompletableFuture<MusicBrainzResponse> getArtistByIdAsync(String id) {
        return fetchArtistFromMusicBrainz(id);
    }

    @Override
    public MusicBrainzResponse getArtistById(String id) {
        return restTemplate.getForObject(createUrl(id), MusicBrainzResponse.class);
    }

    private CompletableFuture<MusicBrainzResponse> fetchArtistFromMusicBrainz(String id) {
        return getAsync(() -> restTemplate.getForObject(createUrl(id), MusicBrainzResponse.class));
    }

    private String createUrl(String id) {
        return fromHttpUrl(MUSICBRAINZ_ARTIST_URL)
                .queryParam("fmt", "json")
                .queryParam("inc", "url-rels+release-groups")
                .buildAndExpand(id)
                .toUriString();
    }
}