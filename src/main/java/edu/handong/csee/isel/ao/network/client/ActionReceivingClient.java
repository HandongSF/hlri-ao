package edu.handong.csee.isel.ao.network.client;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import edu.handong.csee.isel.proto.*;

public class ActionReceivingClient {
    private ActionReceivingGrpc.ActionReceivingBlockingStub stub;

    public ActionReceivingClient(String host, int port) {
        stub = ActionReceivingGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress(host, port).build());
    }

    public void sendRawAction(RawAction action) {
        stub.sendRawAction(action);
    }

    public void connect(AgentInfo info) {
        stub.connect(info);
    }

    public void shutdown() throws InterruptedException {
        ((ManagedChannel) stub.getChannel()).shutdown()
                                            .awaitTermination(
                                                    10, TimeUnit.MILLISECONDS);
    }
}
