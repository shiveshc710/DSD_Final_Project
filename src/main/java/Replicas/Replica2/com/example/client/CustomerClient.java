package Replicas.Replica2.com.example.client;


import Replicas.Replica2.com.example.logging.LoggingHelper;
import config.CONFIGURATION;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.*;

/**
 * Customer client for customer operations.
 *
 */
public class CustomerClient {


    private static MasterServerImpl MasterServerRef;
    public static void main(String[] args) {

        try {
            URL url = new URL("http://localhost:"+CONFIGURATION.Main_PORT_2+"/masterservice?wsdl");
            QName qname = new QName("http://example.com/master", "MasterServerImplService");
            Service service = Service.create(url, qname);
            MasterServerRef = service.getPort(MasterServerImpl.class);

            displayMenu(MasterServerRef);
        } catch (Exception e) {
            System.out.println("Error in adminClient: " + e);
        }
    }

    /**
     * Main menu for Customer Client.
     *
     * @param obj       The object of interface.
     */
    public static void displayMenu(MasterServerImpl obj) {
        try {
            System.out.println();
            System.out.println("Welcome Customer!");
            System.out.println("Please enter your ID: ");
            Scanner sc = new Scanner(System.in);

            String id = sc.nextLine();

            System.out.println("1. Book movie tickets");
            System.out.println("2. Get your booking schedule");
            System.out.println("3. Cancel the tickets");
            System.out.println("4. Exchange your tickets");
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
                    System.out.println(obj.bookMovieTickets(id, movieID, movieName, numberOfTickets));

                    LoggingHelper.log(id, "Book Movie Ticket", requestParameters, "Success!", "Success!");
                    displayMenu(obj);
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

                    displayMenu(obj);
                }catch (Exception e){
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

                    System.out.println(obj.cancelMovieTickets(id, movieID, movieName, numberOfTickets));

                    LoggingHelper.log(id, "Cancel Movie Tickets", requestParameters, "Success!", "Success!");
                    displayMenu(obj);
                }catch (Exception e){
                    LoggingHelper.log(id, "Cancel Movie Tickets", requestParameters, "Failed!", "Failed!");
                }
            }
            else if(option == 4){
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
                String requestParameters = id + "," + cMovieID + "," + cMovieName + ","+ nMovieID + "," +nMovieName + "," + numberOfTickets;



                try {
                    System.out.println(obj.exchangeTickets(id, cMovieID, cMovieName, nMovieID, nMovieName, numberOfTickets));
                    LoggingHelper.log(id, "Exchange Tickets", requestParameters, "Success!", "Success!");
                    displayMenu(obj);
                }catch (Exception e){
                    System.out.println("Exception occured in admin client: "+ e);
                    LoggingHelper.log(id, "Exchange Tickets", requestParameters, "Failed!", "Failed!");
                }

            }else if (option == 0) {
                System.exit(0);
            }
        } catch (Exception e) {

        }
    }
}


