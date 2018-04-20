package com.armannds.artistfinder.api.wikipedia;

import com.armannds.artistfinder.service.AsyncService;
import com.armannds.artistfinder.service.DescriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;


public class WikipediaService extends AsyncService<JsonNode> implements DescriptionService {

    private static final String WIKIPEDIA_DESCRIPTION_URL = "https://en.wikipedia.org/w/api.php";

    public WikipediaService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public CompletableFuture<Optional<String>> getDescriptionAsync(String artistName) {
    	return getAsync(() -> restTemplate.getForObject(createUrl(artistName), JsonNode.class))
				.thenApply(this::getDescription);
    }

    @Override
    public Optional<String> getDescriptionByName(String artistName) {
        JsonNode response = restTemplate.getForObject(createUrl(artistName), JsonNode.class);
        return getDescription(response);
    }

    private Optional<String> getDescription(JsonNode result) {
		return Optional.of(result.at("/query/pages"))
				.filter(pages -> pages.fieldNames().hasNext())
				.map(pages -> pages.get(pages.fieldNames().next()).at("/extract").textValue());
	}

	private String createUrl(String artistName) {
		return fromHttpUrl(WIKIPEDIA_DESCRIPTION_URL)
				.queryParam("action", "query")
				.queryParam("format", "json")
				.queryParam("prop", "extracts")
				.queryParam("exintro", "true")
				.queryParam("redirects", "true")
				.queryParam("titles", "{artistName}")
				.buildAndExpand(artistName).toUriString();
	}
}