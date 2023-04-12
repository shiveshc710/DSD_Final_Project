package Sequencer;

import config.CONFIGURATION;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

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

                if (sentence.startsWith("Timeout")) {
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
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    private static void sendTimeoutRequestToReplica() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(CONFIGURATION.CRASH_RM_IP);
        LinkedList<SequenceModel> backupTemp = backupRequestQueue;

            while (backupTemp.peek() != null) {
                    String requestData = backupTemp.getFirst().request;
                    // Define the multicast address and port number
                    requestData = "backup;" + requestData;
                    byte[] timeoutRequestBuffer = requestData.getBytes();
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

    }

    private static void handleServerRestart(int i) throws IOException {
        isRecovering = true;
        sendRequest();
        sendTimeoutRequestToReplica();

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
}
