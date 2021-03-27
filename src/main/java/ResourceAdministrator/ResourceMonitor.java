package ResourceAdministrator;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.InvocationBuilder;

import java.io.IOException;
import java.util.List;

public class ResourceMonitor {
    public static void main(String[] args) {

        DockerClient dockerClient = DockerClientBuilder.getInstance().build();


        List<Container> containers = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true)
                .exec();
        for(Container c: containers){
            InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
            //System.out.println(c.getNames()[0]);
            dockerClient.statsCmd(c.getId()).exec(callback);
            Statistics stats;
            try {
                stats = callback.awaitResult();
                callback.close();
                System.out.println(c.getNames()[0]+ ": "+ stats.getCpuStats().getSystemCpuUsage());
            } catch (RuntimeException | IOException e) {
                // you may want to throw an exception here
            }

        }

    }

}
