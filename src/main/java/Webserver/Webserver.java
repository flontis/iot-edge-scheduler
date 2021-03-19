package Webserver;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import sensor.Sensor;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Webserver extends WebSocketServer {

    private static final int TCP_PORT = 4444;
    private Set<WebSocket> conns;
    private ArrayList<Sensor> sensors = new ArrayList<Sensor>();

    public Webserver() throws UnknownHostException {
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        conns.add(webSocket);
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Received Message");
        System.out.println(s);
        String[] messageArray = s.split(" ");
        int i;
        switch (messageArray[0]) {
            case "Sensors:":
                //todo pass auf, dass immer die total number of sensors gesetzt wird, sodass du nicht ewig addest, sondern auch sensors gestoppt werden
                // ggf. Paare aus Sensoren und Threads, um die Threads zu stoppen test
                for (i = 0; i < Integer.parseInt(messageArray[1]); i++) {
                    try {
                        Sensor sensor = new Sensor(2);
                        sensors.add(sensor);
                        new Thread(sensor).start();
                    } catch (InterruptedException e) {
                        System.out.println("Error");
                        e.printStackTrace();
                    }

                }
                System.out.println("Started " + String.valueOf(i) + " sensors.");
                break;
            case "Frequency:":
                for(Sensor sensor: sensors){
                    sensor.setN(Integer.parseInt(messageArray[1]));
                }
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println(e.getMessage());

    }
}
