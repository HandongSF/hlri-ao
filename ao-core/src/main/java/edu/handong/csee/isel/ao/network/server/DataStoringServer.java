package edu.handong.csee.isel.ao.network.server;

import java.io.IOException;
import java.util.LinkedList;
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
    private Server server;
    private DataStoringService service;
    private Logger logger;

    public DataStoringServer(int port) {
        service = new DataStoringService();
        server = ServerBuilder.forPort(port)
                              .addService(service)
                              .build();
        logger = LoggerFactory.getLogger(getClass());
    }
    
    private class DataStoringService 
            extends DataStoringGrpc.DataStoringImplBase {
        private Queue<Data> queue = new ConcurrentLinkedQueue<>();
        private ErrorHandler handler = new ErrorHandler();

        @Override
        public StreamObserver<Data> sendData(
                StreamObserver<Status> responseObserver) {
            logger.info("Receiving data from AO client");
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
        logger.info("Starting Agent server");
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
