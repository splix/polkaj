package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class DeserializeCommons {

    public static String getHexString(JsonParser jp) throws IOException {
        return getHexString(jp.readValueAs(String.class));
    }

    public static String getHexString(String value) {
        if (value == null || value.length() == 0 || "0x".equals(value)) {
            return null;
        }
        return value;
    }

    public static String getHexString(JsonNode node) {
        if (node == null) {
            return null;
        }
        return getHexString(node.textValue());
    }

}
