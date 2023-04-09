package Replicas.CrashReplica.ReplicaManager;

import Replicas.CrashReplica.MTBInterface.MTBSInterface;
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
            socket = new DatagramSocket(port,InetAddress.getByName(CONFIGURATION.CRASH_RM_IP));
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
                    setWebServiceParams(server.substring(0, 3));
                    // call method on replica server and get response
                    callReplicaServerMethod(request);

                } else {
                    String server = request.split(",")[1];
                    setWebServiceParams(server.substring(0, 3));
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


    public void setWebServiceParams(String server) throws MalformedURLException {
        int port = 0;
        switch (server) {
            case "ATW":
                port = CONFIGURATION.CRASH_MAIN_PORT_ATW;
                break;
            case "VER":
                port = CONFIGURATION.CRASH_MAIN_PORT_VER;
                break;
            case "OUT":
                port = CONFIGURATION.CRASH_MAIN_PORT_OUT;
                break;
            default:
                return;
        }
        URL url = new URL("http://localhost:" + port + "/DMTBS" + server + "/?wsdl");
        QName qname = new QName("http://implementation.CrashReplica.Replicas/", server + "ImplementationService");
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
            ReplicaManager replicaManager = new ReplicaManager(CONFIGURATION.CRASH_MAIN_RM);
            replicaManager.start();


        } catch (Exception e) {
            System.out.println("Error in Crash Replica: " + e);
        }
    }
}
