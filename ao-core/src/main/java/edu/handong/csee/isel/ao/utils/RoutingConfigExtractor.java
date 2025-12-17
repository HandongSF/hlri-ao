package edu.handong.csee.isel.ao.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.ToIntFunction;

import edu.handong.csee.isel.proto.*;

public class RoutingConfigExtractor extends ConfigExtractor {

    public RoutingConfigExtractor(Path config) throws IOException {
        super(config);
    }

    public AgentInfo.AgentType[][] getTargets() {
        try {
            return json.getAsJsonObject()
                       .getAsJsonArray("targets")
                       .asList()
                       .stream()
                       .map(t -> t.getAsJsonArray()
                                  .asList()
                                  .stream()
                                  .map(s -> AgentInfo.AgentType.forNumber(
                                            s.getAsInt()))
                                  .toList()
                                  .toArray(new AgentInfo.AgentType[0]))
                        .toList()
                        .toArray(new AgentInfo.AgentType[0][]);
        } catch (IllegalStateException 
                | ClassCastException 
                | NullPointerException e) {
            return null;
        }
    }

    public int[] getFrames() {
        try {
            return json.getAsJsonObject()
                       .getAsJsonArray("frames")
                       .asList()
                       .stream()
                       .mapToInt(value -> value.getAsInt())
                       .toArray();
        } catch (IllegalStateException 
                | ClassCastException
                | NullPointerException e) {
            return null;
        }
    }

    public int[][] getPairs() {
        try {
            return json.getAsJsonObject()
                       .getAsJsonArray("pairs")
                       .asList()
                       .stream()
                       .map(t -> t.getAsJsonArray()
                                  .asList()
                                  .stream()
                                  .mapToInt(value -> value.getAsInt())
                                  .toArray())
                       .toList()
                       .toArray(new int[0][]);
        } catch (IllegalStateException 
                | ClassCastException
                | NullPointerException e) {
            return null;
        }
    }
}
