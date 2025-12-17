package edu.handong.csee.isel.ao.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.proto.*;

public class Evaluator {
    private final Logger LOGGER = LoggerFactory.getLogger("Evaluator");
    
    private Map<AgentInfo.AgentType, Map<Integer, Long>> respRecords; 
    private Map<Integer, List<Long>> syncRecords;
    private Logger logger;
    private float totalRespTime;
    private float totalSyncTime;
    private int numSuccessResp;
    private int numSuccessRout;
    private int scenarioCount;
    private int syncCount;
    private int routCount;
    private int numAgent;

    public Evaluator(int numAgent) {
        respRecords = new ConcurrentHashMap<>();
        syncRecords = new ConcurrentHashMap<>();
        totalRespTime = 0F;
        totalSyncTime = 0F;
        numSuccessResp = 0;
        numSuccessRout = 0;
        scenarioCount = 0;
        syncCount = 0;
        this.numAgent = numAgent;
    }

    public void record(Data data) {
        Map<Integer, Long> record = respRecords.get(
                AgentInfo.AgentType.forNumber(
                        data.getAgentDataCase().getNumber()));
     
        record.put(data.getFrameNum(), System.currentTimeMillis());
    }

    public synchronized void evalResp(RawAction rawAction) {
        Map<Integer, Long> record = respRecords.get(
                AgentInfo.AgentType.forNumber(
                        rawAction.getAgentRawActionCase().getNumber()));

        totalRespTime += System.currentTimeMillis() 
                - record.get(rawAction.getFrameNum());
        
        if (!(rawAction.getRawActionIsa()
                       .equals(RawActionISA.getDefaultInstance())
                && rawAction.getRawActionIua() 
                            .equals(RawActionIUA.getDefaultInstance())
                && rawAction.getRawActionIoa() 
                            .equals(RawActionIOA.getDefaultInstance()))) {
            numSuccessResp++;
        }

        scenarioCount++;
    }

    public synchronized void evalSync(int key) {
        List<Long> record = syncRecords.get(key);

        if (record == null) {
            syncRecords.put(key, new ArrayList<>());
            record = syncRecords.get(key);
        } 

        if (record.size() < numAgent - 1) {
            record.add(System.currentTimeMillis());
        } else if (record.size() == numAgent - 1) {
            totalSyncTime += System.currentTimeMillis() - record.getFirst();
            syncCount++;
        }   
    }

    public void evalRout(boolean isSuccess) {
        numSuccessRout += isSuccess ? 1 : 0;
        routCount++;
    }

    public void summary() {
        System.out.print("\n======================= Evaluation =======================\n");
        System.out.printf(
                "resp: %.2f ms, sync: %.2f ms, acc: %.2f %%, fit: %.2f %%\n", 
                totalRespTime / scenarioCount,
                totalSyncTime / syncCount,
                ((float) numSuccessResp) / scenarioCount * 100,
                ((float) numSuccessRout) / routCount * 100);
        System.out.print("==========================================================\n");
    }

    public void addRecord(AgentInfo.AgentType type) {
        respRecords.put(type, new HashMap<>());
    }
}
