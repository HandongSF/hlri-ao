package edu.handong.csee.isel.ao.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.handong.csee.isel.proto.*;

public class Evaluator {
    private ConcurrentMap<AgentInfo.AgentType, Map<Integer, Long>> records;
    private float totalInterval;
    private int numSuccess;
    private int numScenarios;

    public Evaluator() {
        records = new ConcurrentHashMap<>();
        
        totalInterval = 0;
        numSuccess = 0;
        numScenarios = 0;
    }

    public void record(Data data) {
        records.get(
                    AgentInfo.AgentType.forNumber(
                            data.getAgentDataCase().getNumber()))
               .put(data.getFrameNum(), System.currentTimeMillis());
    }

    public void evaluate(RawAction rawAction) {
        Map<Integer, Long> record = records.get(
                AgentInfo.AgentType.forNumber(
                        rawAction.getAgentRawActionCase().getNumber()));

        totalInterval += System.currentTimeMillis() 
                - record.get(rawAction.getFrameNum());

        if (rawAction.getAgentRawActionCase() 
                != RawAction.AgentRawActionCase.AGENTRAWACTION_NOT_SET) {
            numSuccess++;
        }
        
        numScenarios++;
    }

    public void addRecord(AgentInfo.AgentType type) {
        records.put(type, new HashMap<>());
    }

    public float getAvgReactionTime() {
        return totalInterval / numScenarios;
    }

    public float getAccuracy() {
        return ((float) numSuccess) / numScenarios;
    }
}
