package com.armannds.artistfinder.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Relation {

    private String type;
    private String resource;

	public Relation() {
	}

	public Relation(String type, String resource) {
		this.type = type;
		this.resource = resource;
	}

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResource() {
        return resource;
    }

    @JsonProperty("url")
    public void setResource(Map<String, String> url) {
        this.resource = url.get("resource");
    }
}
