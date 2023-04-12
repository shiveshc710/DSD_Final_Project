package Replicas.FaraazBackup.ReplicaManager;


import Replicas.Replica3.MainServerImpl;
import config.CONFIGURATION;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.*;

public class ReplicaManager {
    private int port;
    private static com.example.client.MainServerImpl MasterServerRef;
    private DatagramSocket socket;
    private boolean running;

    public ReplicaManager(int port) {
        this.port = port;
    }

    public void start() {
        running = true;
        try {
            socket = new DatagramSocket(port, InetAddress.getByName(CONFIGURATION.RM3_IP));
            System.out.println("ReplicaManager started on port " + port);

            while (running) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);


                String request = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received request: " + request);

                boolean backupRequest = request.contains("backup");

                if (backupRequest) {
                    request = request.split(";")[1];
                    String server = request.split(",")[1];
//                    setWebServiceParams(server.substring(0, 3));
                    // call method on replica server and get response
                    String response = callReplicaServerMethod(request);

                    // send response back to frontend
                    InetAddress sequencerAddress = InetAddress.getByName(CONFIGURATION.SEQUENCER_IP);
                    int port = CONFIGURATION.SEQUENCER_BACKUP_PORT;
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, sequencerAddress, port);
                    socket.send(responsePacket);
                    System.out.println("Sent response to " + sequencerAddress.getHostAddress() + ":" + port);

                } else {
                    String server = request.split(",")[1];
//                    setWebServiceParams(server.substring(0, 3));
                    String response = null;
                    // call method on replica server and get response

                    response = callReplicaServerMethod(request);

                    // send response back to frontend
                    InetAddress frontEndAddress = InetAddress.getByName(CONFIGURATION.FE_IP);
                    int port = CONFIGURATION.FE_RECEIVE_PORT;
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, frontEndAddress, port);
                    socket.send(responsePacket);
                    System.out.println("Sent response to " + frontEndAddress.getHostAddress() + ":" + port);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        if (socket != null) {
            socket.close();
        }
    }

    private String callReplicaServerMethod(String request) {
        if (MasterServerRef == null) {
            System.out.println("Problem here");
            URL url = null;
            try {
                url = new URL("http://localhost:8084/mainserver?wsdl");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
            Service service = Service.create(url, qname);
            MasterServerRef = service.getPort(com.example.client.MainServerImpl.class);
        }
        String ans = "";
        // call method on replica server and return response
        String[] parts = request.split(",");
        if (parts[0].equals("addSlot")) {
            ans = MasterServerRef.addMovieSlots(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        } else if (parts[0].equals("remSlot")) {
            ans = MasterServerRef.removeMovieSlots(parts[1], parts[2], parts[3]);
        } else if (parts[0].equals("book")) {
            ans = MasterServerRef.bookMovieTickets(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        } else if (parts[0].equals("cancel")) {
            ans = MasterServerRef.cancelMovieTickets(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        } else if (parts[0].equals("listSlot")) {
            ans = MasterServerRef.listMovieShowsAvailability(parts[2]);
        } else if (parts[0].equals("listbook")) {
            ans = MasterServerRef.getBookingSchedule(parts[1]);
        } else if (parts[0].equals("exchangeTickets")) {
            ans = MasterServerRef.exchangeTickets(parts[1], parts[2], parts[3], parts[4], parts[5], Integer.parseInt(parts[6]));
        }

        return ans;
    }

    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:8084/mainserver?wsdl");
            QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
            Service service = Service.create(url, qname);
            MasterServerRef = service.getPort(com.example.client.MainServerImpl.class);


        } catch (Exception e) {
            System.out.println("Error in adminClient: " + e);
        }
        ReplicaManager replicaManager = new ReplicaManager(CONFIGURATION.CRASH_MAIN_RM);
        replicaManager.start();
    }
}
