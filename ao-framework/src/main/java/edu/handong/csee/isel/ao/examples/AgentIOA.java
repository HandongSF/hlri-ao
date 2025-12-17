package edu.handong.csee.isel.ao.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.Agent;
import edu.handong.csee.isel.proto.*;

public class AgentIOA extends Agent {
    private Scenario scenario;

    public AgentIOA(Path networkConfig, Path scenarioConfig) 
            throws IOException {
        super(networkConfig, "IOA");

        scenario = new Scenario(scenarioConfig);
    }

    @Override
    protected void connect() throws UnknownHostException {
        client.connect(
                AgentInfo.newBuilder()
                         .setType(AgentInfo.AgentType.AT_IOA)
                         .setHost(InetAddress.getLocalHost().getHostAddress())
                         .setPort(server.getPort())
                         .build());
    }

    @Override
    protected RawAction calcRawAction() {
        int currFrameNum;
        int scenarioFrameNum = scenario.nextFrameNum();
    
        do {
            currFrameNum = server.getData().getFrameNum();
        } while (currFrameNum != scenarioFrameNum);
        
        return scenario.currRawAction();
    }

    public static void main(String[] args) {
        Class<AgentIOA> clazz = AgentIOA.class;

        try (Agent agent = new AgentIOA(
                Path.of(clazz.getResource("/ioa-network.json").toURI()),
                Path.of(clazz.getResource("/ioa-scenario.json").toURI()))) {
            agent.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
