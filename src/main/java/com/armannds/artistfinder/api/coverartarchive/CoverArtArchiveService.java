package com.armannds.artistfinder.api.coverartarchive;

import com.armannds.artistfinder.service.AsyncService;
import com.armannds.artistfinder.service.CoverIconService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static com.armannds.artistfinder.utils.JsonUtils.createObjectNode;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class CoverArtArchiveService extends AsyncService<JsonNode> implements CoverIconService {

	private static final String COVERT_ART_ARCHIVE_URL = "http://coverartarchive.org/release-group/{id}";
	private static final String ERROR = "error";
	private static final String NOT_FOUND = "404 cover icon not found";
	private static final ObjectNode NOT_FOUND_ERROR_NODE = createObjectNode().put(ERROR, NOT_FOUND);

	@Autowired
	public CoverArtArchiveService(RestTemplate restTemplate) {
		super(restTemplate);
	}

	@Override
	public CompletableFuture<JsonNode> getCoverIconForAlbumAsync(final String id, final String title) {
		return getAsync(() -> restTemplate.getForObject(createUrl(id), JsonNode.class))
                .exceptionally(e -> NOT_FOUND_ERROR_NODE)
                .thenApply(result -> createCoverArt(id, title, result));
	}

    @Override
    public JsonNode getCoverIconForAlbum(String id, String title) {
        return createCoverArt(id, title, getCoverArt(id));
    }

    private JsonNode getCoverArt(String album) {
        try {
            return restTemplate.getForObject(createUrl(album), JsonNode.class);
        } catch (RestClientException e) {
            return NOT_FOUND_ERROR_NODE;
        }
    }

    private JsonNode createCoverArt(String id, String title, JsonNode coverIcon) {
        return createObjectNode()
                .put("id", id)
                .put("title", title)
                .set("image", coverIcon);
    }

    private String createUrl(String albumMbid) {
        return fromHttpUrl(COVERT_ART_ARCHIVE_URL)
                .buildAndExpand(albumMbid)
                .toUriString();
    }
}