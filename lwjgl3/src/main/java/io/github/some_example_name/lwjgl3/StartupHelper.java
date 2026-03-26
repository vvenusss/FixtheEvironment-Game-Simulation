package io.github.some_example_name.lwjgl3;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;


public class StartupHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    public static boolean startNewJvmIfRequired() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("mac")) return false;


        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.contains("-XstartOnFirstThread")) return false;
        }


        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.equals(JVM_RESTARTED_ARG)) return false;
        }

        String separator = System.getProperty("file.separator");
        String classpath  = System.getProperty("java.class.path");


        String javaPath = System.getProperty("java.home")
                        + separator + "bin" + separator + "java";


        String mainClass = null;
        try {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            mainClass = stack[stack.length - 1].getClassName();
        } catch (Exception ignored) { }

        if (mainClass == null) return false;

        List<String> cmd = new ArrayList<>();
        cmd.add(javaPath);
        cmd.add("-XstartOnFirstThread");
        cmd.add("-D" + JVM_RESTARTED_ARG);
        cmd.add("-cp");
        cmd.add(classpath);
        cmd.add(mainClass);

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
