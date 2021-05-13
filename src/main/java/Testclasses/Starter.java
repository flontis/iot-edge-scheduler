package Testclasses;

import EdgeServer.ESWebSocketServer;
import ResourceAdministrator.ResourceMonitor;

public class Starter {
    public static void main(String[] args) {
        ESWebSocketServer es = new ESWebSocketServer();
        ResourceMonitor rm = new ResourceMonitor();
    }

}
