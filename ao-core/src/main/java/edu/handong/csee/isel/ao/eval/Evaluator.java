package edu.handong.csee.isel.ao.eval;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.utils.RoutingConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class Evaluator {
    private final Logger LOGGER = LoggerFactory.getLogger("Evaluator");
    
    private AgentInfo.AgentType[][] targets;
    private int[][] simScenarioPairs;
    private Map<AgentInfo.AgentType, Map<Integer, Long>> respRecords; 
    private Map<Integer, List<Long>> syncRecords;
    private float totalRespTime;
    private float latestRespTime;
    private float totalSyncTime;
    private float latestSyncTime;
    private int numSuccessResp;
    private int numSuccessRout;
    private int numSuccessTrans;
    private int scenarioCount;
    private int syncCount;
    private int routCount;
    private int transCount;
    private int numAgent;
    private int idx;

    public Evaluator(Path config, int numAgent) throws IOException {
        RoutingConfigExtractor extractor = new RoutingConfigExtractor(config);
        
        targets = extractor.getTargets(); 

        if (targets == null) {
            throw new IOException(
                    "Format of routing config file is not valid "
                            + "(check targets field)");
        }

        simScenarioPairs = extractor.getPairs();

        for (int[] simScenarioPair : simScenarioPairs) {
            for (int scenarioNum: simScenarioPair) {
                System.out.print(scenarioNum);
            }
            System.out.println();
        }
        
        if (simScenarioPairs == null) {
            throw new IOException(
                    "Format of routing config file is not valid "
                            + "(check pairs field)");
        }

        respRecords = new ConcurrentHashMap<>();
        syncRecords = new ConcurrentHashMap<>();

        totalRespTime = 0F;
        latestRespTime = 0F;
        totalSyncTime = 0F;
        latestSyncTime = 0F;
        numSuccessResp = 0;
        numSuccessRout = 0;
        numSuccessTrans = 0;
        scenarioCount = 0;
        syncCount = 0;
        transCount = 0;
        this.numAgent = numAgent;
        idx = 0;
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
        
        latestRespTime = System.currentTimeMillis() 
                - record.get(rawAction.getFrameNum());
        totalRespTime += latestRespTime;
       
        if (!(rawAction.getRawActionIsa()
                       .equals(RawActionISA.getDefaultInstance())
                && rawAction.getRawActionIua() 
                            .equals(RawActionIUA.getDefaultInstance())
                && rawAction.getRawActionIoa() 
                            .equals(RawActionIOA.getDefaultInstance()))) {
            numSuccessResp++;
        } else {
            LOGGER.info("Receiving wrong action from an agent");
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
            totalSyncTime += System.currentTimeMillis() - record.get(0);
            syncCount++;
        }   
    }

    public void evalRout(AgentInfo.AgentType[] decision) {
        AgentInfo.AgentType[] target = targets[idx];

        routCount++;

        idx = (idx + 1) % targets.length;

        if (decision.length != target.length) {
            LOGGER.info("Routing to a wrong agent");
            return;
        }

        for (int i = 0; i < decision.length; i++) {
            if (decision[i] != target[i]) {
                LOGGER.info("Routing to a wrong agent");
                return;
            }
        }

        numSuccessRout++;
    }

    public void evalTransfer(boolean[] history) {
        for (int[] simScenarioPair : simScenarioPairs) {
            if (history[simScenarioPair[0] - 1] 
                    && history[simScenarioPair[1] - 1]) {
                numSuccessTrans++;
            }

            transCount++;
        }
    }

    public void summary() {
        System.out.print("\n============================= Evaluation =============================\n");
        System.out.printf(
                "resp: %.2f(%.2f) ms, sync: %.2f ms, acc: %.2f %%, fit: %.2f %%, trans: %.2f %%\n", 
                totalRespTime / scenarioCount,
                latestRespTime,
                totalSyncTime / syncCount,
                ((float) numSuccessResp) / scenarioCount * 100,
                ((float) numSuccessRout) / routCount * 100,
                ((float) numSuccessTrans) / transCount * 100);
        System.out.print("======================================================================\n");
    }

    public void addRecord(AgentInfo.AgentType type) {
        respRecords.put(type, new HashMap<>());
    }
}
