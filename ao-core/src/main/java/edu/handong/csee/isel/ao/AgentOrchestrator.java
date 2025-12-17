package edu.handong.csee.isel.ao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.eval.Evaluator;
import edu.handong.csee.isel.ao.network.ROSSimulator;
import edu.handong.csee.isel.ao.network.client.DataStoringClient;
import edu.handong.csee.isel.ao.network.server.ActionReceivingServer;
import edu.handong.csee.isel.ao.policy.Router;
import edu.handong.csee.isel.ao.utils.NetworkConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class AgentOrchestrator implements AutoCloseable {
    private final Logger LOGGER = LoggerFactory.getLogger("AO");
    private final int NUM_AGENT = 2;
    private final int NUM_FRAME = 30;

    private ActionReceivingServer server;
    private DataStoringClient client;
    private ROSSimulator simulator;
    private Router router;
    private Evaluator evaluator;
    private List<Thread> threads;

    public AgentOrchestrator(Path config) throws IOException {
        Integer port = new NetworkConfigExtractor(config).getServerPort();
        
        if (port == null) {
            throw new IOException("Format of the config file is not valid");
        }
        
        server = new ActionReceivingServer(this, port);
        client = new DataStoringClient(this, NUM_AGENT);
        simulator = new ROSSimulator(this);
        router = new Router();
        evaluator = new Evaluator(NUM_AGENT);

        threads = new ArrayList<>();
        threads.add(new Thread(
                () -> {
                    try {
                        while (true) {
                            Thread.sleep(30000); 
                            evaluator.summary();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, 
                "eval"));
    }

    public void run() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Thread shutdownHook = new Thread(
                () -> { 
                    try {
                        close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                },
                "shutdown-hook");

        Thread.currentThread().setName("main");
        runtime.addShutdownHook(shutdownHook);
        
        server.start();

        while (!client.isReady());

        simulator.start();
        threads.getFirst().start();

        server.awaitTermination(10, TimeUnit.MINUTES);
        
        LOGGER.info("Time limit exceeded");
        runtime.removeShutdownHook(shutdownHook);
    }

    public void update(AgentInfo info) {
        Thread thread;
        AgentInfo.AgentType type = info.getType();
        
        evaluator.addRecord(type);
        client.addStub(type, info.getHost(), info.getPort());
        
        thread = new Thread(
                () -> {
                    int frameSent = 0;

                    while (!Thread.interrupted()) {
                        client.sendData(type, NUM_FRAME);
                        frameSent += NUM_FRAME;

                        evaluator.evalSync(frameSent);
                    }
                },
                "AO-" + type.toString().replace("AT_", ""));
        threads.add(thread);
        thread.start();
    }

    public void update(
            byte[] image, byte[] depth, int frameNum, 
            float accel, float angular, 
            float mag_str_x, float mag_str_y, 
            String target, String text,
            byte[] header, byte[] format, byte[] voiceData) {
        for (AgentInfo.AgentType type
                : router.route(
                        image, depth, accel, angular, 
                        mag_str_x, mag_str_y, target, 
                        text, header, format, voiceData)) {
            Data data;

            switch (type) {
                case AT_ISA:
                    data = convertToClientFormat(
                            image, depth, frameNum, 
                            accel, angular, mag_str_x, mag_str_y, 
                            target, text);
                    
                    break;
                
                case AT_IUA:
                    data = convertToClientFormat(
                            image, depth, frameNum, 
                            header, format, voiceData);

                    break;

                case AT_IOA:
                    data = convertToClientFormat(image, depth, frameNum);

                    break;
                
                default:
                    data = Data.getDefaultInstance();
            }

            client.addData(type, data);
        }
    }

    private Data convertToClientFormat(
            byte[] image, byte[] depth, int frameNum, 
            float accel, float angular, 
            float mag_str_x, float mag_str_y, 
            String target, String text) {
        Data.Builder dataBuilder = Data.newBuilder();
        DataISA.Builder dataIsaBuilder = dataBuilder.getDataIsaBuilder();
        
        dataIsaBuilder.getRgbdBuilder()
                      .setImage(ByteString.copyFrom(image))
                      .setDepth(ByteString.copyFrom(depth));
        dataIsaBuilder.getImuBuilder()
                      .setAccel(accel)
                      .setAngular(angular)
                      .setMagStrX(mag_str_x)
                      .setMagStrY(mag_str_y);
        dataIsaBuilder.getCmdBuilder()
                      .setTarget(target)
                      .setText(text);
        
        return dataBuilder.setFrameNum(frameNum).build();
    }

    private Data convertToClientFormat(
            byte[] image, byte[] depth, int frameNum,
            byte[] header, byte[] format, byte[] data) {
        Data.Builder dataBuilder = Data.newBuilder();
        DataIUA.Builder dataIuaBuilder = dataBuilder.getDataIuaBuilder();
        
        dataIuaBuilder.getRgbdBuilder()
                      .setImage(ByteString.copyFrom(image))
                      .setDepth(ByteString.copyFrom(depth));
        dataIuaBuilder.getVoiceBuilder()
                      .setHeader(ByteString.copyFrom(header))
                      .setFormat(ByteString.copyFrom(format))
                      .setData(ByteString.copyFrom(data));
        
        return dataBuilder.setFrameNum(frameNum).build();
    }

    private Data convertToClientFormat(
            byte[] image, byte[] depth, int frameNum) {
        Data.Builder dataBuilder = Data.newBuilder();
        
        dataBuilder.getDataIoaBuilder()
                   .getRgbdBuilder()
                   .setImage(ByteString.copyFrom(image))
                   .setDepth(ByteString.copyFrom(depth));
        
        return dataBuilder.setFrameNum(frameNum).build();
    }

    public void update(Data data) {
        evaluator.record(data);
    }

    public void update(RawAction rawAction) {
        String action;

        evaluator.evalResp(rawAction);
        
        switch (rawAction.getAgentRawActionCase()) {
            case RAW_ACTION_ISA:
                RawActionISA rawActionISA = rawAction.getRawActionIsa();
                Linear linear = rawActionISA.getLinear();
                Angular angular = rawActionISA.getAngular();

                action = convertToSimulatorFormat(
                        linear.getX(), linear.getY(), linear.getZ(),
                        angular.getX(), angular.getY(), angular.getZ());
                
                break;

            case RAW_ACTION_IUA:
                action = convertToSimulatorFormat(
                        rawAction.getRawActionIua().getSpeech());
                
                break;

            case RAW_ACTION_IOA:
                RawActionIOA rawActionIOA = rawAction.getRawActionIoa();
                Coordinate coord = rawActionIOA.getCoord();

                action = convertToSimulatorFormat(
                        rawActionIOA.getIntr().toString(), 
                        coord.getX(), coord.getY(), coord.getZ());
                
                break;

            default:
                action = "robot action";
        }

        simulator.sendAction(action);
    }

    private String convertToSimulatorFormat(
            float linearX, float linearY, float linearZ, 
            float angularX, float angularY, float angularZ) {
        return String.format(
                "linear\n\tx: %f\n\ty: %f\n\tz: %f\n" 
                        + "angular\n\tx: %f\n\ty: %f\n\tz: %f",
                linearX, linearY, linearZ, 
                angularX, angularY, angularZ);
    }

    private String convertToSimulatorFormat(String speech) {
        return String.format("response\n\t%s", speech);
    }

    private String convertToSimulatorFormat(
            String intr, float coordX, float coordY, float coordZ) {
        return String.format(
                "interaction\n\t%s\ncoordinate\n\tx: %f\n\ty: %f\n\tz: %f",
                intr, coordX, coordY, coordZ);
    }

    public void close() throws InterruptedException {
        LOGGER.info("Shutting down server and client");
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
                Path.of(AgentOrchestrator.class.getResource("/ao-network.json")
                                               .toURI()))) {
            ao.run();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
