package edu.handong.csee.isel.ao.policy.schedule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import edu.handong.csee.isel.ao.policy.schedule.Scheduler;
import edu.handong.csee.isel.ao.utils.SchedulingConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class ScenarioScheduler extends Scheduler {
    private int[] numFrames;
    private Map<AgentInfo.AgentType, int[]> numFramesType;
    private Map<AgentInfo.AgentType, Integer> idxType;
    private int idx;

    public ScenarioScheduler(Path config) throws IOException {
        SchedulingConfigExtractor extractor 
                = new SchedulingConfigExtractor(config);

        numFrames = extractor.extractNumFramesOverall();

        if (numFrames == null) {
            throw new IOException(
                    "Format of scheduling config file is not valid " 
                            + "(check numFrames.overall field)");
        }

        idx = 0;

        numFramesType = new HashMap<>();
        idxType = new HashMap<>();

        for (AgentInfo.AgentType type : AgentInfo.AgentType.values()) {
            if (type != AgentInfo.AgentType.AT_UNSPECIFIED 
                    && type != AgentInfo.AgentType.UNRECOGNIZED) {
                numFramesType.put(type, extractor.extractNumFramesForType(type));
           
                if (numFramesType.get(type) == null) {
                    throw new IOException(
                            "Format of scheduling config file is not valid " 
                                    + "(check numFrames."
                                    +  type.name().replace("AT_", "") 
                                    + " field");
                }

                idxType.put(type, 0);
            }
        }
    }

    @Override
    public int nextNumFrames() {
        int numFrame = numFrames[idx];
        
        idx = (idx + 1) % numFrames.length;
        
        return numFrame;
    }

    @Override
    public int nextNumFramesForType(AgentInfo.AgentType type) {
        int[] numFrames = numFramesType.get(type);
        int idx = idxType.get(type);
        int numFrame = numFrames[idx];

        idxType.put(type, (idx + 1) % numFrames.length);

        return numFrame;
    }
}
