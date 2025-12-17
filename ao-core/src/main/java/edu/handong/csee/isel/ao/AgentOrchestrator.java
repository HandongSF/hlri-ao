package edu.handong.csee.isel.ao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.eval.Evaluator;
import edu.handong.csee.isel.ao.network.ROSSimulator;
import edu.handong.csee.isel.ao.network.client.DataStoringClient;
import edu.handong.csee.isel.ao.network.server.ActionReceivingServer;
import edu.handong.csee.isel.ao.policy.Router;
import edu.handong.csee.isel.ao.policy.Scheduler;
import edu.handong.csee.isel.ao.utils.NetworkConfigExtractor;
import edu.handong.csee.isel.ao.utils.RoutingConfigExtractor;
import edu.handong.csee.isel.ao.utils.TempData;
import edu.handong.csee.isel.ao.examples.policy.ScenarioRouter;
import edu.handong.csee.isel.proto.*;

public class AgentOrchestrator implements AutoCloseable {
    private final Logger LOGGER = LoggerFactory.getLogger("AO");
    private final int NUM_AGENT = 3;
    private final int NUM_FRAME = 30;
    private final int EVAL_INTERVAL = 30000;

    private ActionReceivingServer server;
    private DataStoringClient client;
    private ROSSimulator simulator;
    private Router router;
    private Scheduler scheduler;
    private Queue<TempData> queue;
    private Evaluator evaluator;
    private List<Thread> threads;

    public AgentOrchestrator(Path networkConfig, Path routingConfig) 
            throws IOException {
        Integer port 
                = new NetworkConfigExtractor(networkConfig).getServerPort();
        
        if (port == null) {
            throw new IOException("Format of the config file is not valid");
        }
        
        server = new ActionReceivingServer(this, port);
        client = new DataStoringClient(this, NUM_AGENT);
        simulator = new ROSSimulator(this);
        router = new ScenarioRouter(this, routingConfig);
        scheduler = new Scheduler(routingConfig);
        evaluator = new Evaluator(routingConfig, NUM_AGENT);
        queue = new ConcurrentLinkedQueue<>();

        threads = new ArrayList<>();
        threads.add(new Thread(
                () -> {
                    try {
                        while (true) {
                            Thread.sleep(EVAL_INTERVAL); 
                            evaluator.summary();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, 
                "eval"));
    }

    public void run() throws IOException, InterruptedException {
        /** 
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
        **/
        Thread.currentThread().setName("main");
        
        server.start();

        while (!client.isReady()) {
            Thread.sleep(50);
        }

        simulator.start();
        threads.getFirst().start();
        
        while (true) {
            orchest();
        }
    }

    private void orchest() {
        AgentInfo.AgentType[] targets;
        List<TempData> tempDataList = new ArrayList<>();
        int numFrame = scheduler.nextNumFrame();
    
        while (tempDataList.size() < numFrame) {
            if (!queue.isEmpty()) {
                tempDataList.add(queue.poll());
            }
        }
        
        targets = router.route(tempDataList);
        
        for (TempData tempData : tempDataList) {
            for (AgentInfo.AgentType type : targets) {
                client.addData(type, convertToClientFormat(type, tempData));
            }
        }
    }

    private Data convertToClientFormat(
            AgentInfo.AgentType type, TempData data) {
        Data.Builder dataBuilder = Data.newBuilder();

        switch (type) {
            case AT_ISA:
                DataISA.Builder dataIsaBuilder 
                        = dataBuilder.getDataIsaBuilder();
        
                dataIsaBuilder.getRgbdBuilder()
                              .setImage(ByteString.copyFrom(data.getImage()))
                              .setDepth(ByteString.copyFrom(data.getDepth()));
                dataIsaBuilder.getImuBuilder()
                              .setAccel(data.getAccel())
                              .setAngular(data.getAngular())
                              .setMagStrX(data.getMagStrX())
                              .setMagStrY(data.getMagStrY());
                dataIsaBuilder.getCmdBuilder()
                              .setTarget(data.getTarget())
                              .setText(data.getText());
        
                return dataBuilder.setFrameNum(data.getFrameNum()).build();
            
            case AT_IUA:
                DataIUA.Builder dataIuaBuilder 
                        = dataBuilder.getDataIuaBuilder();
        
                dataIuaBuilder.getRgbdBuilder()
                              .setImage(ByteString.copyFrom(data.getImage()))
                              .setDepth(ByteString.copyFrom(data.getDepth()));
                dataIuaBuilder.getVoiceBuilder()
                              .setHeader(ByteString.copyFrom(data.getHeader()))
                              .setFormat(ByteString.copyFrom(data.getFormat()))
                              .setData(ByteString.copyFrom(data.getData()));

                return dataBuilder.setFrameNum(data.getFrameNum()).build();
            
            case AT_IOA:
                dataBuilder.getDataIoaBuilder()
                           .getRgbdBuilder()
                           .setImage(ByteString.copyFrom(data.getImage()))
                           .setDepth(ByteString.copyFrom(data.getDepth()));
        
                return dataBuilder.setFrameNum(data.getFrameNum()).build();
            
            default: 
                return null;
        }
    }

    public void update(
            byte[] image, byte[] depth, int frameNum, 
            float accel, float angular, 
            float magStrX, float magStrY, 
            String target, String text,
            byte[] header, byte[] format, byte[] data) {
        queue.add(
                new TempData(
                        image, depth, frameNum, 
                        accel, angular, magStrX, magStrY, 
                        target, text, header, format, data));
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

    public void update(AgentInfo.AgentType[] decision) {
        evaluator.evalRout(decision);
    }

    public void update(boolean[] history) {
        evaluator.evalTransfer(history);
    }
               
    public void update(Data data) {
        evaluator.record(data);
    }

    public void update(RawAction action) {
        evaluator.evalResp(action);

        simulator.sendAction(convertToSimulatorFormat(action));
    }

    private String convertToSimulatorFormat(RawAction action) {
        switch (action.getAgentRawActionCase()) {
            case RAW_ACTION_ISA:
                RawActionISA rawActionISA = action.getRawActionIsa();
                Linear linear = rawActionISA.getLinear();
                Angular angular = rawActionISA.getAngular();

                return String.format(
                        "linear\n\tx: %f\n\ty: %f\n\tz: %f\n" 
                                + "angular\n\tx: %f\n\ty: %f\n\tz: %f",
                         linear.getX(), linear.getY(), linear.getZ(), 
                         angular.getX(), angular.getY(), angular.getZ());
                
            case RAW_ACTION_IUA:
                return String.format(
                        "response\n\t%s", 
                        action.getRawActionIua().getSpeech());
                
            case RAW_ACTION_IOA:
                RawActionIOA rawActionIOA = action.getRawActionIoa();
                Coordinate coord = rawActionIOA.getCoord();

                return String.format(
                        "interaction\n\t%s\n"
                                + "coordinate\n\tx: %f\n\ty: %f\n\tz: %f",
                        rawActionIOA.getIntr(), coord.getX(), 
                        coord.getY(), coord.getZ());
                
            default:
                return "robot action";
        }   
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
                                               .toURI()),
                Path.of(AgentOrchestrator.class.getResource("/ao-routing.json")
                                               .toURI()))) {
            ao.run();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
