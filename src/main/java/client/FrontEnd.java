package client;

import java.io.IOException;
import java.net.*;

public class FrontEnd {
    private DatagramSocket socket;
    private InetAddress sequencerAddress;
    private int sequencerPort;
    private int replicaPort1, replicaPort2, replicaPort3;
    private ReplicaManagerHelper replicaManagerHelper;
    private int timeout = 5000; // timeout in milliseconds
    private int maxTries = 3; // maximum number of tries to get a response from a replica

    public FrontEnd(String sequencerAddress, int sequencerPort, int replicaPort1, int replicaPort2, int replicaPort3) throws UnknownHostException, SocketException {
        this.socket = new DatagramSocket();
        this.sequencerAddress = InetAddress.getByName(sequencerAddress);
        this.sequencerPort = sequencerPort;
        this.replicaPort1 = replicaPort1;
        this.replicaPort2 = replicaPort2;
        this.replicaPort3 = replicaPort3;
        this.replicaManagerHelper = new ReplicaManagerHelper();
    }

    public void sendRequest(String request) throws IOException {
        // construct the message to send to the sequencer
        String message = request + ";" + System.currentTimeMillis();
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, sequencerAddress, sequencerPort);

        // send the message to the sequencer
        socket.send(packet);

        // receive the response from the replicas
//        String response = receiveResponse();

        // return the response to the client
//        return response;
    }

    public String receiveResponse() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        // receive responses from the replicas
        String response1 = null, response2 = null, response3 = null;
        int numTries1 = 0, numTries2 = 0, numTries3 = 0;
        long maxResponseTime = 0;

        while (true) {

            // check if all responses have been received
            if (response1 != null && response2 != null && response3 != null) {
                // check if any responses are incorrect
                if (!response1.equals(response2) || !response2.equals(response3)) {
                    // inform the replica manager about the incorrect response
                    replicaManagerHelper.informReplicas(response1, response2, response3);
                }
                // return the correct response to the client
                return response1;
            }

            // set the timeout for receiving the response
            long responseTimeout;
            if (maxResponseTime == 0) responseTimeout = timeout;
            else responseTimeout = maxResponseTime * 2;
            socket.setSoTimeout((int) responseTimeout);

            try {
                // receive a response from a replica
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());

                // check which replica sent the response
                if (packet.getPort() == replicaPort1) {
                    // check if the response is correct
                    if (response1 == null || response1.equals(response)) {
                        response1 = response;
                    } else {
                        // inform the replica manager about the incorrect response
                        replicaManagerHelper.informReplicas(response1, response, null);
                    }
                    numTries1 = 0;
                } else if (packet.getPort() == replicaPort2) {
                    // check if the response is correct
                    if (response2 == null || response2.equals(response)) {
                        response2 = response;
                    } else {
                        // inform the replica manager about the incorrect response
                        replicaManagerHelper.informReplicas(response2, response, null);
                    }
                    numTries2 = 0;
                } else if (packet.getPort() == replicaPort3) {
                    // check if the response is correct
                    if (response3 == null || response3.equals(response)) {
                        response3 = response;
                    } else {
                        // inform the replica manager about the incorrect response
                        replicaManagerHelper.informReplicas(response3, response, null);
                    }
                    numTries3 = 0;
                }
            } catch (SocketTimeoutException e) {
                    // handle the case where a response is not received in time
                if (numTries1 >= maxTries && numTries2 >= maxTries && numTries3 >= maxTries) {
                    // inform the replica manager about the timeout
                    replicaManagerHelper.informReplicas(null, null, null);

                    // return an error message to the client
                    return "Error: response not received from any replica";
                } else {
                    if (response1 == null) {
                        numTries1++;
                    }
                    if (response2 == null) {
                        numTries2++;
                    }
                    if (response3 == null) {
                        numTries3++;
                    }
                }
            }
            // calculate the maximum response time among the replicas
            maxResponseTime = Math.max(replicaManagerHelper.getResponseTime1(), Math.max(replicaManagerHelper.getResponseTime2(), replicaManagerHelper.getResponseTime3()));
        }
    }
}

class ReplicaManagerHelper {
    private long responseTime1, responseTime2, responseTime3;

    public void informReplicas(String response1, String response2, String response3) {
        if (response1 != null) {
            responseTime1 = System.currentTimeMillis() - Long.parseLong(response1.split(";")[1]);
        }
        if (response2 != null) {
            responseTime2 = System.currentTimeMillis() - Long.parseLong(response2.split(";")[1]);
        }
        if (response3 != null) {
            responseTime3 = System.currentTimeMillis() - Long.parseLong(response3.split(";")[1]);
        }
    }

    public long getResponseTime1() {
        return responseTime1;
    }

    public long getResponseTime2() {
        return responseTime2;
    }

    public long getResponseTime3() {
        return responseTime3;
    }
}