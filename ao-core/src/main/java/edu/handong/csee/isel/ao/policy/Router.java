package edu.handong.csee.isel.ao.policy;

import java.util.List;

import edu.handong.csee.isel.ao.utils.TempData;
import edu.handong.csee.isel.proto.*;

public abstract class Router {
    
    public abstract AgentInfo.AgentType[] route(List<TempData> data);
}
