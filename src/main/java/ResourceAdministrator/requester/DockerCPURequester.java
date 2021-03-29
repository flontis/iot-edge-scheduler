package ResourceAdministrator.requester;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.InvocationBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DockerCPURequester {

    DockerClient dockerClient;
    public DockerCPURequester() {
        dockerClient = DockerClientBuilder.getInstance().build();
    }

    private long getTotalCPUUsage(Statistics stats){
        return stats.getCpuStats().getCpuUsage().getTotalUsage();
    }
    private long getSystemUsage(Statistics stats){
        return stats.getCpuStats().getSystemCpuUsage();
    }
    private double getNumberOfCores(Statistics stats){
        return stats.getCpuStats().getCpuUsage().getPercpuUsage().size();
    }

    private double calculateCPUPercentage(long oldTotalUsage, long newTotalUsage, long oldSystemUsage, long newSystemUsage, double numberOfCPU){
        double cpuPercent= 0.0;
        double cpuDelta = (double) newTotalUsage - (double) oldTotalUsage;
        double systemDelta = (double) newSystemUsage - (double) oldSystemUsage;

        if (systemDelta > 0.0 && cpuDelta > 0.0){
            cpuPercent = (cpuDelta/systemDelta) * numberOfCPU * 100;
        }
        return cpuPercent;
    }

    private Statistics getStatsFromDocker(String containerID){
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        dockerClient.statsCmd(containerID).exec(callback);
        Statistics stats = null;
        try {
            stats = callback.awaitResult();
            callback.close();
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();// you may want to throw an exception here
        }
        return stats;
    }

    public String getCPUUsageofContainer(String containerID){

        Statistics stats = getStatsFromDocker(containerID);
        long oldTotalUsage = getTotalCPUUsage(stats);
        long oldSystemUsage = getTotalCPUUsage(stats);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stats = getStatsFromDocker(containerID);
        long newTotalUsage = getTotalCPUUsage(stats);
        long newSystemUsage = getSystemUsage(stats);
        double numberOfCores = getNumberOfCores(stats);

        double percentage = calculateCPUPercentage(oldTotalUsage, newTotalUsage, oldSystemUsage, newSystemUsage, numberOfCores);



        return String.format("%.02f", percentage) + "%";

    }

    public JSONObject getCPUUsageOfAllContainers(){
        JSONObject r = new JSONObject();
        String result = "";
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true)
                .withStatusFilter(Arrays.asList("running"))
                .exec();
        for (Container c : containers) {
            r.put(c.getNames()[0], getCPUUsageofContainer(c.getId()));
            result = result.concat(c.getNames()[0] + ": " + getCPUUsageofContainer(c.getId())) + "\n";

        }
        //TODO refactor to be in JSON format
        return r;
    }
}
