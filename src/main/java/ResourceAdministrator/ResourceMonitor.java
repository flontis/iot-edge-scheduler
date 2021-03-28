package ResourceAdministrator;



public class ResourceMonitor {
    //TODO Kommentare, Kommentare, Kommentare. Hier und in den Requesters

    public static void main(String[] args) {
        DockerMemRequester memReq = new DockerMemRequester();
        DockerCPURequester CPUReq = new DockerCPURequester();
        System.out.println(memReq.getMemoryUsageOfAllContainers());

    }

}
