package edu.handong.csee.isel.ao.examples.policy;

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

    public int lastFrameNum() {
        return frameNums[frameNums.length - 1];
    }

    public RawAction currRawAction() {
        if (random.nextInt(10) == 0) {
            RawAction.Builder builder = RawAction.newBuilder();

            switch (type) {
                case AT_ISA:
                    builder = builder.setRawActionIsa(
                            RawActionISA.getDefaultInstance());
                    
                    break;
                
                case AT_IUA:
                    builder = builder.setRawActionIua(
                            RawActionIUA.getDefaultInstance());
                    
                    break;
                
                case AT_IOA:
                    builder = builder.setRawActionIoa(
                            RawActionIOA.getDefaultInstance());
                    
                    break;
                
                default:
            }

            return builder.setFrameNum(frameNums[idx]).build();
        } else {
            return rawActions[idx];
        }
    }

    public void reset() {
        idx = -1;
    }
}
