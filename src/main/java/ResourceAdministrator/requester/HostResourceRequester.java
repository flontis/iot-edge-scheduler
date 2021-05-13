package ResourceAdministrator.requester;


import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class HostResourceRequester {

    public JSONObject getHostResources(){
        JSONObject json = new JSONObject();
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        long availableMemory = hal.getMemory().getTotal();
        long freeMem = hal.getMemory().getAvailable();
        long cpu = hal.getProcessor().getMaxFreq();
        int cores = hal.getProcessor().getLogicalProcessors().size();
        int co = hal.getProcessor().getPhysicalProcessorCount();
        json.put("maxMem", availableMemory);
        json.put("freeMem", freeMem);
        json.put("cores", co);
        json.put("threads", cores);
        json.put("maxFrequency", cpu);

        return json;
    }
}
