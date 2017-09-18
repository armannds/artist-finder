package com.armannds.artistfinder.rest;

import com.armannds.artistfinder.data.Artist;
import com.armannds.artistfinder.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

@RestController
public class ArtistFinderController {

    private static final Logger LOG = LoggerFactory.getLogger(ArtistFinderController.class);

    private static final int VALID_MBID_LENGTH = 36;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/artist/{mbid}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Callable<ResponseEntity<Artist>> getArtistByMbid(@PathVariable("mbid") String mbid) {
        if (isParameterValid(mbid)) {
	        return () -> createSuccessResponse(artistService.getArtistById(mbid));
        } else {
        	LOG.warn("Bad request with parameter <" + ">!");
			return this::createBadRequestResponse;
        }
    }

	private boolean isParameterValid(String mbid) {
        return !StringUtils.isEmpty(mbid) && mbid.length() == VALID_MBID_LENGTH;
    }

    private ResponseEntity<Artist> createSuccessResponse(Artist artist) {
    	return new ResponseEntity<>(artist, HttpStatus.OK);
    }

	private ResponseEntity<Artist> createBadRequestResponse() {
    	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
