package edu.handong.csee.isel.ao.examples.policy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

import edu.handong.csee.isel.ao.utils.TempData;
import edu.handong.csee.isel.ao.AgentOrchestrator;
import edu.handong.csee.isel.ao.policy.Router;
import edu.handong.csee.isel.ao.utils.RoutingConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class ScenarioRouter extends Router {
    private final int NUM_AGENT = 3;

    private AgentInfo.AgentType[][] targets;
    private boolean[] history;
    private AgentOrchestrator subscriber;
    private Random random;
    private int idx;
   
    public ScenarioRouter(AgentOrchestrator ao, Path config) 
            throws IOException {
        RoutingConfigExtractor extractor = new RoutingConfigExtractor(config);
        
        targets = extractor.getTargets();

        if (targets == null) {
            throw new IOException(
                    "Format of routing config file is not valid "
                            + "(check targets field)");
        }

        history = new boolean[targets.length];
        subscriber = ao;
        random = new Random();
        idx = 0;
    }

    @Override
    public AgentInfo.AgentType[] route(List<TempData> data) {
        AgentInfo.AgentType[] decision;

        if (random.nextInt(10) < 2) {
            decision = new AgentInfo.AgentType[] { 
                    AgentInfo.AgentType.forNumber(
                            random.nextInt(NUM_AGENT) + 1)};
            history[idx] = false;
        } else {
            decision = targets[idx];
            history[idx] = true;
        }

        notify(decision);

        idx = (idx + 1) % targets.length;
            
        if (idx == 0) {
            System.out.println("TEST");
            notify(history);
        }

        return decision;
    }

    private void notify(AgentInfo.AgentType[] decision) {
        subscriber.update(decision);
    }

    private void notify(boolean[] history) {
        subscriber.update(history);
    }
}