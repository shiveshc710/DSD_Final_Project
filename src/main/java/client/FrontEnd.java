package client;

import config.CONFIGURATION;

import java.io.IOException;
import java.net.*;

public class FrontEnd {
    private DatagramSocket clientSocket;
    private DatagramSocket replicaSocket;
    private InetAddress sequencerAddress;
    private int sequencerPort;
    private InetAddress[] replicaAddresses;
    private int[] replicaPorts;
    private int[] replicaFailures;
    private int numReplicas;
    private int maxFailures;
    private int timeout;
    private int frontEndPort;
    private int clientPort;

    private boolean isCrashReplica = false;

    public FrontEnd(String sequencerAddr, int sequencerPrt, String[] replicaAddrs, int[] replicaPrt, int timeoutMs, int frontEndPrt) throws UnknownHostException, SocketException {
        //Request from Client
        this.clientSocket = new DatagramSocket(frontEndPrt, InetAddress.getByName(CONFIGURATION.FE_IP));

        //Response from Replica
        this.replicaSocket = new DatagramSocket(CONFIGURATION.FE_RECEIVE_PORT, InetAddress.getByName(CONFIGURATION.FE_IP));
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

    public void start() throws UnknownHostException, InterruptedException {
        InetAddress SERVER_ADDRESS = InetAddress.getLocalHost();
        System.out.println(SERVER_ADDRESS.getHostAddress());
        System.out.println("client.FrontEnd listening on port " + frontEndPort);
//        System.out.println(clientSocket.getInetAddress().getHostAddress());
        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket request = new DatagramPacket(buf, buf.length);
            try {
                clientSocket.receive(request);
                System.out.println("----------------------------------------------------------------");
                byte[] data = request.getData();
                String requestString = new String(data, 0, request.getLength());
                System.out.println("Received request: " + requestString);

                String userType = requestString.split(":")[0];
                if (userType.equals("Admin"))
                    clientPort = CONFIGURATION.CLIENT_PORT_ADMIN;
                else
                    clientPort = CONFIGURATION.CLIENT_PORT_CUSTOMER;

                requestString = requestString.split(":")[1];

                byte[] requestBuffer = requestString.getBytes();
                // Send request to sequencer
                DatagramPacket sequencerRequest = new DatagramPacket(requestBuffer, requestBuffer.length, sequencerAddress, sequencerPort);
                clientSocket.send(sequencerRequest);

                // Receive responses from replicas
                String[] responses = new String[numReplicas];
                int numResponses = 0;
                String response = null;
                for (int i = 0; i < numReplicas; i++) {
                    try {
                        replicaSocket.setSoTimeout(timeout);
                        DatagramPacket replicaResponse = new DatagramPacket(new byte[1024], 1024);
                        replicaSocket.receive(replicaResponse);
                        int port = replicaResponse.getPort();
                        response = new String(replicaResponse.getData(), 0, replicaResponse.getLength());
                        System.out.println("Received response from replica on port" + port + ": " + response);
                        // Identify which replica the response belongs to based on the port number
                        int replicaId;
                        if (port == CONFIGURATION.RM1_PORT) {
                            replicaId = 1;
                            responses[replicaId - 1] = response;
                        } else if (port == CONFIGURATION.RM2_PORT) {
                            replicaId = 2;
                            responses[replicaId - 1] = response;

                        } else if (port == CONFIGURATION.RM3_PORT) {
                            replicaId = 3;
                            responses[replicaId - 1] = response;

                        }else if (port == CONFIGURATION.CRASH_MAIN_RM) {
                            isCrashReplica = true;
                        } else {
                            System.err.println("Received response from unknown port: " + port);
                            continue;
                        }
                         // Store the response in the correct position of the responses array
                        numResponses++;
                    } catch (SocketTimeoutException e) {
                        int replicaId = i + 1;
                        System.err.println("Timeout occurred.");
                        replicaFailures[replicaId - 1]++;
                    }
                }

                if (isCrashReplica) {
                    for (int i = 0; i < numReplicas; i++) {
                        if (responses[i] == null) {
                            responses[i] = response;
                            response = null;
                        }
                    }
                }

                // Identify correct response
                String correctResponse = null;
                for (int i = 0; i < numReplicas; i++) {
                    if (responses[i] != null) {
                        int count = 0;
                        for (int j = 0; j < numReplicas; j++) {
                            if (responses[j] != null && responses[i].trim().equals(responses[j].trim())) {
                                count++;
                            }
                        }
                        if (count >= 2) {
                            correctResponse = responses[i].trim();
                            break;
                        }

                    }
                }

                //force timeout
//                responses[2] = null;
                int noResponseServer = -1;
                //identify which server did not send the response
                for (int i = 0; i < numReplicas; i++) {
                    if (responses[i] == null) {
                        noResponseServer = (i + 1);
                    }
                }



                if (noResponseServer != -1) {
                    System.out.println("Replica " + (noResponseServer) + " timed out.");
                    System.out.println("Trying to restart Replica " + noResponseServer);
                    String timeoutRequestString = "Timeout," + noResponseServer;

                    byte[] timeoutRequestBuffer = timeoutRequestString.getBytes();
                    // Send request to sequencer
                    DatagramPacket timeoutSequencerRequest = new DatagramPacket(timeoutRequestBuffer, timeoutRequestBuffer.length, sequencerAddress, sequencerPort);
                    clientSocket.send(timeoutSequencerRequest);

                }

                if (correctResponse == null) {
                    System.err.println("All replicas timed out.");
                } else {
                    // Send correct response back to client

                    System.out.println(correctResponse);
                    DatagramSocket socket = new DatagramSocket();

                    // Define the client's IP address and port number
                    InetAddress clientAddress = InetAddress.getByName(CONFIGURATION.HOSTNAME);

                    // Create the response data
                    String responseData = correctResponse;
                    byte[] responseBuffer = responseData.getBytes();

                    // Create the UDP packet with the response data
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);

                    // Send the response packet to the client end
                    socket.send(responsePacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SocketException, InterruptedException {
        try {
            String sequencerAddr = CONFIGURATION.SEQUENCER_IP;
            int sequencerPort = CONFIGURATION.SEQUENCER_PORT;
            String[] replicaAddrs = {CONFIGURATION.RM1_IP, CONFIGURATION.RM2_IP, CONFIGURATION.RM3_IP};
            int[] replicaPorts = {CONFIGURATION.RM1_PORT, CONFIGURATION.RM2_PORT, CONFIGURATION.RM3_PORT};
            int timeoutMs = CONFIGURATION.TIMEOUT;
            int frontEndPort = CONFIGURATION.FE_PORT;
            FrontEnd frontEnd = new FrontEnd(sequencerAddr, sequencerPort, replicaAddrs, replicaPorts, timeoutMs, frontEndPort);
            frontEnd.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
