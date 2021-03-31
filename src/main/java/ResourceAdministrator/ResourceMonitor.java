package ResourceAdministrator;


import ResourceAdministrator.KeyStorage.KeyStorage;
import ResourceAdministrator.requester.DockerCPURequester;
import ResourceAdministrator.requester.DockerMemRequester;
import ResourceAdministrator.requester.HostResourceRequester;
import org.json.JSONObject;

public class ResourceMonitor {
    //TODO Kommentare, Kommentare, Kommentare. Hier und in den Requesters

    public static void main(String[] args) {

        // create all necessary instances
        // put requests in different threads to ensure they are made as close as possible together in time
        DockerMemRequester memReq = new DockerMemRequester();
        DockerCPURequester CPUReq = new DockerCPURequester();
        HostResourceRequester hostReq = new HostResourceRequester();
        KeyStorage keyStorage = new KeyStorage();
        // request respective resources
        JSONObject memoryUsagePerContainer = memReq.getMemoryUsageOfAllContainers();
        JSONObject cpuUsagePerContainer = CPUReq.getCPUUsageOfAllContainers();
        JSONObject hostResources = hostReq.getHostResources();

        // combine JSONs containing resources
        JSONObject combined = new JSONObject();
        combined.put("Host", hostResources);
        combined.put("CPU", cpuUsagePerContainer);
        combined.put("Memory", memoryUsagePerContainer);


        System.out.println(combined.toString(4));
        //return combined;
    }

}
