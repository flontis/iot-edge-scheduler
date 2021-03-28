package ResourceAdministrator;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.InvocationBuilder;

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

    public String getMemoryUsageOfContainer(String containerID) {
//        List<Container> containers = dockerClient.listContainersCmd()
//                .withShowSize(true)
//                .withShowAll(true)
//                .withStatusFilter(Arrays.asList("running"))
//                .exec();
//        for (Container c : containers) {
//            InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
//            dockerClient.statsCmd(c.getId()).exec(callback);
//            Statistics stats;
//            try {
//                stats = callback.awaitResult();
//                callback.close();
//            } catch (RuntimeException | IOException e) {
//                e.printStackTrace();// you may want to throw an exception here
//            }
//        }
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
        return "memusage: " + memusage + ", percentage: " + String.format("%.02f", percentage) + "%";
    }

    public String getMemoryUsageOfAllContainers() {
        String result = "";
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true)
                .withStatusFilter(Arrays.asList("running"))
                .exec();
        for (Container c : containers) {
        result = result.concat(c.getNames()[0] + ": " + getMemoryUsageOfContainer(c.getId())) + "\n";

        }
        //System.out.println("result: " + result);
        return result;
    }
}