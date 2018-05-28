package com.armannds.artistfinder.rest.artistfindercontroller;

import com.armannds.artistfinder.rest.ArtistFinderController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidateParametersTest {

    private static final String EMPTY = "";
    private static final String MBID_WITH_INVALID_LENGTH = "1234567899";

    @Autowired
    private ArtistFinderController artistFinderController;

    @Test
    public void testIllegalArgumentsWithNull() {
        testIllegalArguments(null);
    }

    @Test
    public void testIllegalArgumentsWithEmpty() {
        testIllegalArguments(EMPTY);
    }

    @Test
    public void testIllegalArgumentsWithInvalidLength() {
        testIllegalArguments(MBID_WITH_INVALID_LENGTH);
    }

    private void testIllegalArguments(String mbid) {
        Throwable thrown = catchThrowable(() -> artistFinderController.getArtistByMbid(mbid));
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MBID " + mbid + " is not valid");
    }
}
