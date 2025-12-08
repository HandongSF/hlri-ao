package edu.handong.csee.isel.ao.network.server;

import java.io.IOException;

import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import edu.handong.csee.isel.ao.AgentOrchestrator;
import edu.handong.csee.isel.ao.network.ErrorHandler;
import edu.handong.csee.isel.proto.*;

public class ActionReceivingServer {
    private Server server;
    private AgentOrchestrator subscriber;

    public ActionReceivingServer(AgentOrchestrator ao, int port) {
        server = ServerBuilder.forPort(port)
                              .addService(new ActionReceivingService())
                              .build();
        subscriber = ao;
    }

    private class ActionReceivingService 
            extends ActionReceivingGrpc.ActionReceivingImplBase {
        private ErrorHandler handler = new ErrorHandler();

        @Override
        public void connect(
                AgentInfo request, 
                StreamObserver<Status> responseObserver) {
            AgentInfo.AgentType type = request.getType();

            if (type == AgentInfo.AgentType.AT_UNSPECIFIED) {
                handler.handle(
                        "The type of agent is not specified.", 
                        Code.INVALID_ARGUMENT, 
                        "AGENT_TYPE_UNSPECIFIED", 
                        "ao.isel.csee.handong.edu", 
                        responseObserver);
                    
                return;
            } else if (type == AgentInfo.AgentType.UNRECOGNIZED) {
                handler.handle(
                        "The type of an agent is not recognized.",
                        Code.INVALID_ARGUMENT,
                        "UNRECOGNIZED",
                        "ao.isel.csee.handong.edu",
                        responseObserver);
                
                return;
            }

            notifySubscriber(type, request.getHost(), request.getPort());

            responseObserver.onNext(
                    Status.newBuilder()
                          .setCode(Code.OK_VALUE)
                          .build());
            responseObserver.onCompleted();
        }

        @Override 
        public void sendRawAction(
                RawAction action, StreamObserver<Status> responseObserver) {
            if (action.getAgentRawActionCase() 
                    == RawAction.AgentRawActionCase.AGENTRAWACTION_NOT_SET) {
                handler.handle(
                        "Raw action from an agent is not set.", 
                        Code.INVALID_ARGUMENT, 
                        "AGENT_RAW_ACTION_NOT_SET", 
                        "ao.isel.csee.handong.edu", 
                        responseObserver);
                        
                return;
            }

            notifySubscriber(action);

            responseObserver.onNext(
                    com.google.rpc.Status.newBuilder()
                                         .setCode(Code.OK_VALUE)
                                         .build());
            responseObserver.onCompleted();
        }

    }

    public void start() throws IOException {
        server.start();
    }

    public void shutdownNow() {
        server.shutdownNow();
    }

    public void notifySubscriber(
            AgentInfo.AgentType type, String host, int port) {
        subscriber.update(type, host, port);
    }

    public void notifySubscriber(RawAction action) {
        switch (action.getAgentRawActionCase()) {
            case RAW_ACTION_ISA:
                RawActionISA actionISA = action.getRawActionISA();
                Linear linear = actionISA.getLinear();
                Angular angular = actionISA.getAngular();

                subscriber.update(
                    linear.getX(), linear.getY(), linear.getZ(),
                    angular.getX(), angular.getY(), angular.getZ());
                
                break;

            case RAW_ACTION_IUA:
                subscriber.update();
                
                break;

            case RAW_ACTION_IOA:
                subscriber.update();
                
                break;

            default:
        }
    }
}
