package edu.handong.csee.isel.ao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.protobuf.ByteString;

import io.grpc.internal.JsonParser;

import edu.handong.csee.isel.ao.network.ROSSimulator;
import edu.handong.csee.isel.ao.network.client.DataStoringClient;
import edu.handong.csee.isel.ao.network.server.ActionReceivingServer;
import edu.handong.csee.isel.ao.policy.Router;
import edu.handong.csee.isel.proto.*;

public class AgentOrchestrator implements AutoCloseable {
    private ROSSimulator simulator;
    private ActionReceivingServer server;
    private DataStoringClient client;
    private Router router;
    private Collection<Thread> threads;

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
        router = new Router();
        threads = new ArrayList<>();
    }

    public void run() throws IOException {
        simulator.start();
        server.start();
    }

    public void update(AgentInfo.AgentType type, String host, int port) {
        Thread thread;
        
        client.addStub(type, host, port);
        thread = new Thread(
                () -> { 
                    try {
                        while (true) {
                            client.sendData(type, 30);
                            Thread.sleep(1);
                        } 
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
        threads.add(thread);
        thread.start();
    }

    public void update(
            byte[] image, byte[] depth, float accel, float angular, 
            float mag_str_x, float mag_str_y, String target, String text,
            byte[] header, byte[] format, byte[] data) {
        for (AgentInfo.AgentType type
                : router.route(
                        image, depth, accel, angular, 
                        mag_str_x, mag_str_y, target, 
                        text, header, format, data)) {
                            
            switch (type) {
                case AT_UNSPECIFIED:

                case AT_ISA:
                    client.addData(
                            type, 
                            convertToClientFormat(
                                    image, depth, accel, angular, 
                                    mag_str_x, mag_str_y, target, text));
                    
                    break;
                
                case AT_IUA:
                    client.addData(
                            type, 
                            convertToClientFormat(
                                    image, depth, header, format, data));

                    break;

                case AT_IOA:
                
                case UNRECOGNIZED:
            }
        }
    }

    private Data convertToClientFormat(
            byte[] image, byte[] depth, float accel, float angular, 
            float mag_str_x, float mag_str_y, String target, String text) {
        Data.Builder dataBuilder = Data.newBuilder();
        DataISA.Builder dataISABuilder = dataBuilder.getDataISABuilder();

        dataISABuilder.getRgbdBuilder()
                      .setImage(ByteString.copyFrom(image))
                      .setDepth(ByteString.copyFrom(depth));
        dataISABuilder.getImuBuilder()
                      .setAccel(accel)
                      .setAngular(angular)
                      .setMagStrX(mag_str_x)
                      .setMagStrY(mag_str_y);
        dataISABuilder.getCmdBuilder()
                      .setTarget(target)
                      .setTarget(text);
        
        return dataBuilder.build();
    }

    private Data convertToClientFormat(
            byte[] image, byte[] depth, 
            byte[] header, byte[] format, byte[] data) {
        Data.Builder dataBuilder = Data.newBuilder();
        DataIUA.Builder dataIUABuilder = DataIUA.newBuilder();

        dataIUABuilder.getRgbdBuilder()
                      .setImage(ByteString.copyFrom(image))
                      .setDepth(ByteString.copyFrom(depth));
        dataIUABuilder.getVoiceBuilder()
                      .setHeader(ByteString.copyFrom(header))
                      .setFormat(ByteString.copyFrom(format))
                      .setFormat(ByteString.copyFrom(data));
        
        return dataBuilder.build();
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

    public void update() {
        
    }

    public void close() throws InterruptedException {
        server.shutdown();
        
        for (Thread thread : threads) {
            thread.interrupt();
            
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        client.shutdown();
    }

    public static void main(String[] args) {
        try (AgentOrchestrator ao = new AgentOrchestrator(
                Path.of(ClassLoader.getSystemResource("ao.json").toURI()))) {
            ao.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
