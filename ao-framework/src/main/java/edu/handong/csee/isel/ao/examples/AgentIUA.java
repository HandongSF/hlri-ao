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

public class AgentIUA extends Agent {
    private Scenario scenario;

    public AgentIUA(Path networkConfig, Path scenarioConfig) throws IOException {
        super(networkConfig, "IUA");

        scenario = new Scenario(scenarioConfig, AgentInfo.AgentType.AT_IUA);
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
    protected RawAction calcRawAction() {
        int dataFrameNum;
        int scenarioFrameNum = scenario.nextFrameNum(); 

        do {
            dataFrameNum = server.getData().getFrameNum();
        } while (dataFrameNum < scenarioFrameNum);
        
        return scenario.currRawAction();
    }

    public static void main(String[] args) {
        Class<AgentIUA> clazz = AgentIUA.class;

        try (Agent agent = new AgentIUA(
                Path.of(clazz.getResource("/iua-network.json").toURI()),
                Path.of(clazz.getResource("/iua-scenario.json").toURI()))) {
            agent.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
