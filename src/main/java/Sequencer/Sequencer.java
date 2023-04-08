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

    public static void main(String[] args) throws InterruptedException {

        try {
            aSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_PORT, InetAddress.getByName(CONFIGURATION.HOSTNAME));
            byte[] buffer = new byte[1000];
            System.out.println("Sequencer UDP Server Started");

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer,
                        buffer.length);

                aSocket.receive(request);
                String sentence = new String(request.getData(),0, request.getLength());
                System.out.println(sentence);

                if(sentence.startsWith("Timeout")){
                    if (sentence.endsWith("1")){
                        handleServerRestart(1);
                    } else if (sentence.endsWith("2")) {
                        handleServerRestart(2);
                    } else if (sentence.endsWith("3")) {
                        handleServerRestart(3);
                    }
                    //Wait for the restart/backup to complete?
//                    Thread.sleep(1000);
                }
                else {
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
        DatagramSocket restartSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_PORT+1, InetAddress.getByName(CONFIGURATION.HOSTNAME));
        InetAddress multicastAddress = InetAddress.getByName("localhost");
        String timeoutRequestData = "Timeout";
        LinkedList<SequenceModel> backupTemp = backupRequestQueue;

        //Sending UDP request to Replica Manager to reInstantiate the servers
        byte[] timeoutRequestBuffer = timeoutRequestData.getBytes();
        DatagramPacket requestPacket = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, multicastAddress, port);
        DatagramSocket socket = new DatagramSocket();
        socket.send(requestPacket);

        byte[] buffer = new byte[1000];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        restartSocket.receive(response);


        String sentence = new String(response.getData(), 0, response.getLength());
        restartSocket.close();
        socket.close();

        if (sentence.equals("Replica Restarted")) {

            while (true) {
                if (backupTemp.peek() != null) {
                    String requestData = backupTemp.getFirst().request;
                    // Define the multicast address and port number
                    requestData = "restart;" + requestData;
                    timeoutRequestBuffer = requestData.getBytes();
                    requestPacket = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, multicastAddress, port);
                    socket = new DatagramSocket();
                    socket.send(requestPacket);

                    buffer = new byte[1000];
                    response = new DatagramPacket(buffer, buffer.length);

                    restartSocket.receive(response);
                    sentence = new String(response.getData(), 0, response.getLength());
                    if (sentence.contains("Success") || sentence.contains("Atwater") || sentence.contains("ATW") || sentence.contains("VER") || sentence.contains("OUT")) {
                        requestQueue.removeFirst();
                    }

                    restartSocket.close();
                    socket.close();

                }
            }
        }else {
            sendTimeoutRequestToReplica(port,backupRequestQueue);
        }

    }

    private static void handleServerRestart(int i) throws IOException, InterruptedException {
        if (i == 1) {
            sendTimeoutRequestToReplica(5000, backupRequestQueue);
        } else if (i == 2) {
            sendTimeoutRequestToReplica(6000, backupRequestQueue);
        } else if (i == 3) {
            sendTimeoutRequestToReplica(7000, backupRequestQueue);
        }
    }

    public static void sendRequest() throws IOException {

            while (true) {
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
                    for (int i = 1; i <= 3; i++) {
                        int replicaPort = 4000 + i * 1000; // calculate the port number for the current replica server
                        InetAddress replicaAddress = InetAddress.getByName("localhost"); // assume all replica servers are running on the same machine

                        // Set the port number of the current replica server in the request packet
                        requestPacket.setPort(replicaPort);

                        // Set the address of the current replica server in the request packet
                        requestPacket.setAddress(replicaAddress);

                        // Send the request packet to the current replica server
                        DatagramSocket socket = new DatagramSocket();
                        socket.send(requestPacket);
                        socket.close();
                        System.out.println("Sent request to replica " + i);
                    }

                    backupRequestQueue.add(requestQueue.getFirst());
                    requestQueue.removeFirst();

                }else {
                        isFirstTime = false;
                        sequencerId = 0;
                        break;
                }
            }
    }
}
