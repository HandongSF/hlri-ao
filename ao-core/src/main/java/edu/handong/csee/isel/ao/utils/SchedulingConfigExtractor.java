package edu.handong.csee.isel.ao.utils;

import java.io.IOException;
import java.nio.file.Path;

import edu.handong.csee.isel.proto.*;

public class SchedulingConfigExtractor extends ConfigExtractor {

    public SchedulingConfigExtractor(Path config) throws IOException {
        super(config);
    }

    public int[] extractNumFramesOverall() {
        return extractNumFramesOf("overall");
    }

    public int[] extractNumFramesForType(AgentInfo.AgentType type) {
        return extractNumFramesOf(type.name().replace("AT_", ""));
    }

    private int[] extractNumFramesOf(String key) {
        try {
            return json.getAsJsonObject()
                       .getAsJsonObject("numFrames")
                       .getAsJsonArray(key)
                       .asList()
                       .stream()
                       .mapToInt(value -> value.getAsInt())
                       .toArray();
        } catch (
                IllegalStateException 
                        | ClassCastException
                        | NullPointerException e) {
            return null;
        }
    }
}
