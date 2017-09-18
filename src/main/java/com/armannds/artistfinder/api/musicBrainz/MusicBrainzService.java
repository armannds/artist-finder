package com.armannds.artistfinder.api.musicBrainz;

import com.armannds.artistfinder.data.Album;
import com.armannds.artistfinder.data.Artist;
import com.armannds.artistfinder.errorhandling.DescriptionException;
import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.CoverIconService;
import com.armannds.artistfinder.service.DescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class MusicBrainzService implements ArtistService {

	private static final Logger LOG = LoggerFactory.getLogger(MusicBrainzService.class);

	private static final String MUSICBRAINZ_ARTIST_URL = "http://musicBrainz.org/ws/2/artist/{mbid}?&fmt=json&inc=url-rels+release-groups";

	private final RestTemplate restTemplate;

	private final DescriptionService descriptionService;

	private final CoverIconService coverIconService;

	public MusicBrainzService(RestTemplate restTemplate, DescriptionService descriptionService, CoverIconService coverIconService) {
		this.restTemplate = restTemplate;
		this.descriptionService = descriptionService;
		this.coverIconService = coverIconService;
	}

	//TODO the get() methods are blocking the async process, need to find a better solution
	@Override
	public Artist getArtistById(String id) throws ExecutionException, InterruptedException {
		MusicBrainzResponse mbResponse = fetchArtistFromMusicBrainz(id);
		CompletableFuture<String> description = fetchArtistDescription(mbResponse);
		CompletableFuture<Set<Album>> albums = fetchArtistAlbums(mbResponse);
		return createArtist(mbResponse, description.get(), albums.get());
	}

	private MusicBrainzResponse fetchArtistFromMusicBrainz(String id) {
		try {
			ResponseEntity<MusicBrainzResponse> responseEntity = restTemplate.exchange(MUSICBRAINZ_ARTIST_URL, HttpMethod.GET,
					new HttpEntity<>(new HttpHeaders()), MusicBrainzResponse.class, id);
			return responseEntity.getBody();

		} catch (RestClientException rce) {
			LOG.error(rce.getMessage());
		}
		return null;
	}

	private CompletableFuture<String> fetchArtistDescription(MusicBrainzResponse mbResponse) {
		try {
			return descriptionService.getDescriptionFromRelations(mbResponse.getRelations());
		} catch (DescriptionException e) {
			LOG.warn(e.getMessage());
		}
		return CompletableFuture.completedFuture("");
	}

	private CompletableFuture<Set<Album>> fetchArtistAlbums(MusicBrainzResponse mbResponse) {
		return coverIconService.getCoverIconForAlbums(mbResponse.getAlbums());
	}

	private Artist createArtist(MusicBrainzResponse mbResponse, String description, Set<Album> albums) {
		Artist artist = new Artist();
		artist.setMkid(mbResponse.getId());
		artist.setName(mbResponse.getName());
		artist.setDescription(description);
		artist.setAlbums(albums);
		return artist;
	}
}