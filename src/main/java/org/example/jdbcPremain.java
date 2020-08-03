package org.example;

import java.lang.instrument.Instrumentation;

public class jdbcPremain {
    public static void premain(String agentArgs, Instrumentation inst){
        System.out.println("premain is loading...");
        inst.addTransformer(new Test(),true);
    }
}
