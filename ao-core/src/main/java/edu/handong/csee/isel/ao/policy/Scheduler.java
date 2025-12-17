package edu.handong.csee.isel.ao.policy;

import java.io.IOException;
import java.nio.file.Path;

import edu.handong.csee.isel.ao.utils.RoutingConfigExtractor;

public class Scheduler {
    private int[] numFrames;
    private int idx;

    public Scheduler(Path config) throws IOException {
        numFrames = new RoutingConfigExtractor(config).getFrames();

        if (numFrames == null) {
            System.out.println("TEST");
        }

        idx = 0;
    }

    public int nextNumFrame() {
        int numFrame = numFrames[idx];
        
        idx = (idx + 1) % numFrames.length;
        
        return numFrame;
    }
}
