package edu.handong.csee.isel.ao.examples.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;

import edu.handong.csee.isel.ao.utils.ConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class RoutingConfigExtractor extends ConfigExtractor {

    public RoutingConfigExtractor(Path config) throws IOException {
        super(config);
    }

    public AgentInfo.AgentType[][] extractTargets() {
        return extract(
                "targets", 
                t -> t.getAsJsonArray()
                      .asList()
                      .stream()
                      .map(s -> AgentInfo.AgentType.forNumber(s.getAsInt()))
                      .toList()
                      .toArray(new AgentInfo.AgentType[0]), 
                new AgentInfo.AgentType[0][]);
    }

    public int[][] extractPairs() {
        return extract(
                "pairs", 
                t -> t.getAsJsonArray()
                      .asList()
                      .stream()
                      .mapToInt(value -> value.getAsInt())
                      .toArray(),
                new int[0][]);
    }

    private <T> T[] extract(
            String name, 
            Function<? super JsonElement, T> mapper, 
            T[] container) {
        try {
            return json.getAsJsonObject()
                       .getAsJsonArray(name)
                       .asList()
                       .stream()
                       .map(mapper)
                       .toList()
                       .toArray(container);
        } catch (
                IllegalStateException 
                        | ClassCastException 
                        | NullPointerException e) {
            return null;
        }
    }
}
