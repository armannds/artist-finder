package com.armannds.artistfinder.api.musicbrainz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicBrainzResponse {

    private String id;
    private String name;
    private Set<Relation> relations;
    private Set<ReleaseGroup> albums;

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

	public Set<ReleaseGroup> getAlbums() {
		return albums;
	}

    @JsonProperty("release-groups")
    public void setAlbums(Set<ReleaseGroup> albums) {
        this.albums = albums;
    }
}