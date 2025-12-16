package edu.handong.csee.isel.ao.examples;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import edu.handong.csee.isel.ao.examples.utils.ScenarioConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class Scenario {
    private int[] frameNums;
    private RawAction[] rawActions;
    private AgentInfo.AgentType type;
    private Random random;
    private int idx;

    public Scenario(Path config) throws IOException {
        ScenarioConfigExtractor extractor = new ScenarioConfigExtractor(config);
        AgentInfo.AgentType type;

        frameNums = extractor.getFrameNums();

        if (frameNums == null) {
            throw new IOException(
                    "Format of scenario config file is not valid "
                            + "(check frameNums field)");
        }

        type = extractor.getAgentType();

        if (type == null) {
            throw new IOException(
                    "Format of scenario config file is not valid "
                            + "(check type field)");
        }

        rawActions = extractor.getRawActions(type);

        if (rawActions == null) {
            throw new IOException(
                    "Format of scenario config file is not valid "
                            + "(check rawActions field)");
        }

        random = new Random();

        idx = -1;
    }

    public int nextFrameNum() {
        idx = (idx + 1) % frameNums.length;
        
        return frameNums[idx];
    }

    public RawAction currRawAction() {
        return random.nextInt(10) == 0 
                ? RawAction.newBuilder()
                           .setFrameNum(frameNums[idx])
                           .build()
                : rawActions[idx];
    }
}
