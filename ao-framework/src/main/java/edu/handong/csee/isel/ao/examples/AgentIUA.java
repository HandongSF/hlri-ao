package edu.handong.csee.isel.ao.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

import javax.xml.crypto.Data;

import edu.handong.csee.isel.ao.Agent;
import edu.handong.csee.isel.proto.*;

public class AgentIUA extends Agent {

    public AgentIUA(Path config) throws IOException {
        super(config);
    }

    @Override
    protected void connect() throws UnknownHostException {
        client.connect(
                AgentInfo.newBuilder()
                         .setType(AgentInfo.AgentType.AT_IUA)
                         .setHost(InetAddress.getLocalHost().getHostAddress())
                         .setPort(server.getPort())
                         .build());
    }

    @Override
    protected RawAction calcRawAction(Data data) {
        return RawAction.newBuilder()
                        .setRawActionIUA(RawActionIUA.getDefaultInstance())
                        .build();
    }

    public static void main(String[] args) {
        try (Agent agent = new AgentIUA(
                Path.of(AgentIUA.class.getResource("/agent.json").toURI()))) {
            agent.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
