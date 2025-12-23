package edu.handong.csee.isel.ao.utils;

import java.io.IOException;
import java.nio.file.Path;

public class NetworkConfigExtractor extends ConfigExtractor {
    
    public NetworkConfigExtractor(Path config) throws IOException {
        super(config);
    }

    public Integer extractServerPort() {
        return extractPortFrom("server");
    }

    public String extractClientHost() {
         try {
            return json.getAsJsonObject()
                       .getAsJsonObject("client")
                       .get("host")
                       .getAsString();
        } catch (IllegalStateException 
                | ClassCastException 
                | NullPointerException e) {
            return null;
        }
    }

    public Integer extractClientPort() {
        return extractPortFrom("client");
    }

    private Integer extractPortFrom(String key) {
        try {
            return json.getAsJsonObject()
                       .getAsJsonObject(key)
                       .get("port")
                       .getAsInt();
        } catch (IllegalStateException 
                | ClassCastException 
                | NullPointerException e) {
            return null;
        }
    }
}
