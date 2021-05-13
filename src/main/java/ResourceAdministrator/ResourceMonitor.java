package ResourceAdministrator;



import ResourceAdministrator.requester.DockerCPURequester;
import ResourceAdministrator.requester.DockerMemRequester;
import ResourceAdministrator.requester.HostResourceRequester;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static java.lang.Thread.*;

public class ResourceMonitor {
    //TODO Kommentare, Kommentare, Kommentare. Hier und in den Requesters
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        // create all necessary instances
        // put requests in different threads to ensure they are made as close as possible together in time
        DockerMemRequester memReq = new DockerMemRequester();
        DockerCPURequester CPUReq = new DockerCPURequester();
        HostResourceRequester hostReq = new HostResourceRequester();
        MessageDigest salt;
        String uuid= "";
        try {
            salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
             uuid= bytesToHex(salt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ResourceWebserverStarter communicationChannel = new ResourceWebserverStarter(new URI("ws://localhost:4444"), uuid);
        communicationChannel.connect();




        boolean development = false;
        while(!development) {
            // request respective resources
            JSONObject memoryUsagePerContainer = memReq.getMemoryUsageOfAllContainers();
            JSONObject cpuUsagePerContainer = CPUReq.getCPUUsageOfAllContainers();
            JSONObject hostResources = hostReq.getHostResources();

            // combine JSONs containing resources
            JSONObject combined = new JSONObject();
            combined.put("UUID", uuid);
            combined.put("Host", hostResources);
            combined.put("CPU", cpuUsagePerContainer);
            combined.put("Memory", memoryUsagePerContainer);
            communicationChannel.sendReport(combined);
            Thread.sleep(10000);


            //System.out.println(combined.toString(4));
        }
        boolean done= false;
        while(development){
            if(!done){
                JSONObject memoryUsagePerContainer = memReq.getMemoryUsageOfAllContainers();
                JSONObject cpuUsagePerContainer = CPUReq.getCPUUsageOfAllContainers();
                JSONObject hostResources = hostReq.getHostResources();

                // combine JSONs containing resources
                JSONObject combined = new JSONObject();
                combined.put("Host", hostResources);
                combined.put("CPU", cpuUsagePerContainer);
                combined.put("Memory", memoryUsagePerContainer);
                communicationChannel.sendReport(combined);
                done = true;
            }
            sleep(2000);
            System.out.println("Waiting...");
        }
        //return combined;
    }

}
