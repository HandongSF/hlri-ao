package edu.handong.csee.isel.ao.examples.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.handong.csee.isel.ao.utils.ConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class ScenarioConfigExtractor extends ConfigExtractor {

    public ScenarioConfigExtractor(Path config) throws IOException {
        super(config);
    }
    
    public int[] extractFrameNums() {
        try {
            return json.getAsJsonObject()
                       .get("frameNums")
                       .getAsJsonArray()
                       .asList()
                       .stream()
                       .mapToInt(value -> value.getAsInt())
                       .toArray();
        } catch (IllegalStateException | NullPointerException e) {
            return null;
        }
    }

    public RawAction[] extractRawActions(AgentInfo.AgentType type) {
        try {
            Stream<JsonElement> JsonElementStream = json.getAsJsonObject()
                                                        .get("rawActions")
                                                        .getAsJsonArray()
                                                        .asList()
                                                        .stream();
            Stream<RawAction> rawActionStream = null;

            switch (type) {
                case AT_UNSPECIFIED:
                    return null;

                case AT_ISA:
                    rawActionStream = JsonElementStream.map(
                            this::buildRawActionISA);
                    
                    break;
                
                case AT_IUA:
                    rawActionStream = JsonElementStream.map(
                            this::buildRawActionIUA);
                    
                    break;
                
                case AT_IOA:
                     rawActionStream = JsonElementStream.map(
                            this::buildRawActionIOA);
                    
                    break;
                  
                case UNRECOGNIZED:
                    return null;
            }

            return rawActionStream.toList().toArray(new RawAction[0]);
        } catch (IllegalStateException | NullPointerException e) {
            return null;
        }
    }

    private RawAction buildRawActionISA(JsonElement json) {
        RawAction.Builder rawActionBuilder = RawAction.newBuilder();
        RawActionISA.Builder rawActionIsaBuilder 
                = rawActionBuilder.getRawActionIsaBuilder();
        JsonObject rawAction = json.getAsJsonObject();
        JsonObject linear = rawAction.getAsJsonObject("linear");
        JsonObject angular = rawAction.getAsJsonObject("angular");

        rawActionBuilder.setFrameNum(rawAction.get("frameNum").getAsInt());
        rawActionIsaBuilder.getLinearBuilder()
                           .setX(linear.get("x").getAsInt())
                           .setY(linear.get("y").getAsInt())
                           .setZ(linear.get("z").getAsInt());
        rawActionIsaBuilder.getAngularBuilder()
                           .setX(angular.get("x").getAsInt())
                           .setY(angular.get("y").getAsInt())
                           .setZ(angular.get("z").getAsInt());
        
        return rawActionBuilder.build();
    }

    private RawAction buildRawActionIUA(JsonElement json) {
        RawAction.Builder builder = RawAction.newBuilder();
        JsonObject rawAction = json.getAsJsonObject();

        builder.setFrameNum(rawAction.get("frameNum").getAsInt());
        builder.getRawActionIuaBuilder()
               .setSpeech(rawAction.get("speech").getAsString());
        
        return builder.build();
    }

    private RawAction buildRawActionIOA(JsonElement json) { 
        RawAction.Builder rawActionBuilder = RawAction.newBuilder();
        RawActionIOA.Builder rawActionIoaBuilder 
                = rawActionBuilder.getRawActionIoaBuilder();
        JsonObject rawAction = json.getAsJsonObject();
        JsonObject coord = rawAction.getAsJsonObject("coord");

        rawActionBuilder.setFrameNum(rawAction.get("frameNum").getAsInt());
        rawActionIoaBuilder.setIntr(
                RawActionIOA.Interaction.valueOf(
                        rawAction.get("intr").getAsInt()));
        rawActionIoaBuilder.getCoordBuilder()
                           .setX(coord.get("x").getAsFloat())
                           .setY(coord.get("y").getAsFloat())
                           .setZ(coord.get("z").getAsFloat());
        
        return rawActionBuilder.build();
    }
}
