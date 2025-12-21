package edu.handong.csee.isel.ao.examples.utils;

import java.io.IOException;
import java.nio.file.Path;

import edu.handong.csee.isel.ao.utils.ConfigExtractor;

public class SchedulingConfigExtractor extends ConfigExtractor {

    public SchedulingConfigExtractor(Path config) throws IOException {
        super(config);
    }

    public int[] extractNumFrames() {
        try {
            return json.getAsJsonObject()
                       .getAsJsonArray("numFrames")
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
