package Replicas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecompileAndRestart {
    private static final String JAVA_HOME = System.getProperty("java.home");
    private static final String JAVA_EXE = JAVA_HOME + "/bin/java";
    private static final String CLASSPATH = System.getProperty("java.class.path");

    public String recompileAndRestart(String filePath1, String className1) {
        String filePath = filePath1; // Replace with the absolute path to your source file
        String className = className1; // Replace with the name of your main class

        // Find the PID of the running Java program
        int pid = getRunningJavaProgramPID(className);
        if (pid == -1) {
            System.out.println("No running Java program found.");
            return "Failed";
        }
        System.out.println("Found running Java program with PID " + pid);

        // Kill the running Java program
        if (killJavaProgram(pid)) {
            System.out.println("Killed running Java program.");
        } else {
            System.out.println("Failed to kill running Java program.");
            return "Failed";
        }

        // Recompile the source code
        if (compileJavaFile(filePath)) {
            System.out.println("Compiled " + filePath + " successfully.");
        } else {
            System.out.println("Failed to compile " + filePath + ".");
            return "Failed";
        }

        // Run the new version of the program
        String[] command = new String[] {JAVA_EXE, "-cp", CLASSPATH, className};
        try {
            Process process = new ProcessBuilder(command).start();
            System.out.println("Started new version of " + className + ".");
            return "Success";
        } catch (IOException ex) {
            System.out.println("Failed to start new version of " + className + ".");
            ex.printStackTrace();
        }
        return "Failed";
    }

    private static int getRunningJavaProgramPID(String className) {
        int pid = -1;
        try {
            Process process = new ProcessBuilder("jps").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(className)) {
                    String[] parts = line.split("\\s+");
                    pid = Integer.parseInt(parts[0]);
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return pid;
    }

    private static boolean killJavaProgram(int pid) {
        try {
            Process process = new ProcessBuilder("jcmd", String.valueOf(pid), "Ctrl_C_Event").start();
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static boolean compileJavaFile(String filePath) {
        try {
            Process process = new ProcessBuilder("javac", filePath).start();
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String rerunMainClass(String className, String filePath, String[] args) {
        Path path = Paths.get(filePath);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        try {
            Class<?> clazz = classLoader.loadClass(filePath);

            Method mainMethod = clazz.getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
            return "Success";
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return "Failed";
    }
}