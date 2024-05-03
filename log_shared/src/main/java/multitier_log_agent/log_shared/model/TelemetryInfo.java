package multitier_log_agent.log_shared.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class TelemetryInfo implements Externalizable {

    private double processCpuLoad;
    private long processCpuTime;
    private double systemCpuLoad;
    private long freeMemory;
    private long maxMemory;
    private long totalMemory;
    
    public TelemetryInfo() {
        
    }
    
    @SuppressWarnings("restriction")
    public TelemetryInfo captureTelemetry() {
        OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Runtime runtime = Runtime.getRuntime();
        
        processCpuLoad = os.getProcessCpuLoad();
        processCpuTime = os.getProcessCpuTime();
        systemCpuLoad = os.getSystemCpuLoad();
        freeMemory = runtime.freeMemory();
        maxMemory = runtime.maxMemory();
        totalMemory = runtime.totalMemory();
        
        return this;
    }

    public double getProcessCpuLoad() {
        return processCpuLoad;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }


    public long getProcessCpuTime() {
        return processCpuTime;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(processCpuLoad);
        out.writeLong(processCpuTime);
        out.writeDouble(systemCpuLoad);
        out.writeLong(freeMemory);
        out.writeLong(maxMemory);
        out.writeLong(totalMemory);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        processCpuLoad = in.readDouble();
        processCpuTime = in.readLong();
        systemCpuLoad = in.readDouble();
        freeMemory = in.readLong();
        maxMemory = in.readLong();
        totalMemory = in.readLong();
    }
}
