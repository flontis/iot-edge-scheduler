package EdgeServer;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ESWebSocketServer extends WebSocketServer {

    private static final KeyGenerator.Algorithm cryptoAlgo= KeyGenerator.Algorithm.AES;
    private static final int TCP_PORT = 4444;
    private Set<WebSocket> conns;
    private WebSocket ICS;
    private String currentCapacityReport;
    private final KeyGenerator keygen;
    private SecretKey sharedKey;
    private PriorityQueue<EdgeNodeEntry> edgeNodes = new PriorityQueue<>();


    public ESWebSocketServer(){
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();
        keygen = new KeyGenerator(cryptoAlgo);
    }

    private String encryptKey(String msgToEncrypt){
        try
        {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sharedKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(msgToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    private String decryptKey(String msgToDecrypt){
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sharedKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(msgToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    private void sendCurrentCommunityKey(WebSocket webSocket){
        webSocket.send("This is your Edge Server speaking");
        byte[] communityKey = keygen.getPubKey();
        byte[] sharedSecretBytes = keygen.getSharedSecret();
        sharedKey = new SecretKeySpec(sharedSecretBytes, 0, sharedSecretBytes.length, "AES");
        String messageToEncrypt = null;
        messageToEncrypt = new String(communityKey, StandardCharsets.ISO_8859_1);
        System.out.println("Original key at Edge Server: " + Arrays.toString(communityKey));
        String messagedComKey = encryptKey(messageToEncrypt);
        JSONObject communityKeyJSON = new JSONObject();
        communityKeyJSON.put("Type", "CommunityKeyMessage");
        communityKeyJSON.put("Algorithm", "AES");
        communityKeyJSON.put("Key", messagedComKey);
        webSocket.send(communityKeyJSON.toString());






    }

//    public void retrievePubKey(String[] args) {
//
//        switch (cryptoAlgo){
//            case AES:
//                System.out.println("Retrieved AES key: " + Arrays.toString(keygen.getPubKey()));
//                break;
//            case RSA:
//                System.out.println("Retrieved RSA public key: " + Arrays.toString(keygen.getPubKey()));
//                break;
//            case Blowfish:
//                System.out.println("Retrieved Blowfish key: " + Arrays.toString(keygen.getPubKey()));
//                break;
//            case DESede:
//                System.out.println("Retrieved DESede key: " + Arrays.toString(keygen.getPubKey()));
//                break;
//            default:
//                System.out.println("Error in retrieving key");
//        }
//    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        conns.add(webSocket);
        //System.out.println("New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
        sendCurrentCommunityKey(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        //System.out.println("Edge Server received message: " + s);
        try {
            JSONObject message = new JSONObject(s);
            String messageType = (String) message.get("Type");
            switch (messageType){
                case "CapacityReport":
                    System.out.println("Edge Server: Received CapacityReport");
                    currentCapacityReport = message.toString();
                    digestCapacityReport(message);
                    break;
                default:
                    System.out.println("Edge Server: Received an unknown type of message.");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            System.out.println("Edge Server: Received non-JSON message. --> " + s);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }


    private void digestCapacityReport(JSONObject message){
        String uuid = message.getString("UUID");

        ////////////////////////UUID decryption//////////////////////////
        byte[] comKey = keygen.getPubKey();
        SecretKey sharedKey = new SecretKeySpec(comKey, 0, comKey.length, "AES");
        String decryptedUUID = "";
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sharedKey);
            decryptedUUID = new String(cipher.doFinal(Base64.getDecoder().decode(uuid)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        ///////////////////////Create Edge Node Entry///////////////////////////
        EdgeNodeEntry enEntry = new EdgeNodeEntry(message, decryptedUUID);
        edgeNodes.removeIf(entry -> Objects.equals(entry.UUID, enEntry.UUID));
        edgeNodes.add(enEntry);


    }
    public static void main(String[] args){
        ESWebSocketServer server = new ESWebSocketServer();
        server.start();

    }
}
