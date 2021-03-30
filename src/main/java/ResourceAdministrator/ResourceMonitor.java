package ResourceAdministrator;


import ResourceAdministrator.requester.DockerCPURequester;
import ResourceAdministrator.requester.DockerMemRequester;
import ResourceAdministrator.requester.HostResourceRequester;
import org.json.JSONObject;

public class ResourceMonitor {
    //TODO Kommentare, Kommentare, Kommentare. Hier und in den Requesters

    public static void main(String[] args) {
        DockerMemRequester memReq = new DockerMemRequester();
        DockerCPURequester CPUReq = new DockerCPURequester();
        HostResourceRequester hostReq = new HostResourceRequester();
        JSONObject memoryUsagePerContainer = memReq.getMemoryUsageOfAllContainers();
        JSONObject cpuUsagePerContainer = CPUReq.getCPUUsageOfAllContainers();
        JSONObject hostResources = hostReq.getHostResources();
        JSONObject combined = new JSONObject();
        combined.put("Host", hostResources);
        combined.put("CPU", cpuUsagePerContainer);
        combined.put("Memory", memoryUsagePerContainer);

        // put requests in different threads to ensure they are made as close as possible together in time
        System.out.println(combined.toString(4));
        //return combined;
    }

}
