package com.armannds.artistfinder.data;

import java.util.Set;

public class Artist {

    private String mkid;
    private String name;
    private String description;
    private Set<Album> albums;

    public Artist() {
    }

	public Artist(String mkid, String name, String description, Set<Album> albums) {
		this.mkid = mkid;
		this.name = name;
		this.description = description;
		this.albums = albums;
	}

	public String getMkid() {
        return mkid;
    }

    public void setMkid(String mkid) {
        this.mkid = mkid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public Set<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(Set<Album> albums) {
		this.albums = albums;
	}
}