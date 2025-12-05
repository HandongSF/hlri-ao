package edu.handong.csee.isel.ao.network;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.google.rpc.Code;

import edu.handong.csee.isel.ao.AgentOrchestrator;
import edu.handong.csee.isel.proto.*;

public class DataFeedingServer {
    private DataFeedingService service;
    private Server server;
    private AgentOrchestrator subscriber;
    
    public DataFeedingServer(AgentOrchestrator ao, int port) {
        service = new DataFeedingService();
        server = ServerBuilder.forPort(port)
                              .addService(service)
                              .build();
        subscriber = ao;
    }
    
    private class DataFeedingService 
            extends DataFeedingGrpc.DataFeedingImplBase {
        private Queue<RGBD> rgbds = new LinkedList<>();
        private Queue<IMU> imus = new LinkedList<>(); 
        private Queue<UserCmd> cmds = new LinkedList<>();
        private Queue<Voice> voices = new LinkedList<>();
        private Queue<Tectile> tectiles = new LinkedList<>();
        private ErrorHandler handler = new ErrorHandler();

        @Override
        public void getData(AgentInfo request, 
                            StreamObserver<Data> responseObserver) {
            Data.Builder builder = Data.newBuilder();

            switch (request.getType()) {
                case AT_UNSPECIFIED:
                    handler.handle("The type of an agent is not specified",
                                   Code.INVALID_ARGUMENT,
                                   "AGENT_TYPE_UNSPECIFIED",
                                   "ao.isel.csee.handong.edu",
                                   responseObserver);
                    
                    return;

                case AT_ISA:
                    builder.getDataISABuilder()
                           .setRgbd(rgbds.element())
                           .setImu(imus.element())
                           .setCmd(cmds.element());     
                   
                    break;

                case AT_IUA:
                    builder.getDataIUABuilder()
                           .setRgbd(rgbds.element())
                           .setVoice(voices.element())
                           .setTectile(tectiles.element());                    
                    
                    break;

                case AT_IOA:
                    break;
                
                case UNRECOGNIZED:
                    handler.handle("Unknown error occured.", 
                                   Code.UNKNOWN, 
                                   "UNKNOWN_ERROR", 
                                   "ao.isel.csee.handong.edu", 
                                   responseObserver);

                    return;
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }
        
        @Override 
        public void sendRawAction(
                RawAction action, 
                StreamObserver<com.google.rpc.Status> responseObserver) {

            if (action.getAgentRawActionCase() 
                    == RawAction.AgentRawActionCase.AGENTRAWACTION_NOT_SET) {
                handler.handle("Raw action from an agent is not set.", 
                               Code.INVALID_ARGUMENT, 
                               "AGENT_RAW_ACTION_NOT_SET", 
                               "ao.isel.csee.handong.edu", 
                               responseObserver);
                        
                return;
            }

            responseObserver.onNext(
                    com.google.rpc.Status.newBuilder()
                                         .setCode(Code.OK_VALUE)
                                         .build());
            responseObserver.onCompleted();

            notifySubscriber(action);
        }

        public void addRGBD(RGBD rgbd) {
            rgbds.add(rgbd);
        }

        public void addImu(IMU imu) {
            imus.add(imu);
        }

        public void addUserCmd(UserCmd cmd) {
            cmds.add(cmd);
        }
    }

    public Server start() throws IOException {
        return server.start();
    }

    public void addRGBD(RGBD rgbd) {
        service.addRGBD(rgbd);
    }

    public void addImu(IMU imu) {
        service.addImu(imu);
    }

    public void addUserCmd(UserCmd cmd) {
        service.addUserCmd(cmd);
    }

    public void notifySubscriber(RawAction action) {
        switch (action.getAgentRawActionCase()) {
            case RAW_ACTION_ISA:
                RawActionISA actionISA = action.getRawActionISA();
                Linear linear = actionISA.getLinear();
                Angular angular = actionISA.getAngular();

                subscriber.updateActionCommandingServer(
                    linear.getX(), linear.getY(), linear.getZ(),
                    angular.getX(), angular.getY(), angular.getZ());
                
                break;

            case RAW_ACTION_IUA:
                subscriber.updateActionCommandingServer();
                
                break;

            case RAW_ACTION_IOA:
                subscriber.updateActionCommandingServer();
                
                break;

            default:
        }
    }
}
