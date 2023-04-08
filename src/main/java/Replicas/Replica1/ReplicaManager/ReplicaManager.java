package Replicas.Replica1.ReplicaManager;


import Replicas.RecompileAndRestart;
import Replicas.Replica1.MTBInterface.MTBSInterface;
import config.CONFIGURATION;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.*;

public class ReplicaManager {
    private int port;
    private static MTBSInterface MasterServerRef;
    private DatagramSocket socket;
    private boolean running;

    URL url = null;
    QName qname = null;
    Service service = null;
    static String[] arr;

    public ReplicaManager(int port) {
        this.port = port;
    }

    public void start() throws Exception {
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
                if (request.startsWith("Timeout")) {
                    restart();
                    continue;
                }

                boolean backupRequest = request.contains("restart");
                if (backupRequest) {
                    String server = request.split(";")[1].split(",")[1];
                    setWebServiceParams(server.substring(0, 3));

                    // call method on replica server and get response
                    String response = callReplicaServerMethod(request);
                    // send response back to frontend
                    InetAddress frontEndAddress = InetAddress.getByName("localhost");
                    int port = CONFIGURATION.SEQUENCER_PORT + 1;
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, frontEndAddress, port);
                    socket.send(responsePacket);
                    System.out.println("Sent response to " + frontEndAddress.getHostAddress() + ":" + port);
                } else {
                    String server = request.split(",")[1];
                    setWebServiceParams(server.substring(0, 3));

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
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private void restart() throws Exception {
//        String fileName = "ReplicaManager.java";
//        String className = "ReplicaManager";
//        ProcessBuilder pb = new ProcessBuilder("javac", fileName);
//        pb.inheritIO();
//        Process p = pb.start();
//        p.waitFor();
//
//        ClassLoader classLoader = ReplicaManager.class.getClassLoader();
//        Class<?> cls = classLoader.loadClass(className);
//        cls.newInstance();
//
//        DatagramSocket socket = new DatagramSocket();
//        InetAddress address = InetAddress.getLocalHost();
//        int port = CONFIGURATION.SEQUENCER_PORT + 1;
//        String message = "Replica Restarted";
//        byte[] buf = message.getBytes();
//        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
//        socket.send(packet);
//        socket.close();
        System.out.println("TESTING RESTART");
        RecompileAndRestart jr = new RecompileAndRestart();
        String result = jr.rerunMainClass("ATWAServer","target/classes/Replicas/Replica1/server/ATWAServer.class", arr);
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getLocalHost();
        int port = CONFIGURATION.SEQUENCER_PORT + 1;
        byte[] buf = result.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        socket.close();
    }

    public void setWebServiceParams(String server) throws MalformedURLException {
        int port = 0;
        switch (server) {
            case "ATW":
                port = CONFIGURATION.ATW_PORT;
                break;
            case "VER":
                port = CONFIGURATION.VER_PORT;
                break;
            case "OUT":
                port = CONFIGURATION.OUT_PORT;
                break;
            default:
                return;
        }
        URL url = new URL("http://localhost:" + port + "/DMTBS" + server + "/?wsdl");
        QName qname = new QName("http://implementation.Replica1.Replicas/", server + "ImplementationService");
        Service service = Service.create(url, qname);
        MasterServerRef = service.getPort(MTBSInterface.class);
        System.out.println("Params setting done");
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
            ans = MasterServerRef.addMovieSlots(parts[2], parts[3], Integer.parseInt(parts[4]));
        } else if (parts[0].equals("remSlot")) {
            ans = MasterServerRef.removeMovieSlots(parts[2], parts[3]);
        } else if (parts[0].equals("book")) {
            ans = MasterServerRef.bookMovieTickets(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        } else if (parts[0].equals("cancel")) {
            ans = MasterServerRef.cancelMovieTickets(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        } else if (parts[0].equals("listSlot")) {
            ans = MasterServerRef.listMovieShowsAvailability(parts[1]);
        } else if (parts[0].equals("listbook")) {
            ans = MasterServerRef.getBookingSchedule(parts[1]);
        } else if (parts[0].equals("exchangeTickets")) {
            ans = MasterServerRef.exchangeTickets(parts[1], parts[3], parts[2], parts[4], parts[5], Integer.parseInt(parts[6]));
        }

        System.out.println("Answer received : " + ans);
        return ans;
    }

    public static void main(String[] args) throws Exception {
        arr = args;
        try {
            ReplicaManager replicaManager = new ReplicaManager(5000);
            replicaManager.start();


        } catch (Exception e) {
            System.out.println("Error in adminClient: " + e);
        }
        ReplicaManager replicaManager = new ReplicaManager(5000);
        replicaManager.start();
    }
}
