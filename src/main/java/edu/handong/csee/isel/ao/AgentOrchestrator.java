package edu.handong.csee.isel.ao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.protobuf.ByteString;

import io.grpc.internal.JsonParser;

import edu.handong.csee.isel.ao.network.ROSSimulator;
import edu.handong.csee.isel.ao.network.client.DataStoringClient;
import edu.handong.csee.isel.ao.network.server.ActionReceivingServer;
import edu.handong.csee.isel.proto.*;

public class AgentOrchestrator implements AutoCloseable {
    private ROSSimulator simulator;
    private ActionReceivingServer server;
    private DataStoringClient client;

    public AgentOrchestrator(Path config) throws IOException {
        Object jsonObject = JsonParser.parse(Files.readString(config));
        
        if (!(jsonObject instanceof Map jsonMap 
                && jsonMap.get("port") instanceof Integer)) {
            throw new IOException("Format of the config file is not valid.");
        }

        simulator = new ROSSimulator(this);
        server = new ActionReceivingServer(
                this, (Integer) jsonMap.get("port"));
        client = new DataStoringClient();
    }

    public void listenRobot() {
        simulator.start();
    }

    public void listenAgents() throws IOException {
        server.start();
    }

    public void update(
            float linearX, float linearY, float linearZ, 
            float angularX, float angularY, float angularZ) {
        simulator.sendAction(
                convertToSimulatorFormat(
                        linearX, linearY, linearZ, 
                        angularX, angularY, angularZ));
    }

    private String convertToSimulatorFormat(
            float linearX, float linearY, float linearZ, 
            float angularX, float angularY, float angularZ) {
        return String.format(
                "linear\n\tx: %f\n\ty: %f\n\tz: %f\n" 
                        + "angular\n\tx: %f\n\ty: %f\n\tz: %f\n",
                linearX, linearY, linearZ, 
                angularX, angularY, angularZ);
    }

    public void update(AgentInfo.AgentType type, String host, int port) {
        client.addStub(type, host, port);
    }

    public void update(
            byte[] image, byte[] depth, float accel, float angular, 
            float mag_str_x, float mag_str_y, String target, String text,
            byte[] header, byte[] format, byte[] data) {
        client.addRGBD(convertToClientFormat(image, depth));
        client.addIMU(
                convertToClientFormat(accel, angular, mag_str_x, mag_str_y));
        client.addUserCmd(convertToClientFormat(target, text));
        client.addVoice(convertToClientFormat(header, format, data));
    }

    private RGBD convertToClientFormat(byte[] image, byte[] depth) {
        return RGBD.newBuilder()
                   .setImage(ByteString.copyFrom(image))
                   .setDepth(ByteString.copyFrom(depth))
                   .build();
    }

    private IMU convertToClientFormat(
            float accel, float angular, float mag_str_x, float mag_str_y) {
        return IMU.newBuilder()
                  .setAccel(accel)
                  .setAngular(angular)
                  .setMagStrX(mag_str_x)
                  .setMagStrY(mag_str_y)
                  .build();
    }

    private UserCmd convertToClientFormat(String target, String text) {
        return UserCmd.newBuilder()
                      .setTarget(target)
                      .setTarget(text)
                      .build();
    }

    private Voice convertToClientFormat(
            byte[] header, byte[] format, byte[] data) {
        return Voice.newBuilder()
                    .setHeader(ByteString.copyFrom(header))
                    .setFormat(ByteString.copyFrom(format))
                    .setData(ByteString.copyFrom(data))
                    .build();
    }

    public void update() {
        
    }

    public void close() {
        server.shutdownNow();
        client.shutdownNow();
    }

    public static void main(String[] args) {
        try (AgentOrchestrator ao = new AgentOrchestrator(Path.of(args[0]))) {
            ao.listenRobot();
            ao.listenAgents();
        } catch (Exception e) {

        }
    }
}
