package Sequencer;

import config.CONFIGURATION;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Sequencer {
    private static int sequencerId = 0;



    public Sequencer(){

    }

    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(CONFIGURATION.SEQUENCER_PORT, InetAddress.getByName(CONFIGURATION.SEQUENCER_IP));
            byte[] buffer = new byte[1000];
            System.out.println("Sequencer UDP Server Started");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer,
                        buffer.length);

                aSocket.receive(request);
                String sentence = new String(request.getData(),0, request.getLength());
                System.out.println(sentence);
                sendRequest(sentence);


//                String sentence = new String(request.getData());
//
//                String[] parts = sentence.split(";");
//
//                if(parts[1].equals("Add")){
//                    parts[1] = "1";
//                }
//
//                byte[] SeqId = (CONFIGURATION.SEQUENCER_IP).getBytes();
//                InetAddress aHost1 = request.getAddress();
//                int port1 = request.getPort();
//
//                System.out.println(aHost1 + ":" + port1);
//                DatagramPacket request1 = new DatagramPacket(SeqId,
//                        SeqId.length, aHost1, port1);
//                aSocket.send(request1);
//                sequencerId++;
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

    public static void sendMessage(String message, int sequencerId1, boolean isRequest) {
        int port = 1234;

        if (sequencerId1 == 0 && isRequest) {
            sequencerId1 = ++sequencerId;
        }
        String finalMessage = sequencerId1 + ";" + message;

        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] messages = finalMessage.getBytes();
            InetAddress aHost = InetAddress.getByName(CONFIGURATION.RM_IP);

            DatagramPacket request = new DatagramPacket(messages,
                    messages.length, aHost, port);
            aSocket.send(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void sendRequest(String requestData) throws IOException {
        // Define the multicast address and port number
        InetAddress multicastAddress = InetAddress.getByName("localhost");
        int multicastPort = 5000;

        // Create the request data
        byte[] requestBuffer = requestData.getBytes();

        // Create the UDP packet with the request data
        DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length, multicastAddress, multicastPort);

        // Use a for loop to send the request packet to each replica server
        for (int i = 1; i <= 3; i++) {
            int replicaPort = 5000 + i * 1000; // calculate the port number for the current replica server
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
    }
}
