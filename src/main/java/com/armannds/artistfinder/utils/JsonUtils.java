package com.armannds.artistfinder.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtils {

    private JsonUtils(){}

    public static ObjectNode createObjectNode() {
        return new ObjectMapper().createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return new ObjectMapper().createArrayNode();
    }
}
