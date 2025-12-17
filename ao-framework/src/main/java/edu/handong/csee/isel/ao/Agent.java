package edu.handong.csee.isel.ao;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import edu.handong.csee.isel.ao.network.client.ActionReceivingClient;
import edu.handong.csee.isel.ao.network.server.DataStoringServer;
import edu.handong.csee.isel.ao.utils.NetworkConfigExtractor;
import edu.handong.csee.isel.proto.*;

public abstract class Agent implements AutoCloseable {
    protected DataStoringServer server;
    protected ActionReceivingClient client;

    public Agent(Path config, String name) throws IOException {
        NetworkConfigExtractor extractor = new NetworkConfigExtractor(config);
        Integer serverPort = extractor.getServerPort();
        String clientHost = extractor.getClientHost();
        Integer clientPort = extractor.getClientPort();

        if (serverPort == null || clientHost == null || clientPort == null) {
            throw new IOException(
                    "Format of network config file is not valid");
        }

        server = new DataStoringServer(serverPort, name);
        client = new ActionReceivingClient(clientHost, clientPort, name);
    }

    public void run() throws IOException {
        server.start();
        connect();

        while (true) {
            client.sendRawAction(calcRawAction());
        }
    }

    protected abstract void connect() throws UnknownHostException;

    protected abstract RawAction calcRawAction();

    @Override
    public void close() throws InterruptedException {
        server.shutdown();
        client.shutdown();
    }
}
