package com.armannds.artistfinder.api.coverartarchive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverArtArchiveImage {

    private String image;

    public CoverArtArchiveImage() {
    }

    public CoverArtArchiveImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
