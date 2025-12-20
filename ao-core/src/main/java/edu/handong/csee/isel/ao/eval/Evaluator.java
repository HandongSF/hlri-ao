package edu.handong.csee.isel.ao.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.handong.csee.isel.proto.*;

public abstract class Evaluator {
    protected Map<AgentInfo.AgentType, Map<Integer, Long>> records;
    protected long totalRespTime;
    protected long latestRespTime;
    protected int successRespCount;
    protected int respCount;
    protected long totalSyncTime;
    protected long latestSyncTime;
    protected int syncCount;
    protected int successRoutCount;
    protected int routCount;    
    protected int successTransCount;
    protected int transCount;

    public Evaluator() {
        records = new ConcurrentHashMap<>();
        
        totalRespTime = 0L;
        latestRespTime = 0L;
        successRespCount = 0;
        respCount = 0;

        totalRespTime = 0L;
        latestSyncTime = 0L;
        syncCount = 0;
        
        successRoutCount = 0;
        routCount = 0;

        successTransCount = 0;
        transCount = 0;
    }

    public void recordSendingTime(Data data) {
        records.get(
                AgentInfo.AgentType.forNumber(
                        data.getAgentDataCase().getNumber()))
               .put(data.getFrameNum(), System.currentTimeMillis());
    }

    public synchronized void evalRespAndAcc(RawAction action) {
        Map<Integer, Long> record = records.get(
                AgentInfo.AgentType.forNumber(
                        action.getAgentRawActionCase().getNumber()));
        
        latestRespTime = System.currentTimeMillis() 
                - record.get(action.getFrameNum());
        totalRespTime += latestRespTime;
                    
        evalAcc(action);
        
        respCount++;
    }

    public void summary() {
        System.out.print(
                "\n===================================== Evaluation "
                        + "=====================================\n");
        System.out.printf(
                "resp: %.2f(%d) ms, sync: %.2f(%d) ms, acc: %.2f %%, "
                        + "fit: %.2f %%, trans: %.2f %%\n", 
                ((float) totalRespTime) / respCount, latestRespTime,
                ((float) totalSyncTime) / syncCount, latestSyncTime,
                ((float) successRespCount) / respCount * 100,
                ((float) successRoutCount) / routCount * 100,
                ((float) successTransCount) / transCount * 100);
        System.out.print(
                "============================================================="
                        + "=========================\n");
    }

    public void addRecord(AgentInfo.AgentType type) {
        records.put(type, new HashMap<>());
    }

    public abstract void evalFit(AgentInfo.AgentType[] prediciton);
    public abstract void evalSync();
    public abstract void evalAcc(RawAction action);
    public abstract void evalTrans(boolean[] history);
}
