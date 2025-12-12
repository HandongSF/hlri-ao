package edu.handong.csee.isel.ao.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

import edu.handong.csee.isel.ao.Agent;
import edu.handong.csee.isel.proto.*;


public class AgentISA extends Agent {

    public AgentISA(Path config) throws IOException {
        super(config);
    }

    @Override
    protected void connect() throws UnknownHostException {
        client.connect(
                AgentInfo.newBuilder()
                         .setType(AgentInfo.AgentType.AT_ISA)
                         .setHost(InetAddress.getLocalHost().getHostAddress())
                         .setPort(server.getPort())
                         .build());
    }

    @Override
    protected RawAction calcRawAction(Data data) {
        return RawAction.newBuilder()
                        .setRawActionISA(RawActionISA.getDefaultInstance())
                        .build();
    }

    public static void main(String[] args) {
        try (Agent agent = new AgentISA(
                Path.of(AgentISA.class.getResource("/agent.json").toURI()))) {
            agent.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
