package com.armannds.artistfinder.dto;

import java.util.Collection;

public class Artist {

    private String mkid;
    private String description;
    private Collection<Album> albums;

    public Artist(String mkid, String description, Collection<Album> albums) {
        this.mkid = mkid;
        this.description = description;
        this.albums = albums;
    }

    public String getMkid() {
        return mkid;
    }

    public String getDescription() {
        return description;
    }

    public Collection<Album> getAlbums() {
        return albums;
    }

    public static final class Builder {
        private String id;
        private String description;
        private Collection<Album> albums;

        public Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder albums(Collection<Album> albums) {
            this.albums = albums;
            return this;
        }

        public Artist build() {
            return new Artist(id, description, albums);
        }
    }
}