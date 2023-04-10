package Replicas.CrashReplica.server;

import Replicas.CrashReplica.implementation.VERImplementation;
import config.CONFIGURATION;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.RemoteException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class VERAServer {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static FileHandler fh;
    static SimpleFormatter formatter = new SimpleFormatter();
    public static void main(String[] args) throws RemoteException {
        VERImplementation ver_implementation = new VERImplementation();
        Endpoint endPoint = Endpoint.publish("http://localhost:"+CONFIGURATION.CRASH_MAIN_PORT_VER+"/DMTBSVER", ver_implementation);
        System.out.println("Verdun server published : " + endPoint);
        System.out.println("Verdun server published : " + endPoint.isPublished());
        System.out.println("Verdun server is Up and Running...");

        Runnable task = () -> listenForUDP(ver_implementation);;
        Thread t = new Thread(task);
        t.start();
    }

    synchronized private static void listenForUDP(VERImplementation ver_implementation) {
        while (true) {
            try (DatagramSocket aSocket = new DatagramSocket(CONFIGURATION.CRASH_OUT_LISTENER)) {
                String result = "";
                byte[] buffer = new byte[1024];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String type = new String(request.getData()).trim().split(":")[0];
                String data = new String(request.getData()).trim().split(":")[1];
                System.out.println(data + "---" + type);
                writeLog("Received response");
                switch (type) {
                    case "showsList":
                        writeLog("Verdun : List availabilities | Request Parameters : movieName: " + data);
                        result = ver_implementation.listAvailableForServer(data);
                        break;
                    case "showsSchedule":
                        writeLog("Verdun : Get Bookings for Verdun | Request Parameters : customerID: " + data);
                        result = ver_implementation.getBookingScheduleForServer(data);
                        break;
                    case "bookTickets":
                        writeLog("Verdun : Book remote Tickets | Request Parameters : Data: " + data);
                        result = ver_implementation.bookMovieTickets(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], Integer.parseInt(data.split(" ")[3]));
                        break;
                    case "cancelTickets":
                        writeLog("Verdun : Cancel remote Tickets | Request Parameters : Data: " + data);
                        result = ver_implementation.cancelMovieTickets(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], Integer.parseInt(data.split(" ")[3]));
                        break;
                    case "checkMovieTicket":
                        writeLog("Verdun : Check remote Tickets | Request Parameters : Data: " + data);
                        result = ver_implementation.ServerexchangeTicketsCheck(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], data.split(" ")[3], data.split(" ")[4], Integer.parseInt(data.split(" ")[5]));
                        break;
                    case "checkNewMovieTicket":
                        System.out.println("Check New");
                        writeLog("Verdun : Check remote Tickets | Request Parameters : Data: " + data);
                        result = ver_implementation.ServerexchangeTicketsCheckNewMovie(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], data.split(" ")[3], data.split(" ")[4], Integer.parseInt(data.split(" ")[5]));
                        break;
                }

                writeLog("Sending response to the request server.");
                DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void writeLog(String message){
        try {

            fh = new FileHandler("src/main/java/Replicas/CrashReplica/logs/VERLog.log", 0,1,true);

            fh.setFormatter(formatter);

            logger.addHandler(fh);

            logger.setUseParentHandlers(false);

            logger.info("Log from  Verdun : "+ message);

            fh.close();
            LogManager.getLogManager().reset();


        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            logger.info("File Handler Exception!");
            fh.close();
        }
    }
}
