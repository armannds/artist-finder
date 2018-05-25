package com.armannds.artistfinder.dto;

public class Album {
    private String id;
    private String title;
    private String image;

    public Album() {
    }

    public Album(String id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public static final class Builder {
        private String id;
        private String title;
        private String image;

        public Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Album build() {
            return new Album(id, title, image);
        }
    }
}
