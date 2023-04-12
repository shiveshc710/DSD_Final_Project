package client;

import Replicas.Replica1.MTBInterface.MTBSInterface;
import config.CONFIGURATION;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Customer {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    FileHandler fh;
    SimpleFormatter formatter = new SimpleFormatter();
    static String URL = null;
    static String userID = null;
    static DatagramSocket aSocket = null;
    public static void main(String[] args) throws UnknownHostException, SocketException {

        aSocket = new DatagramSocket(CONFIGURATION.CLIENT_PORT_CUSTOMER, InetAddress.getByName(CONFIGURATION.HOSTNAME));
        Scanner sc = new Scanner(System.in);
        Customer c = new Customer();
        try {
             while (true) {
                if (userID == null) {
                    System.out.print("Please enter Customer ID {ATWC / VERC / OUTC}{XXXX}: ");
                    userID = sc.next();
                }
                String serverName = userID.substring(0, 3);
                if (userID.charAt(3) != 'C') {
                    System.out.println("You are not valid user, please try again!!!");
                    userID = null;
                    c.writeLog("Invalid Customer ID");
                } else if ((userID.substring(4).length()) != 4) {
                    System.out.println("You have entered the wrong ID, please enter the 4 digit unique ID!!!");
                    userID = null;
                    c.writeLog("Invalid Customer ID");
                } else if (!(serverName.equals("ATW")) && !(serverName.equals("OUT"))
                        && !(serverName.equals("VER"))) {
                    System.out.println("Please enter the proper serverName!");
                    userID = null;
                    c.writeLog("Invalid Customer ID: Invalid Server Name");
                }else {
                    System.out.println("=========================================");
                    System.out.println("|               Actions                 |");
                    System.out.println("=========================================");
                    System.out.println("|  1 : Book movie tickets               |");
                    System.out.println("|  2 : Get booking schedule             |");
                    System.out.println("|  3 : Cancel Bookings                  |");
                    System.out.println("|  4 : Exchange Tickets                 |");
                    System.out.println("|  5 : Exit                             |");
                    System.out.println("=========================================");
                    System.out.print("\nPlease enter your choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    HashMap<Integer, String> movies = new HashMap<>();
                    movies.put(1, CONFIGURATION.AVATAR);
                    movies.put(2, CONFIGURATION.AVENGERS);
                    movies.put(3, CONFIGURATION.TITANIC);
                    int movie = 0;
                    String movieName="";
                    int numberOfSlots = 0;
                    String movieId = "";
                    boolean exited = false;
                    switch (choice) {
                        case 1:
                            do {
                                System.out.println("=========================================");
                                System.out.println("|        Please select the movie        |");
                                System.out.println("=========================================");
                                System.out.println("|  1 : Avatar                           |");
                                System.out.println("|  2 : Avengers                         |");
                                System.out.println("|  3 : Titanic                          |");
                                System.out.println("|  4 : Exit                             |");
                                System.out.println("=========================================");

                                System.out.print("\nPlease enter your choice: ");

                                movie = sc.nextInt();
                            } while (movie <= 0 || movie >= 5);

                            if (movie == 4)
                                break;
                            movieName = movies.get(movie);
                            System.out.print("\nPlease enter number of ticket you want to book: ");
                            int numberOfTicket = sc.nextInt();
                            sc.nextLine();
                            System.out.print("\nPlease Enter the Movie ID {ATW/VER/OUT}{M/A/E}{DDMMYY}: ");
                            movieId = sc.next();
                            c.writeLog(userID + " : Book Tickets | Request Parameters : Movie Id: " + movieId + " Movie Name: " + movieName+ " Number of Tickets: " + numberOfTicket);
                            String requestParameters = userID + "," + movieId + "," + movieName + "," + numberOfTicket;
                            String req2 = "book," + requestParameters;
                            sendRequest(req2);
                            break;
                        case 2:

                            System.out.println("Fetching all the bookings...");
                            c.writeLog(userID + " : Get Bookings | Request Parameters : Customer Id: " + userID);
                            System.out.println("================================\nBookings of customer " + userID
                                    + "\n================================\n");
                            String requestParameterslB = userID;
                            String req200 = "listbook," + requestParameterslB;
                            sendRequest(req200);
                            break;
                        case 3:
                            do {
                                System.out.println("=========================================");
                                System.out.println("|        Please select the movie        |");
                                System.out.println("=========================================");
                                System.out.println("|  1 : Avatar                           |");
                                System.out.println("|  2 : Avengers                         |");
                                System.out.println("|  3 : Titanic                          |");
                                System.out.println("|  4 : Exit                             |");
                                System.out.println("=========================================");

                                System.out.print("\nPlease enter your choice: ");

                                movie = sc.nextInt();
                            } while (movie <= 0 || movie >= 5);

                            if (movie == 4)
                                break;
                            movieName = movies.get(movie);
                            System.out.print("\nPlease enter number of ticket you want to delete: ");
                            numberOfTicket = sc.nextInt();
                            sc.nextLine();
                            System.out.print("\nPlease Enter the Movie ID {ATW/VER/OUT}{M/A/E}{DDMMYY}: ");
                            movieId = sc.next();
                            c.writeLog(userID + " : Cancel Booking | Request Parameters : Customer Id: "+userID+" Movie Id: " + movieId + " Movie Name: " + movieName+ " Number of Slots: " + numberOfTicket);
                            String requestParameters3 = userID + "," + movieId + "," + movieName + "," + numberOfTicket;
                            String req3 = "cancel," + requestParameters3;
                            sendRequest(req3);
                            break;
                        case 4:
                            System.out.print("\nPlease enter number of ticket you want to exchange: ");
                            numberOfTicket = sc.nextInt();
                            String old_movie = "";
                            String new_movie = "";
                            String movieId_old = "";
                            String movieId_new = "";
                            do {
                                System.out.println("=========================================");
                                System.out.println("|        Please select the movie        |");
                                System.out.println("=========================================");
                                System.out.println("|  1 : Avatar                           |");
                                System.out.println("|  2 : Avengers                         |");
                                System.out.println("|  3 : Titanic                          |");
                                System.out.println("|  4 : Exit                             |");
                                System.out.println("=========================================");

                                System.out.print("\nPlease enter your choice: ");

                                movie = sc.nextInt();
                            } while (movie <= 0 || movie >= 5);

                            if (movie == 4)
                                break;
                            old_movie = movies.get(movie);
                            System.out.print("\nPlease Enter the old Movie ID {ATW/VER/OUT}{M/A/E}{DDMMYY}: ");
                            movieId_old = sc.next();
                            sc.nextLine();
                            do {
                                System.out.println("=========================================");
                                System.out.println("|        Please select the movie        |");
                                System.out.println("=========================================");
                                System.out.println("|  1 : Avatar                           |");
                                System.out.println("|  2 : Avengers                         |");
                                System.out.println("|  3 : Titanic                          |");
                                System.out.println("|  4 : Exit                             |");
                                System.out.println("=========================================");

                                System.out.print("\nPlease enter your choice: ");

                                movie = sc.nextInt();
                            } while (movie <= 0 || movie >= 5);

                            if (movie == 4)
                                break;
                            new_movie = movies.get(movie);

                            sc.nextLine();
                            System.out.print("\nPlease Enter the new Movie ID {ATW/VER/OUT}{M/A/E}{DDMMYY}: ");
                            movieId_new = sc.next();

                            c.writeLog(userID + " : Exchange Tickets | Request Parameters : Movie Id: " + movieId + " Movie Name: " + movieName+ " Number of Tickets: " + numberOfTicket);

                            String requestParametersET = "exchangeTickets," + userID + "," + movieId_old + "," + old_movie + "," + movieId_new + "," + new_movie + "," + numberOfTicket;

                            sendRequest(requestParametersET);

                           break;

                        case 5:
                            exited = true;
                            break;
                    }

                    if (exited){
                        c.writeLog("Customer Logging out.");
                        System.out.println("Customer Logged out....");
                        System.out.println("\nThank you for using our system!!!");
                        break;
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }

    public static void sendRequest(String requestData1) throws IOException {
// Create a socket to send the request
        DatagramSocket socket = new DatagramSocket();

        // Define the front end's IP address and port number
        InetAddress frontEndAddress = InetAddress.getByName(CONFIGURATION.FE_IP);
        requestData1 = "Customer:"+requestData1;
        // Create the request data
        String requestData = requestData1;
        byte[] requestBuffer = requestData.getBytes();

        // Create the UDP packet with the request data
        DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length, frontEndAddress, CONFIGURATION.FE_PORT);

        // Send the request packet to the front end
        socket.send(requestPacket);

        // Receive the response
        byte[] buffer = new byte[1000];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);

        aSocket.receive(response);
        String sentence = new String(response.getData(), 0, response.getLength());
        System.out.println(sentence);
    }

    public void writeLog(String message){
        try {

            fh = new FileHandler("src/main/java/Replicas/Replica1/logs/"+userID+"Log.log", 0,1,true);

            fh.setFormatter(formatter);

            logger.addHandler(fh);

            logger.setUseParentHandlers(false);

            logger.info("Log from "+ userID +"(Customer) : "+ message);

            fh.close();
            LogManager.getLogManager().reset();


        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            logger.info("File Handler Exception!");
            fh.close();
        }
    }
}
