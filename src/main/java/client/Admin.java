package client;


import Replicas.Replica1.MTBInterface.MTBSInterface;
import config.CONFIGURATION;


import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Admin {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static FileHandler fh;
    static SimpleFormatter formatter = new SimpleFormatter();
    static String URL = null;
    static String userID = null;
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Admin c = new Admin();
        java.net.URL url = null;
        QName qname = null;
        Service service = null;
        try {

            MTBSInterface mtbsInterface = null;
            while (true) {
                if (userID == null) {
                    System.out.print("Please enter Admin ID {ATWA / VERA / OUTA}{XXXX}: ");
                    userID = sc.next();
                }

                String serverName = userID.substring(0, 3);
                if (userID.charAt(3) != 'A') {
                    System.out.println("You are not valid user, please try again!!!");
                    userID = null;
                    writeLog("Invalid Admin ID");

                } else if ((userID.substring(4).length()) != 4) {
                    System.out.println("You have entered the wrong ID, please enter the 4 digit unique ID!!!");
                    userID = null;
                    writeLog("Invalid Admin ID");

                } else if (!(serverName.equals("ATW")) && !(serverName.equals("OUT"))
                        && !(serverName.equals("VER"))) {
                    System.out.println("Please enter the proper serverName!");
                    userID = null;
                    writeLog("Invalid Admin ID : Invalid Server Name");
                }else {

//                    if (serverName.equals("ATW")) {
//                        url= new URL("http://localhost:5000/DMTBSATW/?wsdl");
//                        qname= new QName( "http://implementation/", "ATWImplementationService") ;
//                        service = Service.create(url,qname);
//                    }
//                    else if (serverName.equals("OUT")){
//                        url= new URL("http://localhost:5002/DMTBSOUT/?wsdl");
//                        qname= new QName( "http://implementation/", "OUTImplementationService") ;
//                        service = Service.create(url,qname);
//                    }
//                    else {
//                        url= new URL("http://localhost:5001/DMTBSVER/?wsdl");
//                        qname= new QName( "http://implementation/", "VERImplementationService") ;
//                        service = Service.create(url,qname);
//                    }

                    System.out.println("=========================================");
                    System.out.println("|               Actions                 |");
                    System.out.println("=========================================");
                    System.out.println("|  1 : Add movie slots                  |");
                    System.out.println("|  2 : Remove movie slots               |");
                    System.out.println("|  3 : List movie shows availabilities  |");
                    System.out.println("|  4 : Book movie tickets               |");
                    System.out.println("|  5 : Get booking schedule             |");
                    System.out.println("|  6 : Cancel Bookings                  |");
                    System.out.println("|  7 : Exit                             |");
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
                            System.out.print("\nPlease enter slot capacity: ");
                            numberOfSlots = sc.nextInt();
                            sc.nextLine();
                            System.out.print("\nPlease Enter the Movie ID {ATW/VER/OUT}{M/A/E}{DDMMYY}: ");
                            movieId = sc.next();
                            writeLog(userID + " : Add slot | Request Parameters : Movie Id: " + movieId + " Movie Name: " + movieName+ " Number of Slots: " + numberOfSlots);
                            FrontEnd requestFE = new FrontEnd(CONFIGURATION.SEQUENCER_IP,CONFIGURATION.SEQUENCER_PORT,0,0,0);
                            String message = "Add;"+ movieId +";"+ movieName +";"+ numberOfSlots;
                            requestFE.sendRequest(message);
//                            mtbsInterface = service.getPort(MTBSInterface.class);
//                            String result = mtbsInterface.addMovieSlots(movieId,movieName,numberOfSlots);
//                            System.out.println(result);
                            break;
                        case 2:
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
                            System.out.print("\nPlease Enter the Movie ID {ATW/VER/OUT}{M/A/E}{DDMMYY}: ");
                            movieId = sc.next();
//                            mtbsInterface = service.getPort(MTBSInterface.class);
                            writeLog(userID + " : Remove slot | Request Parameters : Movie Id: " + movieId + " Movie Name: " + movieName);
//                            result = mtbsInterface.removeMovieSlots(movieId, movieName);
//                            System.out.println(result);

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

                            mtbsInterface = service.getPort(MTBSInterface.class);
                            System.out.println("\nFetching show details for "+ movieName+"\n");
                            writeLog(userID + " : Display available slots | Request Parameters : Movie Name: " + movieName);
//                            result = mtbsInterface.listMovieShowsAvailability(movieName);
                            System.out.println("============================\nAvailable shows for " + movieName
                                    + "\n============================\n");
//                            String[] showsList = result.split(",");
//                            for (int i = 0; i < showsList.length; i++) {
//                                System.out.println(showsList[i]);
//                            }
                            break;
                        case 4:
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
                            System.out.print("Please enter customer ID: ");
                            String customerID = sc.next();
                            mtbsInterface = service.getPort(MTBSInterface.class);
                            writeLog(customerID + " : Book Tickets | Request Parameters : Movie Id: " + movieId + " Movie Name: " + movieName+ " Number of Tickets: " + numberOfTicket);
//                            result = mtbsInterface.bookMovieTickets(customerID,movieId,movieName,numberOfTicket);
//                            System.out.println(result);
                            break;
                        case 5:
                            mtbsInterface = service.getPort(MTBSInterface.class);
                            System.out.print("Please enter customer ID: ");
                            customerID = sc.next();
                            writeLog(userID + " : Get Bookings | Request Parameters : Customer Id: " + customerID);

                            System.out.println("Fetching all the bookings...");
//                            result = mtbsInterface.getBookingSchedule(customerID);
                            System.out.println("================================\nBookings of customer " + customerID
                                    + "\n================================\n");
//                            showsList = result.split(",");
//                            for (int i = 0; i < showsList.length; i++) {
//                                System.out.println(showsList[i]);
//                            }
                            break;
                        case 6:
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
                            System.out.print("\nPlease enter number of ticket you want to cancel: ");
                            numberOfTicket = sc.nextInt();
                            sc.nextLine();
                            System.out.print("\nPlease Enter the Movie ID {ATW/VER/OUT}{M/A/E}{DDMMYY}: ");
                            movieId = sc.next();
                            System.out.print("Please enter customer ID: ");
                            customerID = sc.next();
                            mtbsInterface = service.getPort(MTBSInterface.class);mtbsInterface = service.getPort(MTBSInterface.class);
                            writeLog(userID + " : Cancel Tickets | Request Parameters : Customer Id: "+customerID+" Movie Id: " + movieId + " Movie Name: " + movieName+ " Number of Slots: " + numberOfTicket);
//                            result = mtbsInterface.cancelMovieTickets(customerID,movieId,movieName,numberOfTicket);
//                            System.out.println(result);
                            break;
                        case 7:
                            exited = true;
                            break;
                    }

                    if (exited){
                        writeLog("Admin Logging out.");
                        System.out.println("Admin Logged out....");
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

    public static void writeLog(String message){
        try {

            fh = new FileHandler("src/logs/"+userID+"Log.log", 0,1,true);

            fh.setFormatter(formatter);

            logger.addHandler(fh);

            logger.setUseParentHandlers(false);

            logger.info("Log from "+ userID +"(Customer) : "+ message);

            fh.close();

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            logger.info("File Handler Exception!");
            fh.close();
        }
    }
}
