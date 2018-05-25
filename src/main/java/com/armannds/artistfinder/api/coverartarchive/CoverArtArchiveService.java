package com.armannds.artistfinder.api.coverartarchive;

import com.armannds.artistfinder.dto.Album;
import com.armannds.artistfinder.service.AsyncService;
import com.armannds.artistfinder.service.CoverIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static com.armannds.artistfinder.api.coverartarchive.NotFoundCoverArtArchiveResponse.NOT_FOUND;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class CoverArtArchiveService extends AsyncService<CoverArtArchiveResponse> implements CoverIconService {

	private static final String COVERT_ART_ARCHIVE_URL = "http://coverartarchive.org/release-group/{id}";
	private static final CoverArtArchiveResponse NOT_FOUND_RESPONSE = new NotFoundCoverArtArchiveResponse();

	@Autowired
	public CoverArtArchiveService(RestTemplate restTemplate) {
		super(restTemplate);
	}

	@Override
	public CompletableFuture<Album> getCoverIconForAlbumAsync(final String id, final String title) {
		return getAsync(() -> restTemplate.getForObject(createUrl(id), CoverArtArchiveResponse.class))
                .exceptionally(e -> NOT_FOUND_RESPONSE)
                .thenApply(result -> createCoverArt(id, title, result));
	}

    @Override
    public Album getCoverIconForAlbum(String id, String title) {
	    CoverArtArchiveResponse response = getCoverArt(id);
	    return createCoverArt(id, title, response);
    }

    private CoverArtArchiveResponse getCoverArt(String id) {
        try {
            return restTemplate.getForObject(createUrl(id), CoverArtArchiveResponse.class);
        } catch (RestClientException e) {
            return NOT_FOUND_RESPONSE;
        }
    }

    private Album createCoverArt(String id, String title, CoverArtArchiveResponse coverArtArchiveResponse) {
	    return coverArtArchiveResponse.getImages()
                .stream()
                .map(CoverArtArchiveImage::getImage)
                .findFirst()
                .map(coverIcon -> createAlbum(id, title, coverIcon))
                .orElse(createAlbum(id, title, NOT_FOUND));
    }

    private Album createAlbum(String id, String title, String coverIcon) {
        return new Album.Builder()
                .id(id)
                .title(title)
                .image(coverIcon)
                .build();
    }

    private String createUrl(String id) {
        return fromHttpUrl(COVERT_ART_ARCHIVE_URL)
                .buildAndExpand(id)
                .toUriString();
    }
}