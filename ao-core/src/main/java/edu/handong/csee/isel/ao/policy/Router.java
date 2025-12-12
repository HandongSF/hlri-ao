package edu.handong.csee.isel.ao.policy;

import java.util.Collection;
import java.util.List;

import edu.handong.csee.isel.proto.*;

public class Router {
    
    public Collection<AgentInfo.AgentType> route(
            byte[] image, byte[] depth, float accel, float angular, 
            float mag_str_x, float mag_str_y, String target, String text,
            byte[] header, byte[] format, byte[] data) { 
        return List.of(AgentInfo.AgentType.AT_ISA, AgentInfo.AgentType.AT_IUA);
    }
}
