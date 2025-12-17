package edu.handong.csee.isel.ao.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonParser;

import edu.handong.csee.isel.ao.utils.TempData;
import edu.handong.csee.isel.ao.AgentOrchestrator;
import edu.handong.csee.isel.ao.policy.Router;
import edu.handong.csee.isel.ao.utils.RoutingConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class ScenarioRouter extends Router {
    private AgentInfo.AgentType[][] targets;
    private AgentOrchestrator subscriber;
    private Random random;
    private int idx;
   
    public ScenarioRouter(AgentOrchestrator ao, Path config) throws IOException {
        RoutingConfigExtractor extractor = new RoutingConfigExtractor(config);
        
        targets = extractor.getTargets();

        if (targets == null) {
            throw new IOException(
                    "Format of routing config file is not valid "
                            + "(check targets field");
        }

        subscriber = ao;
        idx = 0;
    }

    @Override
    public AgentInfo.AgentType[] route(List<TempData> data) {
        if (random.nextInt(10) < 2) {
            System.out.println("TEST");
            notify(false);

            return new AgentInfo.AgentType[] { 
                    AgentInfo.AgentType.forNumber(random.nextInt(3) + 1)};
        } else {
            System.out.println("TEST1");
            AgentInfo.AgentType[] target = targets[idx];

            notify(true);
            System.out.println("TEST2");
            idx = (idx + 1) % targets.length;
            
            return target;
        }
    }

    private void notify(boolean isSuccess) {
        subscriber.update(isSuccess);
    }
}