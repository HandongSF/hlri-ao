package edu.handong.csee.isel.ao.policy.route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

import edu.handong.csee.isel.ao.AgentOrchestrator;
import edu.handong.csee.isel.ao.policy.route.Router;
import edu.handong.csee.isel.ao.utils.RoutingConfigExtractor;
import edu.handong.csee.isel.ao.utils.TempData;
import edu.handong.csee.isel.proto.*;

public class ScenarioRouter extends Router {
    private AgentInfo.AgentType[][] labels;
    private boolean[] history;
    private AgentOrchestrator subscriber;
    private Random random;
    private int idx;
   
    public ScenarioRouter(AgentOrchestrator ao, Path config) 
            throws IOException {
        RoutingConfigExtractor extractor = new RoutingConfigExtractor(config);
        
        labels = extractor.extractTargets();

        if (labels == null) {
            throw new IOException(
                    "Format of routing config file is not valid "
                            + "(check targets field)");
        }

        history = new boolean[labels.length];
        subscriber = ao;
        random = new Random();
        idx = 0;
    }

    @Override
    public AgentInfo.AgentType[] route(List<TempData> data) {
        AgentInfo.AgentType[] prediction = labels[idx];

        if (random.nextInt(10) < 1) {
            notify(new AgentInfo.AgentType[] { 
                    AgentInfo.AgentType.AT_UNSPECIFIED });
            
            history[idx] = false;
        } else {
            notify(prediction);
            
            history[idx] = true;
        }

        idx = (idx + 1) % labels.length;
            
        if (idx == 0) {
            notify(history);
        }

        return prediction;
    }

    private void notify(AgentInfo.AgentType[] prediction) {
        subscriber.update(prediction);
    }

    private void notify(boolean[] history) {
        subscriber.update(history);
    }
}