package Replicas.Replica2.com.example.client;


import Replicas.Replica2.com.example.logging.LoggingHelper;
import config.CONFIGURATION;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class AdminClient {

    private static MasterServerImpl MasterServerRef;

    static DatagramSocket aSocket = null;

    public static void main(String[] args) throws UnknownHostException, SocketException {
        aSocket = new DatagramSocket(CONFIGURATION.CLIENT_PORT_ADMIN, InetAddress.getByName(CONFIGURATION.HOSTNAME));
        try {
            URL url = new URL("http://localhost:" + CONFIGURATION.Main_PORT_2 + "/masterservice?wsdl");
            QName qname = new QName("http://example.com/master", "MasterServerImplService");
            Service service = Service.create(url, qname);
            MasterServerRef = service.getPort(MasterServerImpl.class);

        } catch (Exception e) {
            System.out.println("Error in adminClient: " + e);
        }

        displayMenu(MasterServerRef);
    }


    /**
     * client.Main menu for ADMIN Client.
     *
     * @param obj The object of interface.
     */
    public static void displayMenu(MasterServerImpl obj) {
        try {

            System.out.println();
            System.out.println("Welcome ADMIN!");
            System.out.println("Please enter your ID: ");
            Scanner sc = new Scanner(System.in);

            String id = sc.nextLine();

            System.out.println("1. Add movie slots");
            System.out.println("2. Remove movie slots");
            System.out.println("3. Shows available in all regions");
            System.out.println("4. Display customer menu");
            System.out.println("0. Exit");

            System.out.println("Please enter your option: ");
            int option = sc.nextInt();
            sc.nextLine();

            if (option == 1) {
                System.out.println("Please enter the date in DD-MM-YY format for adding a booking:");

                String[] date = sc.nextLine().split("-");

                System.out.println("Please enter the time of the show you want to add (M, A, E):");
                String time = sc.nextLine();


                String movieID;
                movieID = id.substring(0, 3) + time + date[0] + date[1] + date[2];

                System.out.println("Enter movie name:");
                String movieName = sc.nextLine();
                System.out.println("Enter Capacity:");
                int bookingCapacity = sc.nextInt();
                sc.nextLine();
                String requestParameters = id + "," + movieID + "," + movieName + "," + bookingCapacity;

                try {
//                    String ans = obj.addMovieSlots(id, movieID, movieName, bookingCapacity);
                    String req = "addSlot," + requestParameters;
                    sendRequest(req);

//                    LoggingHelper.log(id, "ADD MOVIE SLOT", requestParameters, "Success!", "Success!");


//                    System.out.println(ans);


                    displayMenu(obj);
                } catch (Exception e) {
                    System.out.println("Exception " + e);
                    LoggingHelper.log(id, "ADD MOVIE SLOT", requestParameters, "Failed!", "Failed!");
                }
            } else if (option == 2) {
                System.out.println("Please Enter the movie ID: ");
                String movieID = sc.nextLine();

                System.out.println("Please Enter the movie name: ");
                String movieName = sc.nextLine();
                String requestParameters = id + "," + movieID + "," + movieName;

                try {
//                    String ans = obj.removeMovieSlots(id, movieID, movieName);
//
//                    System.out.println(ans);

                    String req = "remSlot," + requestParameters;
                    sendRequest(req);

                    LoggingHelper.log(id, "RemoveMovieSlots", requestParameters, "Success!", "Success!");

                    displayMenu(obj);
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                    LoggingHelper.log(id, "RemoveMovieSlots", requestParameters, "Failed!", "Failed!");
                }
            } else if (option == 3) {
                System.out.println("Please enter the name of the movie:");
                String movieName = sc.nextLine();

                try {

                    String ans = obj.listMovieShowsAvailability(movieName);

                    System.out.println(ans);

                    LoggingHelper.log(id, "List Avaialable Shows", movieName, "Success!", "Success!");

                    displayMenu(obj);
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                    LoggingHelper.log(id, "List Avaialable Shows", movieName, "FAILED!", "FAILED!");
                }
            } else if (option == 4) {
                displayMenuCustomer(obj);
            } else if (option == 0) {
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Exception" + e);

        }
    }

    public static void displayMenuCustomer(MasterServerImpl obj) {
        try {
            System.out.println();
            System.out.println("Welcome Customer!");
            System.out.println("Please enter your ID: ");
            Scanner sc = new Scanner(System.in);

            String id = sc.nextLine();

            System.out.println("1. Book movie tickets");
            System.out.println("2. Get your booking schedule");
            System.out.println("3. Cancel the tickets");
            System.out.println("4. Exchange the tickets");
            System.out.println("5. Go back to Admin Menu");
            System.out.println("0. Exit");

            System.out.println("Please enter your option: ");
            int option = sc.nextInt();
            sc.nextLine();

            if (option == 1) {
                System.out.println("Please enter the movie ID: ");

                String movieID = sc.nextLine();

                System.out.println("Please enter the movie name: ");
                String movieName = sc.nextLine();

                System.out.println("Please enter the number of tickets you want: ");
                int numberOfTickets = sc.nextInt();
                sc.nextLine();

                String requestParameters = id + "," + movieID + "," + movieName + "," + numberOfTickets;

                try {
//                    System.out.println(obj.bookMovieTickets(id, movieID, movieName, numberOfTickets));

                    String req = "book," + requestParameters;
                    sendRequest(req);

                    LoggingHelper.log(id, "Book Movie Ticket", requestParameters, "Success!", "Success!");
                    displayMenuCustomer(obj);
                } catch (Exception e) {
                    LoggingHelper.log(id, "Book Movie Ticket", requestParameters, "Failed!", "Failed!");
                }
            } else if (option == 2) {
                System.out.println("---------------------------------------------------------");
                System.out.println("Your Bookings are as follows:");

                try {


                    String ans = obj.getBookingSchedule(id);

                    System.out.println("---------------------------------------------------------");
                    System.out.println(ans);
                    System.out.println("---------------------------------------------------------");
                    LoggingHelper.log(id, "Get Booking Schedule", id, "Success!", "Success!");

                    displayMenuCustomer(obj);
                } catch (Exception e) {
                    LoggingHelper.log(id, "Get Booking Schedule", id, "Failed!", "Failed!");
                }
            } else if (option == 3) {
                System.out.println("Please enter the movie ID: ");
                String movieID = sc.nextLine();

                System.out.println("Please enter the movie Name: ");
                String movieName = sc.nextLine();

                System.out.println("Please enter the number of tickets: ");
                int numberOfTickets = sc.nextInt();
                sc.nextLine();

                String requestParameters = id + "," + movieID + "," + movieName + "," + numberOfTickets;
                try {

//                    System.out.println(obj.cancelMovieTickets(id, movieID, movieName, numberOfTickets));

                    String req = "cancel," + requestParameters;
                    sendRequest(req);
                    LoggingHelper.log(id, "Cancel Movie Tickets", requestParameters, "Success!", "Success!");
                    displayMenuCustomer(obj);
                } catch (Exception e) {
                    LoggingHelper.log(id, "Cancel Movie Tickets", requestParameters, "Failed!", "Failed!");
                }

            } else if (option == 4) {
                System.out.println("Please enter movie id of existing ticket:");
                String cMovieID = sc.nextLine();
                System.out.println("Please enter movie name of existing ticket:");
                String cMovieName = sc.nextLine();
                System.out.println("Please enter the new movie ID:");
                String nMovieID = sc.nextLine();
                System.out.println("Please enter the new movie name:");
                String nMovieName = sc.nextLine();
                System.out.println("Please enter the number of tickets:");
                int numberOfTickets = sc.nextInt();
                sc.nextLine();
                String requestParameters = id + "," + cMovieID + "," + cMovieName + "," + nMovieID + "," + nMovieName + "," + numberOfTickets;


                try {
                    System.out.println(obj.exchangeTickets(id, cMovieID, cMovieName, nMovieID, nMovieName, numberOfTickets));
                    LoggingHelper.log(id, "Exchange Tickets", requestParameters, "Success!", "Success!");
                    displayMenuCustomer(obj);
                } catch (Exception e) {
                    System.out.println("Exception occured in admin client: " + e);
                    LoggingHelper.log(id, "Exchange Tickets", requestParameters, "Failed!", "Failed!");
                }

            } else if (option == 5) {
                displayMenu(obj);
            } else if (option == 0) {
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Exception in admin client: " + e);
        }
    }

    public static void sendRequest(String requestData1) throws IOException {
// Create a socket to send the request
        DatagramSocket socket = new DatagramSocket();

        // Define the front end's IP address and port number
        InetAddress frontEndAddress = InetAddress.getByName("localhost");
        int frontEndPort = 9000;

        // Create the request data
        String requestData = requestData1;
        byte[] requestBuffer = requestData.getBytes();

        // Create the UDP packet with the request data
        DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length, frontEndAddress, frontEndPort);

        // Send the request packet to the front end
        socket.send(requestPacket);

        // Receive the response
        byte[] buffer = new byte[1000];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);

        aSocket.receive(response);
        String sentence = new String(response.getData(), 0, response.getLength());
        System.out.println(sentence);
    }
}
