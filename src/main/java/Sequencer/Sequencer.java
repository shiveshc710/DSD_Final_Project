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
    static int[] ports = new int[]{5000, 6000, 7000};;
    static String[] rmIp = new String[]{
        CONFIGURATION.RM_IP,
        CONFIGURATION.RM_IP,
        CONFIGURATION.RM_IP
    };

    static boolean isRecovering = false;

    public static void main(String[] args) throws InterruptedException {

        try {
            aSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_PORT, InetAddress.getByName(CONFIGURATION.HOSTNAME));
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
                    } else if (sentence.endsWith("2")) {
                        handleServerRestart(1);
                        ports[1] = CONFIGURATION.CRASH_MAIN_RM;
                    } else if (sentence.endsWith("3")) {
                        handleServerRestart(2);
                        ports[2] = CONFIGURATION.CRASH_MAIN_RM;
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

    private static void sendTimeoutRequestToReplica(int port, LinkedList<SequenceModel> backupRequestQueue) throws IOException {
        InetAddress multicastAddress = InetAddress.getByName("localhost");
        String timeoutRequestData = "Timeout";
        LinkedList<SequenceModel> backupTemp = backupRequestQueue;

        //Sending UDP request to Replica Manager to reInstantiate the servers
        byte[] timeoutRequestBuffer = timeoutRequestData.getBytes();
        DatagramPacket requestPacket = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, multicastAddress, port);
        DatagramSocket socket = new DatagramSocket();
        socket.send(requestPacket);

        byte[] buffer = new byte[1000];
        DatagramSocket receiveSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_PORT+1);
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        receiveSocket.receive(response);

        String sentence = new String(response.getData(), 0, response.getLength());
        socket.close();
        receiveSocket.close();

        if (sentence.equals("Replica Restarted")) {

            while (backupTemp.peek() != null) {
                    String requestData = backupTemp.getFirst().request;
                    // Define the multicast address and port number
                    requestData = "backup;" + requestData;
                    timeoutRequestBuffer = requestData.getBytes();
                    requestPacket = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, multicastAddress, CONFIGURATION.CRASH_MAIN_RM);
                    socket = new DatagramSocket();
                    socket.send(requestPacket);
                    socket.close();
                    backupTemp.removeFirst();

            }
            isRecovering = false;
            sendRequest();

        } else {
            sendTimeoutRequestToReplica(port, backupRequestQueue);
        }

    }

    private static void handleServerRestart(int i) throws IOException {
        isRecovering = true;
        sendRequest();
        if (i == 0) {
            sendTimeoutRequestToReplica(5000, backupRequestQueue);
        } else if (i == 1) {
            sendTimeoutRequestToReplica(6000, backupRequestQueue);
        } else if (i == 2) {
            sendTimeoutRequestToReplica(7000, backupRequestQueue);
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
                    InetAddress multicastAddress = InetAddress.getByName("localhost");
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
