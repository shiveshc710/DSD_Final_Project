package Replicas.Replica3.com.example.client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

public class adminClient {
    public static void main(String[] args) {
        try {

            displayMenu();

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.err);
        }
    }

    public static void displayMenu() throws MalformedURLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Movie Ticket Booking System.");
        System.out.println("Enter your ID:");
        String id = sc.nextLine();
        boolean validUser = id.startsWith("ATWA") || id.startsWith("VERA") || id.startsWith("OUTA");
        if (validUser) {
            System.out.println("Choose the operation you want to perform:");

            System.out.println("1.  Add a movie slot.");
            System.out.println("2.  Remove a movie slot.");
            System.out.println("3.  List movie shows available at the moment.");
            System.out.println("4.  Do customer operation");
            System.out.println("5.  Exit.");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) {
                System.out.println("Choose the area you want to select:");
                System.out.println("1.  Atwater");
                System.out.println("2.  Verdun");
                System.out.println("3.  Outremont");
                int areaChoice = sc.nextInt();
                sc.nextLine();
                String area = null;
                if (areaChoice == 1) {
                    area = "ATW";
                } else if (areaChoice == 2) {
                    area = "VER";
                } else if (areaChoice == 3) {
                    area = "OUT";
                }
                System.out.println("Choose the time of the show you want to select:");
                System.out.println("1.  Morning (M)");
                System.out.println("2.  Afternoon (A)");
                System.out.println("3.  Evening (E)");
                int timeChoice = sc.nextInt();
                sc.nextLine();
                String time = null;
                if (timeChoice == 1) {
                    time = "M";
                } else if (timeChoice == 2) {
                    time = "A";
                } else if (timeChoice == 3) {
                    time = "E";
                }
                System.out.println("Enter date in dd-MM-yy format:");
                String date = sc.nextLine();
                date = date.replace("-", "");
                String mID = area + time + date;
                System.out.println("Enter movie name:");
                String movieName = sc.nextLine();
                System.out.println("Enter movie seating capacity:");
                int capacity = sc.nextInt();
                sc.nextLine();
                URL url = new URL("http://localhost:8080/mainserver?wsdl");
                QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
                Service service = Service.create(url, qname);
                com.example.client.MainServerImpl mainServerImpl = service.getPort(com.example.client.MainServerImpl.class);
                System.out.println(mainServerImpl.addMovieSlots(id, mID, movieName, capacity));
                displayMenu();
            } else if (choice == 2) {
                System.out.println("Choose the area you want to select:");
                System.out.println("1.  Atwater");
                System.out.println("2.  Verdun");
                System.out.println("3.  Outremont");
                int areaChoice = sc.nextInt();
                sc.nextLine();
                String area = null;
                if (areaChoice == 1) {
                    area = "ATW";
                } else if (areaChoice == 2) {
                    area = "VER";
                } else if (areaChoice == 3) {
                    area = "OUT";
                }
                System.out.println("Choose the time of the show you want to select:");
                System.out.println("1.  Morning (M)");
                System.out.println("2.  Afternoon (A)");
                System.out.println("3.  Evening (E)");
                int timeChoice = sc.nextInt();
                sc.nextLine();
                String time = null;
                if (timeChoice == 1) {
                    time = "M";
                } else if (timeChoice == 2) {
                    time = "A";
                } else if (timeChoice == 3) {
                    time = "E";
                }
                System.out.println("Enter date in dd-MM-yy format:");
                String date = sc.nextLine();
                date = date.replace("-", "");
                String mID = area + time + date;
                System.out.println("Enter movie name:");
                String movieName = sc.nextLine();
                URL url = new URL("http://localhost:8080/mainserver?wsdl");
                QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
                Service service = Service.create(url, qname);
                com.example.client.MainServerImpl mainServerImpl = service.getPort(com.example.client.MainServerImpl.class);
                System.out.println(mainServerImpl.removeMovieSlots(id, mID, movieName));
                displayMenu();
            } else if (choice == 3) {
                System.out.println("Enter movie name:");
                String movieName = sc.nextLine();
                URL url = new URL("http://localhost:8080/mainserver?wsdl");
                QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
                Service service = Service.create(url, qname);
                com.example.client.MainServerImpl mainServerImpl = service.getPort(com.example.client.MainServerImpl.class);
                System.out.println(mainServerImpl.listMovieShowsAvailability(movieName));
                displayMenu();
            } else if (choice == 4) {
                com.example.client.customerClient.displayMenu( 1);
            } else {
                System.out.println("Thank you for using our system.");
                System.exit(0);
            }
        } else {
            System.out.println("Unauthorized User. Please use a valid ID");
            displayMenu();
        }
    }
}
