package edu.handong.csee.isel.ao.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.Agent;
import edu.handong.csee.isel.proto.*;

public class AgentISA extends Agent {
    private Scenario scenario;
    private Logger logger;

    public AgentISA(Path networkConfig, Path scenarioConfig) 
            throws IOException {
        super(networkConfig);

        scenario = new Scenario(scenarioConfig);
        logger = LoggerFactory.getLogger(getClass());
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
        int currFrameNum;
        int scenarioFrameNum = scenario.nextFrameNum();
    
        do {
            currFrameNum = server.getData().getFrameNum();
        } while (currFrameNum < scenarioFrameNum);
        logger.info(
                "Calculating raw action (using up to {}th frame)", 
                currFrameNum, scenarioFrameNum);
        return scenario.currRawAction();
    }

    public static void main(String[] args) {
        Class<AgentISA> clazz = AgentISA.class;

        try (Agent agent = new AgentISA(
                Path.of(clazz.getResource("/isa-network.json").toURI()),
                Path.of(clazz.getResource("/isa-scenario.json").toURI()))) {
            agent.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
