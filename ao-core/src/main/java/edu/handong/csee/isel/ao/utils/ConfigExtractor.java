package edu.handong.csee.isel.ao.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.grpc.internal.JsonParser;

public class ConfigExtractor {
    private Object json;

    public ConfigExtractor(Path config) throws IOException {
        json = JsonParser.parse(Files.readString(config));
    }

    public Integer getServerPort() {
        return getPort("server");
    }

    public String getClientHost() {
        return getHost();
    }

    public Integer getClientPort() {
        return getPort("client");
    }

    private Integer getPort(String key) {
        try {
            return Integer.parseInt(getStringValue(key, "port"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getHost() {
        return getStringValue("client", "host");
    }

    private String getStringValue(String outerKey, String innerKey) {
        if (!(json instanceof Map jsonOuter
                && jsonOuter.get(outerKey) instanceof Map jsonInner
                && jsonInner.get(innerKey) instanceof String val)) {
            return null;
        }

        return val; 
    }
}
