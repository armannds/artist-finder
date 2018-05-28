package com.armannds.artistfinder.api.wikipedia;

import com.armannds.artistfinder.rest.errorhandling.DescriptionException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ValidateParametersTest {

    private static final String EMPTY = "";

    @MockBean
    private RestTemplate restTemplate;

    private WikipediaService wikipediaService;

    @Before
    public void setUp() {
        wikipediaService = new WikipediaService(restTemplate);
    }

    @Test
    public void testInvalidArtistNameWithNull() {
        testInvalidArtistName(null);
    }

    @Test
    public void testInvalidResourceURl_empty() {
        testInvalidArtistName(EMPTY);
    }

    private void testInvalidArtistName(String artistName) {
        Throwable thrown = catchThrowable(() -> wikipediaService.getDescription(artistName));
        assertThat(thrown)
                .isInstanceOf(DescriptionException.class)
                .hasMessage("No description name available");
    }
}
