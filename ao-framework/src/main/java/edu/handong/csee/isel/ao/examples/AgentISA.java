package edu.handong.csee.isel.ao.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.Agent;
import edu.handong.csee.isel.ao.examples.policy.Scenario;
import edu.handong.csee.isel.proto.*;

public class AgentISA extends Agent {
    private Scenario scenario;

    public AgentISA(Path networkConfig, Path scenarioConfig) 
            throws IOException {
        super(networkConfig, "ISA");

        scenario = new Scenario(scenarioConfig, AgentInfo.AgentType.AT_ISA);
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
    protected RawAction calcRawAction() {
        int dataFrameNum;
        int scenarioFrameNum = scenario.nextFrameNum(); 

        do {
            dataFrameNum = server.getData().getFrameNum();
        } while (dataFrameNum < scenarioFrameNum);
        
        return scenario.currRawAction();
    }

    public static void main(String[] args) {
        try (Agent agent = new AgentISA(
                Path.of(AgentISA.class.getResource("/isa-network.json")
                                      .toURI()),
                Path.of(AgentISA.class.getResource("/isa-scenario.json")
                                      .toURI()))) {
            agent.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
