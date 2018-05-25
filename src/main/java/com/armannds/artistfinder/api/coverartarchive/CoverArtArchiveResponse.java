package com.armannds.artistfinder.api.coverartarchive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverArtArchiveResponse {

    private Set<CoverArtArchiveImage> images;

    public Set<CoverArtArchiveImage> getImages() {
        return images;
    }

    public void setImages(Set<CoverArtArchiveImage> images) {
        this.images = images;
    }
}
