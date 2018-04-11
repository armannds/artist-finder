package com.armannds.artistfinder.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils(){}

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }

    public static TextNode createTextNode(String text) {
        return createObjectNode().textNode(text);
    }

    public static String getId(final JsonNode jsonNode) {
        return jsonNode.at("/id").textValue();
    }

    public static Stream<JsonNode> createStream(final JsonNode jsonNode) {
        return StreamSupport.stream(jsonNode.spliterator(), false);
    }
}
