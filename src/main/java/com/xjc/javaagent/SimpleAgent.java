package com.xjc.javaagent;

import java.lang.instrument.Instrumentation;

public class SimpleAgent {

    public static void premain(String agentArgs,
                               Instrumentation instrumentation){

        System.out.println("Starting Agent");
        SimpleClassFileTransformer transformer =
                new SimpleClassFileTransformer();
        instrumentation.addTransformer(transformer);
    }
}