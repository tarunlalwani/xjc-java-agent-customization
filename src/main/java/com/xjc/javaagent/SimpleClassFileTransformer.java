package com.xjc.javaagent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SimpleClassFileTransformer implements
        ClassFileTransformer {

    public byte[] transform(ClassLoader loader,
                            String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;
        if (className.endsWith("/CPropertyInfo")) {
            System.out.println("Loading class - " + className);
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass ctClass = classPool.makeClass(
                        new ByteArrayInputStream(classfileBuffer));
                CtConstructor[] constructors = ctClass.getConstructors();
                for (CtConstructor c : constructors) {
                    c.insertBefore("{        String propFile = System.getProperty(\"XJC_REMAP\");\n" +
                            "        if (propFile != null)\n" +
                            "        {\n" +
                            "            System.out.println(\"External remap file provided\");\n" +
                            "            java.util.Properties props = new java.util.Properties();\n" +
                            "            try {\n" +
                            "                props.load(new java.io.FileInputStream(propFile));\n" +
                            "                java.util.Enumeration enums = props.propertyNames();\n" +
                            "                while (enums.hasMoreElements()) {\n" +
                            "                    String key = (String)enums.nextElement();\n" +
                            "                    String value = props.getProperty(key);\n" +
                            "                    try {\n" +
                            "                        System.out.println(\"Checking if \" + name + \" matches \" + key);\n" +
                            "                        java.util.regex.Pattern pat = java.util.regex.Pattern.compile(\"^\" + key +\"$\", java.util.regex.Pattern.CASE_INSENSITIVE);\n" +
                            "                        if (pat.matcher(name).find())\n" +
                            "                        {\n" +
                            "                            System.out.println(\"Replacing \" + name + \" with \" + value);\n" +
                            "                            name = value;\n" +
                            "                            break;\n" +
                            "                        }\n" +
                            "                    } finally {\n" +
                            "                        if (name == key) {\n" +
                            "                            System.out.println(\"Replacing \" + name + \" with \" + value);\n" +
                            "                            name = value;\n" +
                            "                        }\n" +
                            "                        break;\n" +
                            "                    }\n" +
                            "                }\n" +
                            "            } catch (java.io.IOException e) {\n" +
                            "                e.printStackTrace();\n" +
                            "            }\n" +
                            "        }}");
                }
                byteCode = ctClass.toBytecode();
                ctClass.detach();
                System.out.println("NO Exception occured");
            } catch (Throwable e) {
                System.out.println("Exception occurred");
                e.printStackTrace();
            }
        }

        return byteCode;
    }
}