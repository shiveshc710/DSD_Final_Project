package Replicas.Replica2.com.example.ReplicaManager;

import Replicas.Replica2.com.example.client.MasterServerImpl;
import config.CONFIGURATION;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.*;
import java.io.*;

public class ReplicaManager {
    private int port;
    private static MasterServerImpl MasterServerRef;
    private DatagramSocket socket;
    private boolean running;

    public ReplicaManager(int port) {
        this.port = port;
    }

    public void start() {
        running = true;
        try {
            socket = new DatagramSocket(port);
            System.out.println("ReplicaManager started on port " + port);

            while (running) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);


                String request = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received request: " + request);

                // call method on replica server and get response
                String response = callReplicaServerMethod(request);

                // send response back to frontend
                InetAddress frontEndAddress = InetAddress.getByName("localhost");
                int port = 9001;
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, frontEndAddress, port);
                socket.send(responsePacket);
                System.out.println("Sent response to " + frontEndAddress.getHostAddress() + ":" + port);
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
            ans = MasterServerRef.listMovieShowsAvailability(parts[1]);
        } else if (parts[0].equals("listbook")) {
            ans = MasterServerRef.getBookingSchedule(parts[1]);
        } else if (parts[0].equals("exchangeTickets")) {
            ans = MasterServerRef.exchangeTickets(parts[1], parts[2], parts[3], parts[4], parts[5], Integer.parseInt(parts[6]));
        }

        return ans;
    }

    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:" + CONFIGURATION.Main_PORT_2 + "/masterservice?wsdl");
            QName qname = new QName("http://example.com/master", "MasterServerImplService");
            Service service = Service.create(url, qname);
            MasterServerRef = service.getPort(MasterServerImpl.class);

        } catch (Exception e) {
            System.out.println("Error in adminClient: " + e);
        }
        ReplicaManager replicaManager = new ReplicaManager(6000);
        replicaManager.start();
    }
}
