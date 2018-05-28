package com.armannds.artistfinder.api.wikipedia;

import org.junit.Test;

public class FetchWikipediaDescriptionTest {

    private static final String WIKIPEDIA_DESCRIPTION_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles={artistName}";
    private static final String WIKIPEDIA = "wikipedia";

    @Test
    public void testFetchingFromWikipedia() {
        String jsonResponse = "{\"pages\": {\"1234\": {\"pageid\": 1234,\"extract\": \"Lorem ipsum\"}}}";
//        String artistName = "Kaleo";
//        when(restTemplate
//                .exchange(WIKIPEDIA_DESCRIPTION_URL, HttpMethod.GET,
//                        new HttpEntity<>(new HttpHeaders()), String.class, artistName))
//                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
//        when(jsonObjectMapper
//                .readTree(jsonResponse))
//                .thenReturn(root);
//        when(root.findValue("extract")).thenReturn(new TextNode("Lorem ipsum"));


//        String resourceUrl = "www.island.is/Kaleo";
//        Set<Relation> relations = createRelations(resourceUrl);
//        CompletableFuture<String> future = wikipediaService.getDescriptionFromRelations(relations);
//        assertTrue("Should contain Lorem ipsum", "Lorem ipsum".equals(future.get()));
    }
}
