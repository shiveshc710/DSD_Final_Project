package Sequencer;

import Replicas.FaraazBackup.ReplicaManager.ReplicaManager;
import Replicas.FaraazBackup.ATWImpl;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;
import config.CONFIGURATION;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Sequencer {
    private static int sequencerId = 0;
    private static final LinkedList<SequenceModel> requestQueue = new LinkedList<SequenceModel>();
    private static final LinkedList<SequenceModel> backupRequestQueue = new LinkedList<SequenceModel>();

    private static boolean isFirstTime = false;

    static DatagramSocket aSocket = null;
    static int[] ports = new int[]{CONFIGURATION.RM1_PORT, CONFIGURATION.RM2_PORT, CONFIGURATION.RM3_PORT};
    static String[] rmIp = new String[]{
            CONFIGURATION.RM1_IP,
            CONFIGURATION.RM2_IP,
            CONFIGURATION.RM3_IP
    };

    static boolean isRecovering = false;

    public static void main(String[] args) throws InterruptedException {

        try {
            aSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_PORT, InetAddress.getByName(CONFIGURATION.SEQUENCER_IP));
            byte[] buffer = new byte[1000];
            System.out.println("Sequencer UDP Server Started");

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer,
                        buffer.length);

                aSocket.receive(request);
                String sentence = new String(request.getData(), 0, request.getLength());
                System.out.println(sentence);

                if (sentence.startsWith("CrashReplicaTimeout")) {
                    System.out.println("POSTCRASH_REPLICA_");
                    // create a new thread for ReplicaManager
                    Thread replicaThread = new Thread(() -> {
                        ReplicaManager replicaManager = new ReplicaManager(CONFIGURATION.CRASH_MAIN_RM);
                        replicaManager.start();
                    });

                    // start the thread
                    replicaThread.start();
                    System.out.println("POSTCRASH_REPLICA_RESTARTED");
                    ATWImpl.movieInfo = new HashMap<String, HashMap<String, Integer>>();
                    ATWImpl.moviesBookedInfo = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
                    //DO FOR OUT, VER
                    sendTimeoutRequestToReplica(CONFIGURATION.RM3_PORT, CONFIGURATION.RM3_IP, backupRequestQueue);
                }
                //t
                if (sentence.startsWith("Timeout")) {
//                    TRY TO DO IT FOR NEW INSTANCE FOR FAILED SERVER
//                    Thread replicaThread = new Thread(() -> {
//                        ReplicaManager replicaManager = new ReplicaManager(CONFIGURATION.CRASH_MAIN_RM);
//                        replicaManager.start();
//                    });
                    if (sentence.endsWith("1")) {
                        handleServerRestart(0);
                        ports[0] = CONFIGURATION.CRASH_MAIN_RM;
                        rmIp[0] = CONFIGURATION.CRASH_RM_IP;
                    } else if (sentence.endsWith("2")) {
                        handleServerRestart(1);
                        ports[1] = CONFIGURATION.CRASH_MAIN_RM;
                        rmIp[1] = CONFIGURATION.CRASH_RM_IP;
                    } else if (sentence.endsWith("3")) {
                        handleServerRestart(2);
                        ports[2] = CONFIGURATION.CRASH_MAIN_RM;
                        rmIp[2] = CONFIGURATION.CRASH_RM_IP;

                    }

                    // Send UDP to Crash Server to reinitiate all requests
                } else {
                    sequencerId++;
                    SequenceModel s = new SequenceModel(sequencerId, sentence);
                    requestQueue.addLast(s);

                    if (!isFirstTime) {
                        isFirstTime = true;
                        sendRequest();
                    }
                }

            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    private static void sendTimeoutRequestToReplica(int port, String address, LinkedList<SequenceModel> backupRequestQueue) throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(CONFIGURATION.CRASH_RM_IP);
        String timeoutRequestData = "Timeout";
        LinkedList<SequenceModel> backupTemp = backupRequestQueue;
//
//        //Sending UDP request to Replica Manager to reInstantiate the servers
//        byte[] timeoutRequestBuffer = timeoutRequestData.getBytes();
//        DatagramPacket requestPacket = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, multicastAddress, port);
//        DatagramSocket socket = new DatagramSocket();
//        socket.send(requestPacket);
//
//        byte[] buffer = new byte[1000];
//        DatagramSocket receiveSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_PORT+1, InetAddress.getByName(CONFIGURATION.SEQUENCER_IP));
//        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//        receiveSocket.receive(response);
//
//        String sentence = new String(response.getData(), 0, response.getLength());
//        socket.close();
//        receiveSocket.close();
//
//        if (sentence.equals("Replica Restarted")) {

        while (backupTemp.peek() != null) {
            String requestData = backupTemp.getFirst().request;
            // Define the multicast address and port number
            requestData = "backup;" + requestData;
            byte[] timeoutRequestBuffer = requestData.getBytes();

//                DatagramPacket requestPacket = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, CONFIGURATION.CRASH_RM_IP, CONFIGURATION.CRASH_MAIN_RM);???
            DatagramPacket requestPacket = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, multicastAddress, CONFIGURATION.CRASH_MAIN_RM);
            DatagramSocket socket = new DatagramSocket();
            socket.send(requestPacket);

            byte[] crashbuffer = new byte[1000];
            DatagramSocket crashReceiveSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_BACKUP_PORT, InetAddress.getByName(CONFIGURATION.SEQUENCER_IP));
            DatagramPacket crashResponse = new DatagramPacket(crashbuffer, crashbuffer.length);
            crashReceiveSocket.receive(crashResponse);

            String backupResponse = new String(crashResponse.getData(), 0, crashResponse.getLength());
            System.out.println("Sequencer : " + backupResponse);
            crashReceiveSocket.close();
            socket.close();

            backupTemp.removeFirst();

        }
        isRecovering = false;
        sendRequest();
//
//        } else {
//            sendTimeoutRequestToReplica(port,address, backupRequestQueue);
//        }

    }

    private static void handleServerRestart(int i) throws IOException {
        isRecovering = true;
        sendRequest();
        if (i == 0) {
            sendTimeoutRequestToReplica(CONFIGURATION.RM1_PORT, CONFIGURATION.RM1_IP, backupRequestQueue);
        } else if (i == 1) {
            sendTimeoutRequestToReplica(CONFIGURATION.RM2_PORT, CONFIGURATION.RM2_IP, backupRequestQueue);
        } else if (i == 2) {
            sendTimeoutRequestToReplica(CONFIGURATION.RM3_PORT, CONFIGURATION.RM3_IP, backupRequestQueue);
        }
    }

    public static void sendRequest() throws IOException {

        while (true) {
            if (isRecovering)
                break;

            if (requestQueue.peek() != null) {
                String requestData = requestQueue.getFirst().request;
                System.out.println("Used : " + requestQueue.getFirst().sequenceID + " : " + requestData);
                // Define the multicast address and port number
                InetAddress multicastAddress = InetAddress.getByName(CONFIGURATION.HOSTNAME);
                int multicastPort = 5000;


                // Create the request data
                byte[] requestBuffer = requestData.getBytes();

                // Create the UDP packet with the request data
                DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length, multicastAddress, multicastPort);

                // Use a for loop to send the request packet to each replica server
                for (int i = 0; i < ports.length; i++) {
                    int replicaPort = ports[i]; // calculate the port number for the current replica server
                    InetAddress replicaAddress = InetAddress.getByName(rmIp[i]); // assume all replica servers are running on the same machine

                    // Set the port number of the current replica server in the request packet
                    requestPacket.setPort(replicaPort);

                    // Set the address of the current replica server in the request packet
                    requestPacket.setAddress(replicaAddress);

                    // Send the request packet to the current replica server
                    DatagramSocket socket = new DatagramSocket();
                    socket.send(requestPacket);
                    socket.close();
                    System.out.println("Sent request to replica " + ports[i]);
                }

                backupRequestQueue.add(requestQueue.getFirst());
                requestQueue.removeFirst();

            } else {
                isFirstTime = false;
                sequencerId = 0;
                break;
            }
        }
    }

    public static void killProcessOnPort(int port) throws Exception {
        // First, find the process ID (PID) of the process running on the given port
        int pid = -1;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            pid = serverSocket.getLocalPort();
        } catch (IOException e) {
            // Port is in use, so find the PID using JMX
            MBeanServerConnection connection = null;
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                connection = (MBeanServerConnection) registry.lookup("jmxrmi");
                ObjectName[] objectNames = connection.queryNames(new ObjectName("com.sun.management:type=HotSpotDiagnostic"), null).toArray(new ObjectName[0]);
                List<Integer> pids = new ArrayList<>();
                for (ObjectName objName : objectNames) {
                    HotSpotDiagnosticMXBean bean = (HotSpotDiagnosticMXBean) connection.getMBeanInfo(objName);
                    VMOption pidOption = null;
                    for (VMOption option : bean.getDiagnosticOptions()) {
                        if ("pid".equals(option.getName())) {
                            pidOption = option;
                            break;
                        }
                    }
                    if (pidOption != null) {
                        String pidStr = pidOption.getValue();
                        if (pidStr != null) {
                            try {
                                int pidValue = Integer.parseInt(pidStr);
                                pids.add(pidValue);
                            } catch (NumberFormatException e1) {
                                // Ignore invalid PID value
                            }
                        }
                    }
                }
                for (int p : pids) {
                    List<String> command = Arrays.asList("jcmd", Integer.toString(p), "JFR.stop");
                    ProcessBuilder processBuilder = new ProcessBuilder(command);
                    Process process = processBuilder.start();
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode == 0) {
                        // jcmd succeeded, so this is the correct process
                        pid = p;
                        break;
                    }
                }
            } catch (Exception e2) {
                throw new Exception("Error finding PID for port " + port, e2);
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e1) {
                        // Ignore exception
                    }
                }
            }
        }

        if (pid != -1) {
            // Second, kill the process with the given PID
            List<String> command = Arrays.asList("jcmd", Integer.toString(pid), "VM.shutdown");
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            process.waitFor();
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new Exception("Error killing process with PID " + pid);
            }
        } else {
            throw new Exception("Could not find process running on port " + port);
        }
    }

}
