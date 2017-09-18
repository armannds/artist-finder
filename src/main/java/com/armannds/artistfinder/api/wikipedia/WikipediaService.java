package com.armannds.artistfinder.api.wikipedia;

import com.armannds.artistfinder.data.Relation;
import com.armannds.artistfinder.errorhandling.DescriptionException;
import com.armannds.artistfinder.service.DescriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WikipediaService implements DescriptionService {

	private static final Logger LOG = LoggerFactory.getLogger(WikipediaService.class);

    private static final String WIKIPEDIA_DESCRIPTION_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles={artistName}";

    private static final String WIKIPEDIA = "wikipedia";

    private final RestTemplate restTemplate;

    private final ObjectMapper jsonObjectMapper;

    public WikipediaService(RestTemplate restTemplate, ObjectMapper jsonObjectMapper) {
        this.restTemplate = restTemplate;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Async
    @Override
    public CompletableFuture<String> getDescriptionFromRelations(Set<Relation> relations) throws DescriptionException {
		LOG.debug("Starting asynchronous opertations.");
    	String resourceUrl = getArtistDescriptionResourceUrl(relations);
    	if (isResourceUrlValid(resourceUrl)) {
			String artistName = extractArtistNameFromResourceUrl(resourceUrl);
			String wikipediaDescription = fetchArtistDescription(artistName);
			return CompletableFuture.completedFuture(wikipediaDescription);
		} else {
			throw new DescriptionException("Resource url <" + resourceUrl + "> for wikipediaService is not valid!");
		}
    }

	private String getArtistDescriptionResourceUrl(Set<Relation> relations) {
		return relations.stream()
				.filter(relation -> relation.getType().equals(WIKIPEDIA))
				.map(Relation::getResource).collect(Collectors.toList()).get(0);
	}

	//Standard validation to prevent nullpointer exceptions.
	private boolean isResourceUrlValid(String resourceUrl) {
    	return !StringUtils.isEmpty(resourceUrl) && resourceUrl.contains("/");
    }

	private String extractArtistNameFromResourceUrl(String resourceUrl) {
		return resourceUrl.substring(resourceUrl.lastIndexOf("/") + 1, resourceUrl.length());
	}

	//If something fails while getting the description from wikipedia we will just log the error and return empty String.
	private String fetchArtistDescription(String artistName) {
		try {
			return fetchDescriptionFromWikipedia(artistName);
		} catch (DescriptionException e) {
			LOG.warn(e.getMessage());
		}
		return "";
	}

	private String fetchDescriptionFromWikipedia(String artistName) throws DescriptionException{
    	try {
		    ResponseEntity<String> wikipediaResponse = restTemplate.exchange(WIKIPEDIA_DESCRIPTION_URL, HttpMethod.GET,
				    new HttpEntity<>(new HttpHeaders()), String.class, artistName);
		    return extractDescriptionFromJsonResponse(wikipediaResponse.getBody());
	    } catch (RestClientException e) {
    		throw new DescriptionException("Could not get description for artist with name <" + artistName + "> due to: " + e.getMessage());
	    }
	}

    private String extractDescriptionFromJsonResponse(String jsonResponse) {
        try {
            JsonNode root = jsonObjectMapper.readTree(jsonResponse);
            return root.findValue("extract").asText();
        } catch (IOException e) {
            LOG.warn("Could not extract description(<extract>) from wikipediaResponse due to " + e.getMessage());
        }
        return "";
    }
}