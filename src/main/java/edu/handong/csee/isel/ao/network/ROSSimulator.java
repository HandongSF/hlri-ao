package edu.handong.csee.isel.ao.network;

import edu.handong.csee.isel.ao.AgentOrchestrator;

public class ROSSimulator {
    private AgentOrchestrator subscriber;

    public ROSSimulator(AgentOrchestrator ao) {
        subscriber = ao;
    }

    public void start() {

    }

    public void sendAction(String action) {
        System.out.print(action);
    }
}
