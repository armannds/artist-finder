package com.armannds.artistfinder.api.musicbrainz;

import com.armannds.artistfinder.data.Album;
import com.armannds.artistfinder.data.Relation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicBrainzResponse {

    private String id;
    private String name;
    private Set<Relation> relations;
    private Set<Album> albums;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Relation> getRelations() {
        return relations;
    }

    public void setRelations(Set<Relation> relations) {
        this.relations = relations;
    }

	public Set<Album> getAlbums() {
		return albums;
	}

    @JsonProperty("release-groups")
    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }
}