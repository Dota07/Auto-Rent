package com.autorent.application;

public class PasoTraza {
    private final String nodeId;
    private final String logMsg;

    public PasoTraza(String nodeId, String logMsg) {
        this.nodeId = nodeId;
        this.logMsg = logMsg;
    }

    public String getNodeId() { return nodeId; }
    public String getLogMsg() { return logMsg; }
}
