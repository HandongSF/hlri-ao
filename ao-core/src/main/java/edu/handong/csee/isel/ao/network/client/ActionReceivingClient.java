package edu.handong.csee.isel.ao.network.client;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.proto.*;

public class ActionReceivingClient {
    private ActionReceivingGrpc.ActionReceivingBlockingStub stub;
    private Logger logger;

    public ActionReceivingClient(String host, int port) {
        stub = ActionReceivingGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress(host, port)
                                     .usePlaintext()
                                     .build());
        logger = LoggerFactory.getLogger(getClass());
    }

    public void sendRawAction(RawAction action) {
        logger.info("Sending raw action to AO server");
        stub.sendRawAction(action);
    }

    public void connect(AgentInfo info) {
        logger.info("Connecting to AO server");
        stub.connect(info);
    }

    public void shutdown() throws InterruptedException {
        ((ManagedChannel) stub.getChannel()).shutdown()
                                            .awaitTermination(
                                                    10, TimeUnit.MILLISECONDS);
    }
}
