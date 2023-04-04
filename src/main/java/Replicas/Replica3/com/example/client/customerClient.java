package com.example.client;

import Replicas.Replica3.com.example.client.adminClient;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class customerClient {

    public static void main(String[] args) throws MalformedURLException {


        displayMenu(0);
    }

    public static void displayMenu(int check) throws MalformedURLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Movie Ticket Booking System.");
        System.out.println("Enter your ID:");
        String id = sc.nextLine();
        boolean validUser = id.startsWith("ATWA") || id.startsWith("VERA") || id.startsWith("OUTA") || id.startsWith("ATWC") || id.startsWith("VERC") || id.startsWith("OUTC");
        if (validUser) {
            System.out.println("Choose the operation you want to perform:");

            System.out.println("1.  Book a ticket.");
            System.out.println("2.  List all booked tickets");
            System.out.println("3.  Cancel a ticket");
            System.out.println("4.  Exchange tickets");
            System.out.println("5.  Exit");
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
                System.out.println("Enter number of tickets to book:");
                int capacity = sc.nextInt();
                sc.nextLine();
                URL url = new URL("http://localhost:8080/mainserver?wsdl");
                QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
                Service service = Service.create(url, qname);
                com.example.client.MainServerImpl mainServerImpl = service.getPort(com.example.client.MainServerImpl.class);
                System.out.println(mainServerImpl.bookMovieTickets(id, mID, movieName, capacity));
                displayMenu(check);
            } else if (choice == 2) {
                System.out.println("Enter Customer ID:");
                String movieName = sc.nextLine();
                URL url = new URL("http://localhost:8080/mainserver?wsdl");
                QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
                Service service = Service.create(url, qname);
                com.example.client.MainServerImpl mainServerImpl = service.getPort(com.example.client.MainServerImpl.class);
                String result = mainServerImpl.getBookingSchedule(movieName);
                System.out.println(result);
                displayMenu(check);
            } else if (choice == 3) {
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
                System.out.println("Enter number of tickets to cancel:");
                int capacity = sc.nextInt();
                sc.nextLine();
                URL url = new URL("http://localhost:8080/mainserver?wsdl");
                QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
                Service service = Service.create(url, qname);
                com.example.client.MainServerImpl mainServerImpl = service.getPort(com.example.client.MainServerImpl.class);
                System.out.println(mainServerImpl.cancelMovieTickets(id, mID, movieName, capacity));
                displayMenu(check);
            } else if (choice == 4) {
                System.out.println("Choose the area of the old show you want to exchange:");
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
                System.out.println("Choose the time of the old show you want to exchange:");
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
                System.out.println("Enter old movie name to exchange:");
                String movieName = sc.nextLine();
                System.out.println("Enter new movie name to exchange with:");
                String newmovieName = sc.nextLine();
                System.out.println("Enter number of tickets to exchange:");
                int capacity = sc.nextInt();
                sc.nextLine();
                System.out.println("Choose the area of the new show:");
                System.out.println("1.  Atwater");
                System.out.println("2.  Verdun");
                System.out.println("3.  Outremont");
                int areaChoice1 = sc.nextInt();
                sc.nextLine();
                String area1 = null;
                if (areaChoice1 == 1) {
                    area1 = "ATW";
                } else if (areaChoice1 == 2) {
                    area1 = "VER";
                } else if (areaChoice1 == 3) {
                    area1 = "OUT";
                }
                System.out.println("Choose the time of the new show:");
                System.out.println("1.  Morning (M)");
                System.out.println("2.  Afternoon (A)");
                System.out.println("3.  Evening (E)");
                int timeChoice1 = sc.nextInt();
                sc.nextLine();
                String time1 = null;
                if (timeChoice1 == 1) {
                    time1 = "M";
                } else if (timeChoice1 == 2) {
                    time1 = "A";
                } else if (timeChoice1 == 3) {
                    time1 = "E";
                }
                System.out.println("Enter date in dd-MM-yy format:");
                String date1 = sc.nextLine();
                date1 = date1.replace("-", "");
                String mID1 = area1 + time1 + date1;
                ///
                URL url = new URL("http://localhost:8080/mainserver?wsdl");
                QName qname = new QName("http://example.com/mainserver", "MainServerImplService");
                Service service = Service.create(url, qname);
                com.example.client.MainServerImpl mainServerImpl = service.getPort(com.example.client.MainServerImpl.class);
                System.out.println(mainServerImpl.exchangeTickets(id, mID, movieName, mID1, newmovieName, capacity));
                displayMenu(check);
            } else {
                if (check == 1) {
                    adminClient.displayMenu();
                } else {
                    System.out.println("Thank you!");
                    System.exit(0);
                }
            }
        } else {
            System.out.println("Unauthorized User. Please use a valid ID");
            displayMenu(check);
        }
    }
}
