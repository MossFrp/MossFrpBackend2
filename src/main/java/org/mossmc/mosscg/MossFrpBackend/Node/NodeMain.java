package org.mossmc.mosscg.MossFrpBackend.Node;

public class NodeMain {
    public static void initNodeModule() {
        NodeCache.refreshCache();
        NodePort.loadEmptyPort();
        NodeNumber.loadUsedNumber();
        NodeStatic.updateThread();
        NodeThread.runThread();
    }
}
