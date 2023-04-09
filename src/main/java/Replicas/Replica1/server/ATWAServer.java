package Replicas.Replica1.server;

import Replicas.Replica1.implementation.ATWImplementation;
import config.CONFIGURATION;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ATWAServer {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static FileHandler fh;
    static SimpleFormatter formatter = new SimpleFormatter();

    public static void main(String[] args) {
        ATWImplementation atw_implementation = new ATWImplementation();
        Endpoint endPoint = Endpoint.publish("http://localhost:" + CONFIGURATION.ATW_PORT + "/DMTBSATW", atw_implementation);
        System.out.println("Atwater server published : " + endPoint.isPublished());
        System.out.println("Atwater server is Up and Running...");

        Runnable task = () -> listenForUDP(atw_implementation);
        Thread t = new Thread(task);
        t.start();
    }

    public void test() {
        System.out.println("AOOOOOOOOOOOOOOOOOOO");
        System.exit(0);
    }

    synchronized private static void listenForUDP(ATWImplementation atw_implementation) {
        while (true) {
            try (DatagramSocket aSocket = new DatagramSocket(CONFIGURATION.ATW_LISTENER)) {
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
                        writeLog("Atwater : List availabilities | Request Parameters : movieName: " + data);
                        result = atw_implementation.listAvailableForServer(data);
                        break;
                    case "showsSchedule":
                        writeLog("Atwater : Get Bookings for Atwater | Request Parameters : customerID: " + data);
                        result = atw_implementation.getBookingScheduleForServer(data);
                        break;
                    case "bookTickets":
                        writeLog("Atwater : Book remote Tickets | Request Parameters : Data: " + data);
                        result = atw_implementation.bookMovieTickets(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], Integer.parseInt(data.split(" ")[3]));
                        break;
                    case "cancelTickets":
                        writeLog("Atwater : Cancel remote Tickets | Request Parameters : Data: " + data);
                        result = atw_implementation.cancelMovieTickets(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], Integer.parseInt(data.split(" ")[3]));
                        break;
                    case "checkMovieTicket":
                        writeLog("Atwater : Check remote Tickets | Request Parameters : Data: " + data);
                        result = atw_implementation.ServerexchangeTicketsCheck(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], data.split(" ")[3], data.split(" ")[4], Integer.parseInt(data.split(" ")[5]));
                        break;
                    case "checkNewMovieTicket":
                        System.out.println("Check New");
                        writeLog("Atwater : Check remote Tickets | Request Parameters : Data: " + data);
                        result = atw_implementation.ServerexchangeTicketsCheckNewMovie(data.split(" ")[0], data.split(" ")[1], data.split(" ")[2], data.split(" ")[3], data.split(" ")[4], Integer.parseInt(data.split(" ")[5]));
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

    public static void writeLog(String message) {
        try {

            fh = new FileHandler("src/logs/ATWLog.log", 0, 1, true);

            fh.setFormatter(formatter);

            logger.addHandler(fh);

            logger.setUseParentHandlers(false);

            logger.info("Log from  Atwater : " + message);

            fh.close();

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            logger.info("File Handler Exception!");
            fh.close();
        }
    }
}
