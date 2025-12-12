package edu.handong.csee.isel.ao.network.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.google.rpc.Status;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.proto.*;

public class DataStoringClient {
    private Map<AgentInfo.AgentType, DataStoringGrpc.DataStoringStub> stubs 
            = new HashMap<>();
    private Map<AgentInfo.AgentType, Queue<Data>> queues = Map.of(
            AgentInfo.AgentType.AT_ISA, new LinkedList<>(),
            AgentInfo.AgentType.AT_IUA, new LinkedList<>(),
            AgentInfo.AgentType.AT_IOA, new LinkedList<>());
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public void sendData(AgentInfo.AgentType type, int frames) {
        logger.info("Sending data to Agent client");        
        if (type == AgentInfo.AgentType.AT_UNSPECIFIED 
                || type == AgentInfo.AgentType.UNRECOGNIZED) {
            return;
        }
        
        StreamObserver<Status> responseObserver = new StreamObserver<>() {
        
            @Override
            public void onNext(Status value) {
                logger.info("Data sending status: {}", value.getCode());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override 
            public void onCompleted() {

            }
        };
        
        StreamObserver<Data> requestObserver 
                = stubs.get(type).sendData(responseObserver);
        Queue<Data> queue = queues.get(type);
        int sent = 0;

        while (sent < frames) {
            if (queue.peek() != null) {
                requestObserver.onNext(queue.poll());
                sent++;
            } 
        }

        requestObserver.onCompleted();
    }

    public void addStub(AgentInfo.AgentType key, String host, int port) {
        logger.info("Creating AO client");
        stubs.put(
                key, 
                DataStoringGrpc.newStub(
                        ManagedChannelBuilder.forAddress(host, port)
                                             .usePlaintext()
                                             .build()));
    }

    public void addData(AgentInfo.AgentType type, Data data) {
        queues.get(type).add(data);
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
