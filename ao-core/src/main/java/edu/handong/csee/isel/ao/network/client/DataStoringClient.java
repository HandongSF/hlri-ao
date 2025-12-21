package edu.handong.csee.isel.ao.network.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import com.google.rpc.Status;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.AgentOrchestrator;
import edu.handong.csee.isel.proto.*;

public class DataStoringClient {
    private final Logger LOGGER = LoggerFactory.getLogger("AO-client");

    private Map<AgentInfo.AgentType, DataStoringGrpc.DataStoringStub> stubs;
    private Map<AgentInfo.AgentType, Queue<Data>> queues;
    private AgentOrchestrator subscriber;
    private int numAgent;

    public DataStoringClient(AgentOrchestrator ao, int numAgent) {
        stubs = new ConcurrentHashMap<>();
        queues = new HashMap<>();
        subscriber = ao;
        this.numAgent = numAgent;
    }
    
    public void sendData(AgentInfo.AgentType type, int frames) {
        LOGGER.info("Sending data to an Agent");        
        if (type == AgentInfo.AgentType.AT_UNSPECIFIED 
                || type == AgentInfo.AgentType.UNRECOGNIZED) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<Status> responseObserver = new StreamObserver<>() {
        
            @Override
            public void onNext(Status value) {
                
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override 
            public void onCompleted() {
                notifySubscriber();

                latch.countDown();
            }
        };
        StreamObserver<Data> requestObserver 
                = stubs.get(type).sendData(responseObserver);
        Queue<Data> queue = queues.get(type);
        int sent = 0;

        while (sent < frames) {
            if (!queue.isEmpty()) {
                Data data = queue.poll();
                
                notify(data);

                requestObserver.onNext(data);
                sent++;
            } 
        }

        requestObserver.onCompleted();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return stubs.size() == numAgent;
    }

    public void addStub(AgentInfo.AgentType key, String host, int port) {
        LOGGER.info(
                "Creating an AO client {}/{}", 
                stubs.size() + 1, numAgent);
        stubs.put(
                key, 
                DataStoringGrpc.newStub(
                        ManagedChannelBuilder.forAddress(host, port)
                                             .usePlaintext()
                                             .build()));
        queues.put(key, new ConcurrentLinkedQueue<>());
    }

    public void addData(AgentInfo.AgentType type, Data data) {
        Queue<Data> queue = queues.get(type);

        if (queue != null) {
            queue.add(data);
        }
    }

    public void notify(Data data) {
        subscriber.update(data);
    }

    public void notifySubscriber() {
        subscriber.update();
    }

    public void shutdown() throws InterruptedException {
        for (DataStoringGrpc.DataStoringStub stub : stubs.values()) {
            ((ManagedChannel) stub.getChannel()).shutdown()
                                                .awaitTermination(
                                                        10, 
                                                        TimeUnit.MILLISECONDS);
        }
    }
}
