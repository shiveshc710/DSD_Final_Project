package client;

import config.CONFIGURATION;

import java.io.IOException;
import java.net.*;

public class FrontEnd {
    private DatagramSocket socket;
    private InetAddress sequencerAddress;
    private int sequencerPort;
    private InetAddress[] replicaAddresses;
    private int[] replicaPorts;
    private int[] replicaFailures;
    private int numReplicas;
    private int maxFailures;
    private int timeout;
    private int frontEndPort;

    public FrontEnd(String sequencerAddr, int sequencerPrt, String[] replicaAddrs, int[] replicaPrt, int timeoutMs, int frontEndPrt) throws UnknownHostException, SocketException {
        this.socket = new DatagramSocket(frontEndPrt);
        this.sequencerAddress = InetAddress.getByName(sequencerAddr);
        this.sequencerPort = sequencerPrt;
        this.replicaAddresses = new InetAddress[replicaAddrs.length];
        this.replicaPorts = replicaPrt;
        this.numReplicas = replicaAddrs.length;
        this.replicaFailures = new int[numReplicas];
        for (int i = 0; i < numReplicas; i++) {
            this.replicaAddresses[i] = InetAddress.getByName(replicaAddrs[i]);
            this.replicaFailures[i] = 0;
        }
        this.maxFailures = (numReplicas - 1) / 2;
        this.timeout = timeoutMs;
        this.frontEndPort = frontEndPrt;
    }

    public void start() {
        System.out.println("client.FrontEnd listening on port " + frontEndPort);
        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket request = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(request);
                byte[] data = request.getData();
                String requestString = new String(data, 0, request.getLength());
                System.out.println("Received request: " + requestString);

                byte[] requestBuffer = requestString.getBytes();
                // Send request to sequencer
                DatagramPacket sequencerRequest = new DatagramPacket(requestBuffer, requestBuffer.length, sequencerAddress, sequencerPort);
                socket.send(sequencerRequest);

                // Receive responses from replicas
                String[] responses = new String[numReplicas];
                int numResponses = 0;
                for (int i = 0; i < numReplicas; i++) {
                    try {
                        socket.setSoTimeout(timeout);
                        DatagramPacket replicaResponse = new DatagramPacket(new byte[1024], 1024);
                        socket.receive(replicaResponse);
                        String response = new String(replicaResponse.getData(), 0, replicaResponse.getLength());
                        System.out.println("Received response from replica " + i + ": " + response);
                        responses[i] = response;
                        numResponses++;
                    } catch (SocketTimeoutException e) {
                        System.err.println("Replica " + i + " timed out.");
                        replicaFailures[i]++;
                        if (replicaFailures[i] > maxFailures) {
                            System.err.println("Replica " + i + " has failed.");
                            replicaFailures[i] = 0;
                            for (int j = 0; j < numReplicas; j++) {
                                if (i != j) {
                                    DatagramPacket replicaFailure = new DatagramPacket(new byte[1024], 1024, replicaAddresses[j], replicaPorts[j]);
                                    socket.send(replicaFailure);
                                }
                            }
                        }
                    }
                }

                // Identify correct response
                String correctResponse = null;
                for (int i = 0; i < numReplicas; i++) {
                    if (responses[i] != null) {
                        if (correctResponse == null) {
                            correctResponse = responses[i];
                        } else if (!correctResponse.equals(responses[i])) {
                            System.err.println("Response from replica " + i + " is incorrect.");
                            replicaFailures[i]++;
                            if (replicaFailures[i] > maxFailures) {
                                System.err.println("Replica " + i + " has failed.");
                                replicaFailures[i] = 0;
                                for (int j = 0; j < numReplicas; j++) {
                                    if (i != j) {
                                        DatagramPacket replicaFailure = new DatagramPacket(new byte[1024], 1024, replicaAddresses[j], replicaPorts[j]);
                                        socket.send(replicaFailure);
                                    }
                                }
                            }
                        }
                    }
                }
                if (correctResponse == null) {
                    System.err.println("All replicas timed out.");
                    continue;
                }

                // Send correct response back to client
                byte[] responseBuf = correctResponse.getBytes();
                DatagramPacket response = new DatagramPacket(responseBuf, responseBuf.length, request.getAddress(), request.getPort());
                socket.send(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SocketException {
        try {
            String sequencerAddr = CONFIGURATION.SEQUENCER_IP;
            int sequencerPort = CONFIGURATION.SEQUENCER_PORT;
            String[] replicaAddrs = {"localhost", "localhost", "localhost"};
            int[] replicaPorts = {5000, 6000, 7000};
            int timeoutMs = 50000;
            int frontEndPort = 9000;
            FrontEnd frontEnd = new FrontEnd(sequencerAddr, sequencerPort, replicaAddrs, replicaPorts, timeoutMs, frontEndPort);
            frontEnd.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
