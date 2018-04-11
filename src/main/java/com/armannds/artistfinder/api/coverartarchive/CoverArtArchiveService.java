package com.armannds.artistfinder.api.coverartarchive;

import com.armannds.artistfinder.service.AsyncService;
import com.armannds.artistfinder.service.CoverIconService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static com.armannds.artistfinder.utils.JsonUtils.*;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;


public class CoverArtArchiveService extends AsyncService implements CoverIconService {

	private static final String COVERT_ART_ARCHIVE_URL = "http://coverartarchive.org/release-group/{id}";

	public CoverArtArchiveService(RestTemplate restTemplate) {
		super(restTemplate);
	}

	@Override
	public CompletableFuture<JsonNode> getCoverIconForAlbumAsync(JsonNode album) {
		return getAsync(createUrl(getId(album)))
                .exceptionally(e -> createObjectNode()
                        .set("Error", createTextNode(e.getMessage())))
                .thenApply(result -> createCoverArt(album, result));
	}

    @Override
    public JsonNode getCoverIconForAlbum(JsonNode album) {
        JsonNode response = getCoverArt(album);
        return createCoverArt(album, response);
    }

    private JsonNode getCoverArt(JsonNode album) {
        try {
            return restTemplate.getForObject(createUrl(getId(album)), JsonNode.class);
        } catch (RestClientException e) {
            return createObjectNode().put("image", "404 not found");
        }
    }

    private JsonNode createCoverArt(JsonNode album, JsonNode coverIcon) {
	    return createObjectNode()
                .put("id", getId(album))
                .put("title", album.at("/title").textValue())
                .set("image", coverIcon);
    }

    private String createUrl(String albumMbid) {
        return fromHttpUrl(COVERT_ART_ARCHIVE_URL)
                .buildAndExpand(albumMbid)
                .toUriString();
    }
}