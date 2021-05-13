package EdgeServer;

import org.jetbrains.annotations.NotNull;

public class ADUContainer implements Comparable{

    String ENuuid;
    String name;
    long memUsage;
    double CPUUsage;

    public ADUContainer(String uuid, String name, long memUsage, double CPUUsage){
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
