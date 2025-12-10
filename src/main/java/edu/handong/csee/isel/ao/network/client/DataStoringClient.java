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

import edu.handong.csee.isel.proto.*;

public class DataStoringClient {
    private Map<AgentInfo.AgentType, DataStoringGrpc.DataStoringStub> stubs 
            = new HashMap<>();
    private Map<AgentInfo.AgentType, Queue<Data>> queues = new HashMap<>();
    
    public void sendData(AgentInfo.AgentType type, int frames) {        
        if (type == AgentInfo.AgentType.AT_UNSPECIFIED 
                || type == AgentInfo.AgentType.UNRECOGNIZED) {
            return;
        }
        
        StreamObserver<Status> responseObserver = new StreamObserver<>() {
            
            @Override
            public void onNext(Status value) {

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
        stubs.put(
                key, 
                DataStoringGrpc.newStub(
                        ManagedChannelBuilder.forAddress(host, port).build()));
        queues.put(key, new LinkedList<Data>());
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
