package com.armannds.artistfinder.api.coverartarchive;

import java.util.Collections;

public class NotFoundCoverArtArchiveResponse extends CoverArtArchiveResponse {

    public static final String NOT_FOUND = "404 cover icon not found";

    public NotFoundCoverArtArchiveResponse() {
        super();
        setImages(Collections.singleton(new CoverArtArchiveImage(NOT_FOUND)));
    }
}
