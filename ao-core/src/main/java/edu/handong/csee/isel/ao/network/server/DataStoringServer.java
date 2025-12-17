package edu.handong.csee.isel.ao.network.server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.network.ErrorHandler;
import edu.handong.csee.isel.proto.*;

public class DataStoringServer {
    private final Logger LOGGER; 

    private Server server;
    private DataStoringService service;

    public DataStoringServer(int port, String name) {
        LOGGER = LoggerFactory.getLogger(name + "-server");

        service = new DataStoringService();
        server = ServerBuilder.forPort(port)
                              .addService(service)
                              .build();
    }
    
    private class DataStoringService 
            extends DataStoringGrpc.DataStoringImplBase {
        private Queue<Data> queue = new ConcurrentLinkedQueue<>();
        private ErrorHandler handler = new ErrorHandler();

        @Override
        public StreamObserver<Data> sendData(
                StreamObserver<Status> responseObserver) {
            return new StreamObserver<Data>() {
                
                @Override 
                public void onNext(Data data) {
                    if (data.getAgentDataCase() 
                            == Data.AgentDataCase.AGENTDATA_NOT_SET) {
                        onError(new Exception("Agent data is not set."));

                        return;
                    }

                    queue.add(data);
                }

                @Override 
                public void onError(Throwable t) {
                    handler.handle(
                            t.getMessage(), 
                            Code.INVALID_ARGUMENT, 
                            "AGENT_DATA_NOT_SET", 
                            "ao.isel.csee.handong.edu", 
                            responseObserver);
                }

                @Override
                public void onCompleted() {
                    responseObserver.onNext(
                            Status.newBuilder()
                                  .setCode(Code.OK_VALUE)
                                  .build());
                    responseObserver.onCompleted();
                }
            };    
        }
        
        public Data getData() {
            while (queue.isEmpty());

            return queue.poll();
        }
    }

    public Server start() throws IOException {
        LOGGER.info("Starting Agent server");
        return server.start();
    }

    public void shutdown() throws InterruptedException {
        server.shutdown().awaitTermination();
    }

    public int getPort() {
        return server.getPort();
    }

    public Data getData() {
        return service.getData();
    }
}
