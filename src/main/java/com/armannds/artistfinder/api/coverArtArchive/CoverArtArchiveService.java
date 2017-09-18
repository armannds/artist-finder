package com.armannds.artistfinder.api.coverArtArchive;

import com.armannds.artistfinder.data.Album;
import com.armannds.artistfinder.errorhandling.CoverIconException;
import com.armannds.artistfinder.service.CoverIconService;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CoverArtArchiveService implements CoverIconService {

	private static final Logger LOG = LoggerFactory.getLogger(CoverArtArchiveService.class);

	private static final String COVERT_ART_ARCHIVE_URL = "http://coverartarchive.org/release-group/{id}";

	private static final int VALID_COVER_ART_ARCHIVE_ID = 36;

	private static final String IMAGE_NOT_FOUND = "404 Cover Image not found";

	private RestTemplate restTemplate;

	private ObjectMapper jsonObjectMapper;

	public CoverArtArchiveService(RestTemplate restTemplate, ObjectMapper jsonObjectMapper) {
		this.restTemplate = restTemplate;
		this.jsonObjectMapper = jsonObjectMapper;
	}

	@Async
	@Override
	public CompletableFuture<Set<Album>> getCoverIconForAlbums(Set<Album> albums) {
		LOG.debug("Starting asynchronous operations for cover art.");
		fetchAlbumsCoverIcon(albums);
		LOG.debug("Done asynchronous operations for cover art.");
		return CompletableFuture.completedFuture(albums);
	}

	private void fetchAlbumsCoverIcon(Set<Album> albums) {
		Set<Album> result = new HashSet<>();
		albums.parallelStream().forEach(album -> {
			if (isCoverArtArchiveIdValid(album.getId())) {
				String imageUrl = fetchCoverIconForId(album.getId());
				if (!StringUtils.isEmpty(imageUrl)) {
					album.setImage(imageUrl);
				} else {
					album.setImage(IMAGE_NOT_FOUND);
				}
			} else {
				LOG.warn("Cover Art Archive id <" + album.getId() + "> is not valid!");
			}
		});
	}

	private boolean isCoverArtArchiveIdValid(String id) {
		return !StringUtils.isEmpty(id) && id.length() == VALID_COVER_ART_ARCHIVE_ID;
	}

	//If we can't retrieve the image we will just log the issue and return null, that will skip this particular album
	private String fetchCoverIconForId(String id) {
		String result = null;
		try {
			result = fetchImageFromCoverArtArchive(id);
		} catch (CoverIconException e) {
			LOG.warn(e.getMessage());
		}
		return result;
	}

	//TODO This request needs to be asynchronous as well, use AsyncRestTemplate
	private String fetchImageFromCoverArtArchive(String id) throws CoverIconException {
		try {
			ResponseEntity<String> coverArtArchiveResponse = restTemplate.exchange(COVERT_ART_ARCHIVE_URL, HttpMethod.GET,
					new HttpEntity<>(new HttpHeaders()), String.class, id);
			return extractImageUrlFromCoverArtArchiveResponse(coverArtArchiveResponse.getBody());
		} catch (RestClientException e) {
			throw new CoverIconException("Could not get image with id <" + id + "> from Cover Art Archive due to: " + e.getMessage());
		}
	}

	//If we can't extract image from the response we will just log the issue and return empty string
	private String extractImageUrlFromCoverArtArchiveResponse(String coverArtArchiveResponse) {
		try {
			JsonNode root = jsonObjectMapper.readTree(coverArtArchiveResponse);
			return root.findValue("image").asText();
		} catch (IOException e) {
			LOG.warn("Could not extract image url from coverArtArchiveResponse due to " + e.getMessage());
		}
		return "";
	}
}