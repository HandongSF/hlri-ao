package edu.handong.csee.isel.ao.utils;

import java.io.IOException;
import java.nio.file.Path;

public class NetworkConfigExtractor extends ConfigExtractor {
    
    public NetworkConfigExtractor(Path config) throws IOException {
        super(config);
    }

    public Integer getServerPort() {
        return getPort("server");
    }

    public String getClientHost() {
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

    public Integer getClientPort() {
        return getPort("client");
    }

    private Integer getPort(String key) {
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
