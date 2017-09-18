package com.armannds.artistfinder.api.wikipedia;

import com.armannds.artistfinder.data.Relation;
import com.armannds.artistfinder.errorhandling.DescriptionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class WikipediaServiceUnitTest {

	private static final String WIKIPEDIA_DESCRIPTION_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles={artistName}";

	private static final String WIKIPEDIA = "wikipedia";

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private ObjectMapper jsonObjectMapper;

	@MockBean
	private JsonNode root;

	private WikipediaService wikipediaService;

	private Set<Relation> relations;

	@Before
	public void setUp() {
		wikipediaService = new WikipediaService(restTemplate, jsonObjectMapper);
	}

	private Set<Relation> createRelations(String resourceUrl) {
		Relation relation = new Relation(WIKIPEDIA, resourceUrl);
		return new HashSet<>(Collections.singleton(relation));
	}

	@Test
	public void testInvalidResourceURl_null() {
		Set<Relation> relations = createRelations(null);
		try {
			wikipediaService.getDescriptionFromRelations(relations);
			fail("Should throw DescriptionException");
		} catch (DescriptionException e) {
			assertTrue("Contains error message", !StringUtils.isEmpty(e.getMessage()));
		}
	}

	@Test
	public void testInvalidResourceURl_empty() {
		Set<Relation> relations = createRelations("");
		try {
			wikipediaService.getDescriptionFromRelations(relations);
			fail("Should throw DescriptionException");
		} catch (DescriptionException e) {
			assertTrue("Contains error message", !StringUtils.isEmpty(e.getMessage()));
		}
	}

	@Test
	public void testGettingDescriptionFromWikipedia() throws Exception {
		String jsonResponse = "{\"pages\": {\"1234\": {\"pageid\": 1234,\"extract\": \"Lorem ipsum\"}}}";
		String artistName = "Kaleo";
		when(restTemplate
				.exchange(WIKIPEDIA_DESCRIPTION_URL, HttpMethod.GET,
						new HttpEntity<>(new HttpHeaders()), String.class, artistName))
				.thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
		when(jsonObjectMapper
				.readTree(jsonResponse))
				.thenReturn(root);
		when(root.findValue("extract")).thenReturn(new TextNode("Lorem ipsum"));


		String resourceUrl = "www.island.is/Kaleo";
		Set<Relation> relations = createRelations(resourceUrl);
		CompletableFuture<String> future = wikipediaService.getDescriptionFromRelations(relations);
		assertTrue("Should contain Lorem ipsum", "Lorem ipsum".equals(future.get()));
	}
}