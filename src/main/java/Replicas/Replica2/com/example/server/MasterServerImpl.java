package Replicas.Replica2.com.example.server;


import Replicas.Replica2.com.example.client.VERServerImpl;
import Replicas.Replica2.com.example.client.ATWServerImpl;
import Replicas.Replica2.com.example.client.OUTServerImpl;
import Replicas.Replica2.com.example.logging.LoggingHelper;
import config.CONFIGURATION;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.HashMap;

@WebService(targetNamespace = "http://example.com/master")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class MasterServerImpl implements BookingSystemInterface {


    private HashMap<String, Integer> customerBookingInDiffAreas = new HashMap<>();


    private ATWServerImpl atwServerREF;

    private OUTServerImpl outServerREF;

    private Replicas.Replica2.com.example.client.VERServerImpl verServerREF;


    @Override
    public void createATWObject() {
        try {
            URL url = new URL("http://localhost:" + CONFIGURATION.ATW_PORT_2 + "/ATWServer?wsdl");
            QName qname = new QName("http://example.com/atw", "ATWServerImplService");
            Service service = Service.create(url, qname);
            ATWServerImpl port = service.getPort(ATWServerImpl.class);
            atwServerREF = port;
        } catch (Exception e) {
            System.out.println("Error in createATWObject: " + e);
        }
    }


    @Override
    public void createOUTObject() {
        //Taking reference from OUT Server
        try {
            URL url = new URL("http://localhost:" + CONFIGURATION.OUT_PORT_2 + "/OUTServer?wsdl");
            QName qname = new QName("http://example.com/out", "OUTServerImplService");
            Service service = Service.create(url, qname);
            OUTServerImpl port = service.getPort(OUTServerImpl.class);
            outServerREF = port;
        } catch (Exception e) {
            System.out.println("Error in createOUTObject: " + e);
        }
    }

    @Override
    public void createVERObject() {
        //Taking reference from OUT Server
        try {
            URL url = new URL("http://localhost:" + CONFIGURATION.VER_PORT_2 + "/VERServer?wsdl");
            QName qname = new QName("http://example.com/ver", "VERServerImplService");
            Service service = Service.create(url, qname);
            Replicas.Replica2.com.example.client.VERServerImpl port = service.getPort(VERServerImpl.class);
            verServerREF = port;
        } catch (Exception e) {
            System.out.println("Error in createVERObject: " + e);
        }
    }

    /**
     * Add movie slots for ADMIN in main server.
     *
     * @param adminID         The admin who is sending the request.
     * @param movieID         The ID of the movie.
     * @param movieName       The name of the movie.
     * @param bookingCapacity The capacity of the theatre.
     */
    @Override
    public String addMovieSlots(String adminID, String movieID, String movieName, int bookingCapacity) {
        try {
            if (adminID.startsWith("ATWA")) {
                if (atwServerREF == null) {
                    this.createATWObject();
                }
                if (!movieID.startsWith("ATW")) {
                    return "Failed";
                }
                return atwServerREF.addMovieSlots(adminID, movieID, movieName, bookingCapacity);
            } else if (adminID.startsWith("OUTA")) {
                if (outServerREF == null) {
                    this.createOUTObject();
                }
                if (!movieID.startsWith("OUT")) {
                    return "Failed";
                }
                return outServerREF.addMovieSlots(adminID, movieID, movieName, bookingCapacity);
            } else if (adminID.startsWith("VERA")) {
                if (verServerREF == null) {
                    this.createVERObject();
                }
                if (!movieID.startsWith("VER")) {
                    return "Failed";
                }
                return verServerREF.addMovieSlots(adminID, movieID, movieName, bookingCapacity);
            }
        } catch (Exception e) {
            System.out.println("Error Occured while adding movie: " + e);
        }

        return "Failed";
    }


    /**
     * Remove movie slots for ADMIN in main server.
     *
     * @param adminID   The admin who is sending the request.
     * @param movieID   The ID of the movie.
     * @param movieName The name of the movie.
     */
    @Override
    public String removeMovieSlots(String adminID, String movieID, String movieName) {

        try {
            if (adminID.startsWith("ATWA")) {
                if (atwServerREF == null) {
                    this.createATWObject();
                }
                return atwServerREF.removeMovieSlots(adminID, movieID, movieName);
            } else if (adminID.startsWith("OUTA")) {
                if (outServerREF == null) {
                    this.createOUTObject();
                }
                return outServerREF.removeMovieSlots(adminID, movieID, movieName);
            } else if (adminID.startsWith("VERA")) {
                if (verServerREF == null) {
                    this.createVERObject();
                }
                return verServerREF.removeMovieSlots(adminID, movieID, movieName);
            }
        } catch (Exception e) {
            System.out.println("Error Occurred: " + e);
        }
        return "Failed";
    }

    /**
     * List movie slots for all regions for ADMIN in main server.
     *
     * @param movieName The name of the movie.
     */
    @Override
    public String listMovieShowsAvailability(String movieName) {
        try {

            if (atwServerREF == null) {
                this.createATWObject();
            }

            if (outServerREF == null) {
                this.createOUTObject();
            }

            if (verServerREF == null) {
                this.createVERObject();
            }

            String completeAns = atwServerREF.listMovieShowsAvailability(movieName) + outServerREF.listMovieShowsAvailability(movieName) + verServerREF.listMovieShowsAvailability(movieName);

            return completeAns.equals("") ? "No result Found!!" : completeAns;
        } catch (Exception e) {
            System.out.println("Error Occurred in main server: " + e);
            return null;
        }
    }

    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        try {

            if (customerBookingInDiffAreas.containsKey(customerID)) {
                int currentBookings = customerBookingInDiffAreas.get(customerID);
                if (currentBookings >= 3) {
                    if (!customerID.substring(0, 3).equals(movieID.substring(0, 3))) {
                        return "Cannot book more than 3 movies in different areas!";
                    }
                }

                if (!customerID.substring(0, 3).equals(movieID.substring(0, 3))) {
                    customerBookingInDiffAreas.put(customerID, currentBookings + 1);
                }
            } else {
                customerBookingInDiffAreas.put(customerID, 0);
            }

            if (atwServerREF == null) {
                this.createATWObject();
            }

            if (outServerREF == null) {
                this.createOUTObject();
            }

            if (verServerREF == null) {
                this.createVERObject();
            }

            if (movieID.startsWith("ATW")) {
                return atwServerREF.bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
            } else if (movieID.startsWith("OUT")) {
                return outServerREF.bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
            } else if (movieID.startsWith("VER")) {
                return verServerREF.bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
            }
        } catch (Exception e) {
            System.out.println("Exception Occurred: " + e);
        }
        return "Failed";
    }

    @Override
    public String getBookingSchedule(String customerID) {
        try {

            if (atwServerREF == null) {
                this.createATWObject();
            }

            if (outServerREF == null) {
                this.createOUTObject();
            }

            if (verServerREF == null) {
                this.createVERObject();
            }

            String completeAns = "";

            String atwresult = atwServerREF.getBookingSchedule(customerID);
            String verresult = verServerREF.getBookingSchedule(customerID);
            String outresult = outServerREF.getBookingSchedule(customerID);

            if (atwresult.equals(""))
                atwresult = "No Bookings found\n";
            if (verresult.equals(""))
                verresult = "No Bookings found\n";
            if (outresult.equals(""))
                outresult = "No Bookings found\n";


            completeAns = "----------Atwater----------\n" + atwresult +
                    "----------Verdun----------\n" + verresult +
                    "----------Outremont----------\n" + outresult;


            return completeAns;
        } catch (Exception e) {
            System.out.println("Exception Occurred in main server: " + e);
        }
        return "";
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {


        try {

            if (atwServerREF == null) {
                this.createATWObject();
            }

            if (outServerREF == null) {
                this.createOUTObject();
            }

            if (verServerREF == null) {
                this.createVERObject();
            }
            if (movieID.startsWith("ATW")) {

                return atwServerREF.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
            } else if (movieID.startsWith("OUT")) {
                return outServerREF.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
            } else if (movieID.startsWith("VER")) {
                return verServerREF.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
            }

        } catch (Exception e) {
            System.out.println("Error Occurred in main server: " + e);
        }
        return "Failed";
    }

    @Override
    public String exchangeTickets(String customerID, String movieID, String movieName, String new_movieID, String
            new_movieName, int numberOfTickets) {

        String requestParameters = customerID + "," + movieID + "," + movieName + "," + new_movieID + "," + new_movieName + "," + numberOfTickets;
        try {
            String ans = cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
            if (ans.equals("Success")) {
                ans = bookMovieTickets(customerID, new_movieID, new_movieName, numberOfTickets);
                if (ans.equals("Success")) {
                    return "Success";
                } else {
                    bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
                    return "Failed";
                }
            }
            return ans;
        } catch (Exception e) {
            System.out.println("Exception in server: " + e);
            LoggingHelper.log(this.getClass().getName(), "Exchange Tickets", requestParameters, "Failed!", "Failed!");
            return "Failed";
        }

    }
}

