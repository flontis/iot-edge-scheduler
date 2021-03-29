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

public class DockerMemRequester {
    DockerClient dockerClient;

    public DockerMemRequester() {
        dockerClient = DockerClientBuilder.getInstance().build();


    }

    public double memUsagePercentage(Statistics stats) {
        long memsum = memorySum(stats);
        long limit = stats.getMemoryStats().getLimit() == null ? 0 : stats.getMemoryStats().getLimit();

        return (float) memsum / (float) limit * 100;
    }

    public long memorySum(Statistics stat) {
        long sum = 0;
        if (stat.getMemoryStats().getStats() != null) {
            long rss = stat.getMemoryStats().getStats().getRss() == null ? 0 : stat.getMemoryStats().getStats().getRss();
            long cache = stat.getMemoryStats().getStats().getCache() == null ? 0 : stat.getMemoryStats().getStats().getCache();
            long swap = stat.getMemoryStats().getStats().getSwap() == null ? 0 : stat.getMemoryStats().getStats().getSwap();
            sum = rss + swap + cache;
        }
        return sum;
    }

    public JSONObject getMemoryUsageOfContainer(String containerID) {

        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        dockerClient.statsCmd(containerID).exec(callback);
        Statistics stats = null;
        try {
            stats = callback.awaitResult();
            callback.close();
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();// you may want to throw an exception here
        }
        long memusage = memorySum(stats);
        double percentage = memUsagePercentage(stats);
        //System.out.println("memusage: " + memusage + ", percentage: " + String.format("%.02f", percentage) + "%");
        JSONObject result = new JSONObject();
        result.put("memUsage", memusage);
        result.put("percentage", String.format("%.02f", percentage) + "%");
        return result;
    }

    public JSONObject getMemoryUsageOfAllContainers() {
        JSONObject result = new JSONObject();
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true)
                .withStatusFilter(Arrays.asList("running"))
                .exec();
        for (Container c : containers) {
            result.put(c.getNames()[0], getMemoryUsageOfContainer(c.getId()));
//        result = result.concat(c.getNames()[0] + ": " + getMemoryUsageOfContainer(c.getId())) + "\n";

        }
        //TODO refactor to be in JSON format
        return result;
    }
}