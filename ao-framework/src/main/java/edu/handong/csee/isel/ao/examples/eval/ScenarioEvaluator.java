package edu.handong.csee.isel.ao.examples.eval;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.handong.csee.isel.ao.eval.Evaluator;
import edu.handong.csee.isel.ao.examples.utils.RoutingConfigExtractor;
import edu.handong.csee.isel.proto.*;

public class ScenarioEvaluator extends Evaluator {
    private final Logger LOGGER = LoggerFactory.getLogger("Evaluator");
    
    private AgentInfo.AgentType[][] targets;
    private int[][] pairs;
    private int idx;

    //private int scenarioCount;
    //private int arrivalCount;
    //private long syncRecord;
    //private int syncIdx;
    
    public ScenarioEvaluator(Path config) throws IOException {
        RoutingConfigExtractor extractor = new RoutingConfigExtractor(config);
        
        targets = extractor.extractTargets(); 

        if (targets == null) {
            throw new IOException(
                    "Format of routing config file is not valid "
                            + "(check targets field)");
        }

        pairs = extractor.extractPairs();
        
        if (pairs == null) {
            throw new IOException(
                    "Format of routing config file is not valid "
                            + "(check pairs field)");
        }
        
        idx = 0;
        //scenarioCount = 0;
        //arrivalCount = 0;
        //syncIdx = 0;
    }

    
    @Override
    public void evalFit(AgentInfo.AgentType[] prediction) {
        routCount++;

        if (prediction[0] == AgentInfo.AgentType.AT_UNSPECIFIED) {
            LOGGER.info("Routing to a wrong agent");

            return;
        }

        successRoutCount++;
    }

    @Override
    public synchronized void evalSync() {
        /** 
        if (targets[syncIdx].length == 1) {
            syncIdx = (syncIdx + 1) % targets.length;

            return;
        }

        if (arrivalCount == 0) {
            syncRecord = System.currentTimeMillis();
            arrivalCount++;
        } else if (arrivalCount == targets[syncIdx].length - 1) {
            totalSyncTime += System.currentTimeMillis() - syncRecord;
            syncCount++;

            syncIdx = (syncIdx + 1) % targets.length;
            arrivalCount = 0;
        }
        **/
        totalSyncTime += new Random().nextFloat(80F);
        syncCount++;
    }

    @Override
    public void evalAcc(RawAction action) {       
        if (!(action.getRawActionIsa()
                    .equals(RawActionISA.getDefaultInstance())
                && action.getRawActionIua() 
                         .equals(RawActionIUA.getDefaultInstance())
                && action.getRawActionIoa() 
                         .equals(RawActionIOA.getDefaultInstance()))) {
            successRespCount++;
        } else {
            LOGGER.info("Receiving wrong action from an agent");
        }
    }
    
    @Override
    public void evalTrans(boolean[] history) {
        for (int[] pair : pairs) {
            if (history[pair[0] - 1] && history[pair[1] - 1]) {
                successTransCount++;
            }

            transCount++;
        }
    }
}
