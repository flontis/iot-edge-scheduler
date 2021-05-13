package ResourceAdministrator;

import ResourceAdministrator.KeyStorage.KeyStorage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;

import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.Base64;


public class ResourceWebserverStarter extends WebSocketClient {
    KeyStorage keyStorage;
    String uuid;


    public ResourceWebserverStarter(URI serverURI, String uuid) {
        super(serverURI);
        keyStorage = new KeyStorage();

        this.uuid = uuid;
        System.out.println("Edge Node: Unencrypted id:" + this.uuid);


    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("New connection established");
        send("This is your Resource Administrator speaking");

    }

    @Override
    public void onMessage(String s) {
        System.out.println("Edge Node received message: " + s);
        try {
            JSONObject message = new JSONObject(s);
            String messageType = (String) message.get("Type");
            switch (messageType){
                case "CommunityKeyMessage":
                    System.out.println("Edge Node: Received CommunityKeyMessage");
                    digestCommunityKeyMessage(message);
                    break;
                default:
                    System.out.println("Edge Node: Received an unknown type of message.");
            }
        }
        catch (JSONException e){
            System.out.println("Edge Node: Received non-JSON message.");
        }




    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
    e.printStackTrace();
    }

    public static void main(String[] args) throws URISyntaxException {
        ResourceWebserverStarter webserver = new ResourceWebserverStarter(new URI("ws://localhost:4444"), "1");
        webserver.connect();
    }

    public void sendReport(JSONObject capacityReport){
        String uuid = (String) capacityReport.get("UUID");

        try {
            byte[] comKeyBytes = keyStorage.getEntry("community-key").getEncoded();
            SecretKey comKey = new SecretKeySpec(comKeyBytes, 0, comKeyBytes.length, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, comKey);
            uuid = Base64.getEncoder().encodeToString(cipher.doFinal(uuid.getBytes(StandardCharsets.UTF_8)));
            capacityReport.put("UUID", uuid);

        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        capacityReport.put("Type", "CapacityReport");
        send(capacityReport.toString());
    }

    private void digestCommunityKeyMessage(JSONObject message){
        try {
            String communityKeyBytes = decryptKey((String) message.get("Key"));
            assert communityKeyBytes != null;
            byte[] comBytes = communityKeyBytes.getBytes(StandardCharsets.ISO_8859_1);
            keyStorage.storeKey(comBytes, "community-key");
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

    }

    private String decryptKey(String msgToDecrypt) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        byte[] sharedSecretBytes = keyStorage.getEntry("shared-key").getEncoded();
        SecretKey sharedKey = new SecretKeySpec(sharedSecretBytes, 0, sharedSecretBytes.length, "AES");
        //System.out.println("Edge Node: shared key bytes: " + Arrays.toString(sharedSecretBytes));
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
}
