package edu.handong.csee.isel.ao.examples.policy;

import java.io.IOException;
import java.nio.file.Path;

import edu.handong.csee.isel.ao.examples.utils.SchedulingConfigExtractor;
import edu.handong.csee.isel.ao.policy.Scheduler;

public class ScenarioScheduler extends Scheduler {
    private int[] numFrames;
    private int idx;

    public ScenarioScheduler(Path config) throws IOException {
        numFrames = new SchedulingConfigExtractor(config).extractNumFrames();

        if (numFrames == null) {
            System.out.println(
                    "Format of scheduling config file is not valid " 
                            + "(check numFrames field)");
        }

        idx = 0;
    }

    @Override
    public int nextNumFrame() {
        int numFrame = numFrames[idx];
        
        idx = (idx + 1) % numFrames.length;
        
        return numFrame;
    }
}
