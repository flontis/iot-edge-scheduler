package EdgeServer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.PriorityQueue;

public class EdgeNodeEntry implements Comparable<EdgeNodeEntry>{

    String UUID;
    long maxMem;
    long freeMem;
    long maxFreq;
    PriorityQueue<ADUContainer> containers = new PriorityQueue<>();

    public EdgeNodeEntry(JSONObject dataFromCapRep, String decryptedUUID) {
        this.UUID = decryptedUUID;
        JSONObject host = new JSONObject(dataFromCapRep.get("Host").toString());
        System.out.println(dataFromCapRep.toString(4));
        this.freeMem = (long) host.get("freeMem");
        this.maxMem = (long) host.get("maxMem");
        this.maxFreq = (long) host.get("maxFrequency");

        JSONObject cpu = new JSONObject(dataFromCapRep.get("CPU").toString());
        System.out.println("Containers: " + cpu.keySet());
        JSONObject mem = new JSONObject(dataFromCapRep.get("Memory").toString());

        for (String container: cpu.keySet()){
            if( true /*container.contains("adu")*/) {
                String containerName = container.replace("/", "");
                JSONObject containerjson = new JSONObject(mem.get(container).toString());
                long memUsage = (long) (int) containerjson.get("memUsage");

                double cpuUsage = Double.parseDouble(cpu.get(container).toString().replace("%", ""));
                System.out.println("Add new container " + container + " with of ID " + decryptedUUID + " with memUsage of " + memUsage + " and cpuUsage of " + cpuUsage);
                ADUContainer aduContainer = new ADUContainer(decryptedUUID, containerName, memUsage, cpuUsage);
                containers.add(aduContainer);
            }
        }

    }

    @Override
    public int compareTo(@NotNull EdgeNodeEntry edgeNodeEntry) {
        return 0;
    }
}
