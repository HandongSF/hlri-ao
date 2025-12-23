package edu.handong.csee.isel.ao.policy.schedule;

import edu.handong.csee.isel.proto.*;

public abstract class Scheduler {

    public abstract int nextNumFrames();
    public abstract int nextNumFramesForType(AgentInfo.AgentType type);
}
