package edu.handong.csee.isel.ao.network.client;

import java.util.Map;
import java.util.Queue;

import com.google.rpc.Status;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import edu.handong.csee.isel.proto.*;

public class DataStoringClient {
    private Map<AgentInfo.AgentType, DataStoringGrpc.DataStoringStub> stubs;
    private Queue<RGBD> rgbds;
    private Queue<IMU> imus;
    private Queue<UserCmd> cmds;
    private Queue<Voice> voices;
    private Queue<Tectile> tectiles;
    
    public void sendData(AgentInfo.AgentType key, int frames) {
        if (key == AgentInfo.AgentType.AT_UNSPECIFIED 
                || key == AgentInfo.AgentType.UNRECOGNIZED) {
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
                = stubs.get(key).sendData(responseObserver);
        Data.Builder dataBuilder = Data.newBuilder();

        switch (key) {
            case AT_ISA:
                DataISA.Builder dataISABuilder 
                        = dataBuilder.getDataISABuilder();

                for (int i = 1; i <= frames; i++) {
                    dataISABuilder.setRgbd(rgbds.peek())
                                  .setImu(imus.peek())
                                  .setCmd(cmds.peek());
                    requestObserver.onNext(dataBuilder.build());
                }

                break;
            
            case AT_IUA:
                DataIUA.Builder dataIUABuilder 
                        = dataBuilder.getDataIUABuilder();

                for (int i = 1; i <= frames; i++) {
                    dataIUABuilder.setRgbd(rgbds.peek())
                                  .setVoice(voices.peek())
                                  .setTectile(tectiles.peek());
                    requestObserver.onNext(dataBuilder.build());
                }

                break;
            
            case AT_IOA:

            default:
        }

        requestObserver.onCompleted();
    }

    public void addStub(AgentInfo.AgentType key, String host, int port) {
        stubs.put(
                key, 
                DataStoringGrpc.newStub(
                        ManagedChannelBuilder.forAddress(host, port).build()));
    }

    public void addRGBD(RGBD rgbd) {
        rgbds.add(rgbd);
    }

    public void addIMU(IMU rgbd) {
        imus.add(rgbd);
    }

    public void addRGBD(UserCmd cmd) {
        cmds.add(cmd);
    }

    public void addVoice(Voice voice) {
        voices.add(voice);
    }

    public void addTectile(Tectile tectile) {
        tectiles.add(tectile);
    }

    public void shutdownNow() {
        for (DataStoringGrpc.DataStoringStub stub : stubs.values()) {
            ((ManagedChannel) stub.getChannel()).shutdownNow();    
        }
    }
}
