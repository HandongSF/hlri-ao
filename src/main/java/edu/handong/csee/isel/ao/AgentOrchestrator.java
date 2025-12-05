package edu.handong.csee.isel.ao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.grpc.internal.JsonParser;

import edu.handong.csee.isel.ao.network.ActionCommandingServer;
import edu.handong.csee.isel.ao.network.DataFeedingServer;

public class AgentOrchestrator {
    private DataFeedingServer dfs;
    private ActionCommandingServer acs;

    public AgentOrchestrator(Path config) throws IOException {
        Object json = JsonParser.parse(Files.readString(config));
        
        if (!(json instanceof Map  
                && ((Map<String, ?>) json).get("port") instanceof Integer)) {
            throw new IOException("Formatm of the config file is invalid.");
        }

        dfs = new DataFeedingServer(
                this, ((Map<String, Integer>) json).get("port"));
        acs = new ActionCommandingServer();
    }

    public void listenRobot() {
        acs.start();
    }

    public void listenAgents() throws IOException {
        dfs.start();
    }

    public void updateActionCommandingServer(
            float linearX, float linearY, float linearZ, 
            float AngularX, float AngularY, float AngularZ) {

    }

    public void updateActionCommandingServer() {

    }

    public static void main(String[] args) {
        
    }
}
