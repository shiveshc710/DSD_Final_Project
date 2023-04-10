package Replicas.Replica3;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.HashMap;

@WebService(targetNamespace = "http://example.com/mainserver")
public class MainServerImpl {
    private HashMap<String, Integer> customerBookingInDiffAreas = new HashMap<String, Integer>();


    public static void main(String[] args) {
        String url = "http://localhost:8080/mainserver";
        Endpoint.publish(url, new MainServerImpl());
        System.out.println("Main Server is running and available at " + url + "?wsdl");
    }

    @WebMethod
    public String addMovieSlots(String adminID, String movieID, String movieName, int bookingCapacity) {
        String result = "";

        if (adminID.startsWith("ATW")) {
            try {
                if (!movieID.startsWith("ATW")) {
                    result = "Failed";
                    return result;
                }
                URL url = new URL("http://localhost:8081/atw?wsdl");
                QName qname = new QName("http://example.com/atw", "ATWImplService");
                Service service = Service.create(url, qname);
                com.example.client.ATWImpl atwImpl = service.getPort(com.example.client.ATWImpl.class);
                result = atwImpl.addMovieSlotsATW(adminID, movieID, movieName, bookingCapacity);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (adminID.startsWith("VER")) {
            if (!movieID.startsWith("VER")) {
                result = "Failed";
                return result;
            }
            try {
                URL url = new URL("http://localhost:8082/ver?wsdl");
                QName qname = new QName("http://example.com/ver", "VERImplService");
                Service service = Service.create(url, qname);
                com.example.client.VERImpl verImpl = service.getPort(com.example.client.VERImpl.class);
                result = verImpl.addMovieSlotsVER(adminID, movieID, movieName, bookingCapacity);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (adminID.startsWith("OUT")) {
            if (!movieID.startsWith("OUT")) {
                result = "Failed";
                return result;
            }
            try {
                URL url = new URL("http://localhost:8083/out?wsdl");
                QName qname = new QName("http://example.com/out", "OUTImplService");
                Service service = Service.create(url, qname);
                com.example.client.OUTImpl outImpl = service.getPort(com.example.client.OUTImpl.class);
                result = outImpl.addMovieSlotsOUT(adminID, movieID, movieName, bookingCapacity);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        }
        return result;
    }

    @WebMethod
    public String removeMovieSlots(String adminID, String movieID, String movieName) {
        String result = "";
        if (adminID.startsWith("ATW")) {
            try {
                URL url = new URL("http://localhost:8081/atw?wsdl");
                QName qname = new QName("http://example.com/atw", "ATWImplService");
                Service service = Service.create(url, qname);
                com.example.client.ATWImpl atwImpl = service.getPort(com.example.client.ATWImpl.class);
                result = atwImpl.removeMovieSlotsATW(adminID, movieID, movieName);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (adminID.startsWith("VER")) {
            try {
                URL url = new URL("http://localhost:8082/ver?wsdl");
                QName qname = new QName("http://example.com/ver", "VERImplService");
                Service service = Service.create(url, qname);
                com.example.client.VERImpl verImpl = service.getPort(com.example.client.VERImpl.class);
                result = verImpl.removeMovieSlotsVER(adminID, movieID, movieName);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (adminID.startsWith("OUT")) {
            try {
                URL url = new URL("http://localhost:8083/out?wsdl");
                QName qname = new QName("http://example.com/out", "OUTImplService");
                Service service = Service.create(url, qname);
                com.example.client.OUTImpl outImpl = service.getPort(com.example.client.OUTImpl.class);
                result = outImpl.removeMovieSlotsOUT(adminID, movieID, movieName);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        }

        System.out.println(result);
        return result;
    }

    @WebMethod
    public String listMovieShowsAvailability(String movieName) {
        String result = "";
        try {
            URL url = new URL("http://localhost:8081/atw?wsdl");
            QName qname = new QName("http://example.com/atw", "ATWImplService");
            Service service = Service.create(url, qname);
            com.example.client.ATWImpl atwImpl = service.getPort(com.example.client.ATWImpl.class);
            result = atwImpl.listMovieShowsAvailabilityATW(movieName);

            URL url2 = new URL("http://localhost:8082/ver?wsdl");
            QName qname2 = new QName("http://example.com/ver", "VERImplService");
            Service service2 = Service.create(url2, qname2);
            com.example.client.VERImpl verImpl = service2.getPort(com.example.client.VERImpl.class);
            result += verImpl.listMovieShowsAvailabilityVER(movieName);

            URL url3 = new URL("http://localhost:8083/out?wsdl");
            QName qname3 = new QName("http://example.com/out", "OUTImplService");
            Service service3 = Service.create(url3, qname3);
            com.example.client.OUTImpl outImpl = service3.getPort(com.example.client.OUTImpl.class);
            result += outImpl.listMovieShowsAvailabilityOUT(movieName);
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.err);
        }
        System.out.println(result);
        return result.equals("") ? "No result Found!!" : result;
    }

    @WebMethod
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        String result = "";
        boolean same = customerID.substring(0, 3).equals(movieID.substring(0, 3));
        if (customerBookingInDiffAreas.containsKey(customerID)) {
            int currentBookings = customerBookingInDiffAreas.get(customerID);
            boolean notSameArea = !same;
            if (currentBookings >= 3) {
                if (notSameArea) {
                    return "Cannot book more than 3 movies in different areas!";
                }
            }

            if (notSameArea) {
                customerBookingInDiffAreas.put(customerID, currentBookings + 1);
            }
        } else {
            if (same) {
                customerBookingInDiffAreas.put(customerID, 0);
            }
            else {
                customerBookingInDiffAreas.put(customerID, 1);
            }
        }

        if (movieID.startsWith("ATW")) {
            try {
                URL url = new URL("http://localhost:8081/atw?wsdl");
                QName qname = new QName("http://example.com/atw", "ATWImplService");
                Service service = Service.create(url, qname);
                com.example.client.ATWImpl atwImpl = service.getPort(com.example.client.ATWImpl.class);
                result = atwImpl.bookMovieTicketsATW(customerID, movieID, movieName, numberOfTickets);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (movieID.startsWith("VER")) {
            try {
                URL url = new URL("http://localhost:8082/ver?wsdl");
                QName qname = new QName("http://example.com/ver", "VERImplService");
                Service service = Service.create(url, qname);
                com.example.client.VERImpl verImpl = service.getPort(com.example.client.VERImpl.class);
                result = verImpl.bookMovieTicketsVER(customerID, movieID, movieName, numberOfTickets);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (movieID.startsWith("OUT")) {
            try {
                URL url = new URL("http://localhost:8083/out?wsdl");
                QName qname = new QName("http://example.com/out", "OUTImplService");
                Service service = Service.create(url, qname);
                com.example.client.OUTImpl outImpl = service.getPort(com.example.client.OUTImpl.class);
                result = outImpl.bookMovieTicketsOUT(customerID, movieID, movieName, numberOfTickets);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        }

        System.out.println(result);
        return result;
    }

    @WebMethod
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        String result = "";
        if (movieID.startsWith("ATW")) {
            try {
                URL url = new URL("http://localhost:8081/atw?wsdl");
                QName qname = new QName("http://example.com/atw", "ATWImplService");
                Service service = Service.create(url, qname);
                com.example.client.ATWImpl atwImpl = service.getPort(com.example.client.ATWImpl.class);
                result = atwImpl.cancelMovieTicketsATW(customerID, movieID, movieName, numberOfTickets);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (movieID.startsWith("VER")) {
            try {
                URL url = new URL("http://localhost:8082/ver?wsdl");
                QName qname = new QName("http://example.com/ver", "VERImplService");
                Service service = Service.create(url, qname);
                com.example.client.VERImpl verImpl = service.getPort(com.example.client.VERImpl.class);
                result = verImpl.cancelMovieTicketsVER(customerID, movieID, movieName, numberOfTickets);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        } else if (movieID.startsWith("OUT")) {
            try {
                URL url = new URL("http://localhost:8083/out?wsdl");
                QName qname = new QName("http://example.com/out", "OUTImplService");
                Service service = Service.create(url, qname);
                com.example.client.OUTImpl outImpl = service.getPort(com.example.client.OUTImpl.class);
                result = outImpl.cancelMovieTicketsOUT(customerID, movieID, movieName, numberOfTickets);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                e.printStackTrace(System.err);
            }
        }

        System.out.println(result);
        return result;
    }

    @WebMethod
    public String getBookingSchedule(String customerID) {
        String result = "";
        try {
            result+= "----------Atwater----------\n";
            URL url = new URL("http://localhost:8081/atw?wsdl");
            QName qname = new QName("http://example.com/atw", "ATWImplService");
            Service service = Service.create(url, qname);
            com.example.client.ATWImpl atwImpl = service.getPort(com.example.client.ATWImpl.class);
            String atwresult = atwImpl.getBookingScheduleATW(customerID);
            if (atwresult.equals(""))
                atwresult = "No Bookings found\n";

            result += atwresult;

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.err);
        }
        try {
            result+= "----------Verdun----------\n";
            URL url = new URL("http://localhost:8082/ver?wsdl");
            QName qname = new QName("http://example.com/ver", "VERImplService");
            Service service = Service.create(url, qname);
            com.example.client.VERImpl verImpl = service.getPort(com.example.client.VERImpl.class);
            String verresult = verImpl.getBookingScheduleVER(customerID);

            if (verresult.equals(""))
                verresult = "No Bookings found\n";

            result += verresult;
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.err);
        }
        try {
            result+= "----------Outremont----------\n";
            URL url = new URL("http://localhost:8083/out?wsdl");
            QName qname = new QName("http://example.com/out", "OUTImplService");
            Service service = Service.create(url, qname);
            com.example.client.OUTImpl outImpl = service.getPort(com.example.client.OUTImpl.class);
            String outresult = outImpl.getBookingScheduleOUT(customerID);

            if (outresult.equals(""))
                outresult = "No Bookings found\n";

            result += outresult;
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.err);
        }
        return result;
    }

    @WebMethod
    public String exchangeTickets(String customerID, String movieID, String movieName, String new_movieID, String new_movieName, int numberOfTickets) {
        String result;
        result = cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
        if (result.equals("Success")) {
            result = bookMovieTickets(customerID, new_movieID, new_movieName, numberOfTickets);
            if (result.equals("Success")) {
                return "Success";
            } else {
                bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
                return "Failed";
            }
        }
        return result;
    }
}
