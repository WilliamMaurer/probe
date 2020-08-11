package org.example;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class ProcessId {
    public static String getProcessId(){
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return (runtimeMXBean.getName().split("@")[0]);
    }
}
