package ResourceAdministrator;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ResourceWebserverStarter extends WebSocketClient {


    public ResourceWebserverStarter(URI serverURI) {
        super(serverURI);
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("New connection established");
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
    e.printStackTrace();
    }

    public static void main(String[] args) throws URISyntaxException {
        ResourceWebserverStarter webserver = new ResourceWebserverStarter(new URI("ws://localhost:4444"));
        webserver.connect();
    }
}
