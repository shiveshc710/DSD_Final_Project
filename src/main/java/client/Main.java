package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) throws IOException {
// Create a socket to send the request
        DatagramSocket socket = new DatagramSocket();

        // Define the front end's IP address and port number
        InetAddress frontEndAddress = InetAddress.getByName("localhost");
        int frontEndPort = 9000;

        // Create the request data
        String requestData = "example request data";
        byte[] requestBuffer = requestData.getBytes();

        // Create the UDP packet with the request data
        DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length, frontEndAddress, frontEndPort);

        // Send the request packet to the front end
        socket.send(requestPacket);

    }
}