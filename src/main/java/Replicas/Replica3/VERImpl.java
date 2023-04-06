package Replicas.Replica3;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebService(targetNamespace = "http://example.com/ver")
public class VERImpl {
    private HashMap<String, HashMap<String, Integer>> movieInfo = new HashMap<String, HashMap<String, Integer>>();
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> moviesBookedInfo = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();

    public static void main(String[] args) {
        String url = "http://localhost:8082/ver";
        Endpoint.publish(url, new VERImpl());
        System.out.println("VER is running and available at " + url + "?wsdl");
    }
    @WebMethod
    public String addMovieSlotsVER(String adminID, String movieID, String movieName, int bookingCapacity) {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getTimeInstance();
        String currentTime = dateFormat.format(date);
        String requestType = "Add Movie Slots";
        String requestParameters = adminID + "," + movieID + "," + movieName + "," + bookingCapacity;
        String status = "Success";
        String serverResponse = "Movie Slots Added";
        try {
            if (!movieInfo.containsKey(movieName)) {
                HashMap<String, Integer> value = new HashMap<String, Integer>();
                value.put(movieID, bookingCapacity);
                movieInfo.put(movieName, value);
                logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
                Set<String> keys = movieInfo.keySet();
                for (String key : keys) {
                    System.out.println(key + ": " + movieInfo.get(key));
                }
                return status;
            } else {
                HashMap<String, Integer> value = movieInfo.get(movieName);
                if (value.containsKey(movieID)) {
                    value.put(movieID, bookingCapacity);
                    movieInfo.put(movieName, value);
                    logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
                    Set<String> keys = movieInfo.keySet();
                    for (String key : keys) {
                        System.out.println(key + ": " + movieInfo.get(key));
                    }
                    return status;
                } else {
                    value.put(movieID, bookingCapacity);
                    movieInfo.put(movieName, value);
                    logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
                    Set<String> keys = movieInfo.keySet();
                    for (String key : keys) {
                        System.out.println(key + ": " + movieInfo.get(key));
                    }
                    return status;
                }
            }

        } catch (Exception e) {
            status = "Failed";
            serverResponse = e.toString();
            logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
            return status;
        }
    }

    /**
     * Removes a movie slot from the system.
     *
     * @param adminID   The identifier of the admin who is removing the movie slot.
     * @param movieID   The identifier of the movie.
     * @param movieName The name of the movie.
     * @return A string indicating the success or failure of the operation.
     */

    @WebMethod
    public String removeMovieSlotsVER(String adminID, String movieID, String movieName) {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getTimeInstance();
        String currentTime = dateFormat.format(date);
        String requestType = "Remove Movie Slots";
        String requestParameters = adminID + "," + movieID + "," + movieName;
        String status;
        String serverResponse;
        try {
            if (!movieInfo.containsKey(movieName)) {
                status = "Failed";
                serverResponse = "Movie does not exist in the system";
                logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
                return status;
            } else {
                HashMap<String, Integer> value = movieInfo.get(movieName);
                if (!value.containsKey(movieID)) {
                    status = "Failed";
                    serverResponse = "Movie slot does not exist in the system";
                    logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
                    return status;
                } else {
                    value.remove(movieID);
                    movieInfo.put(movieName, value);

                    value = movieInfo.get(movieName);
                    if (value.isEmpty()) {
                        movieInfo.remove(movieName);
                        System.out.println(movieInfo.size());
                        if (movieInfo.size() == 0) {
                            System.out.println("removed name too");
                        }
                    } else {
                        Set<String> keys = movieInfo.keySet();
                        for (String key : keys) {
                            System.out.println(key + ": " + movieInfo.get(key));
                        }
                    }

                    status = "Success";
                    serverResponse = "Movie slot removed";
                    logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
                    return status;
                }
            }
        } catch (Exception e) {
            status = "Failed";
            serverResponse = "Server error";
            logVER(currentTime, requestType, requestParameters, status, serverResponse, adminID);
            return status;
        }
    }

    /**
     * Gets all the movie shows associated to a theatre.
     *
     * @param movieName The identifier of the user whose tickets needs to be listed.
     * @return A string representing success or failure of the operation.
     */
    @WebMethod
    public String listMovieShowsAvailabilityVER(String movieName) {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getTimeInstance();
        String currentTime = dateFormat.format(date);
        String requestType = "List movie shows";
        String requestParameters = movieName;
        String status;
        String serverResponse;
        String result = "";
        try {
            if (movieInfo.containsKey(movieName)) {
                HashMap<String, Integer> movieShows = movieInfo.get(movieName);
                Set<String> shows = movieShows.keySet();
                for (String show : shows) {
                    result += show + " : " + movieShows.get(show) + "\n";
                }
            } else {
                result = "";
            }
            status = "Success";
            serverResponse = "Movie shows listed, if any.";
            logVER(currentTime, requestType, requestParameters, status, serverResponse, "");
        } catch (Exception e) {
            status = "Failure";
            serverResponse = "Server error";
            logVER(currentTime, requestType, requestParameters, status, serverResponse, "");
        }
        return result;
    }

    /**
     * Books a ticket for a specific movie slot.
     *
     * @param customerID      The identifier of the user who is booking the ticket.
     * @param movieID         The identifier of the movie.
     * @param movieName       The name of the movie.
     * @param numberOfTickets The number of tickets to book.
     * @return A string indicating the success or failure of the operation.
     */

    @WebMethod
    public String bookMovieTicketsVER(String customerID, String movieID, String movieName, int numberOfTickets) {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getTimeInstance();
        String currentTime = dateFormat.format(date);
        String requestType = "Book Movie Tickets";
        String requestParameters = customerID + "," + movieID + "," + movieName + "," + numberOfTickets;
        String status = "";
        String serverResponse = "";
        if (movieInfo.containsKey(movieName)) {
            HashMap<String, Integer> value = movieInfo.get(movieName);
            if (value.containsKey(movieID)) {
                int capacityLeft = value.get(movieID);
                if (numberOfTickets <= capacityLeft) {
                    value.put(movieID, capacityLeft - numberOfTickets);
                    movieInfo.put(movieName, value);

                    HashMap<String, HashMap<String, Integer>> customerInfo = new HashMap<String, HashMap<String, Integer>>();

                    if (moviesBookedInfo.containsKey(customerID)) {
                        HashMap<String, HashMap<String, Integer>> customerMovies = moviesBookedInfo.get(customerID);
                        if (customerMovies.containsKey(movieName)) {
                            HashMap<String, Integer> movieShow = customerMovies.get(movieName);
                            if (movieShow.containsKey(movieID)) {
                                movieShow.put(movieID, movieShow.get(movieID) + numberOfTickets);
                            } else {
                                movieShow.put(movieID, numberOfTickets);
                            }
                            customerMovies.put(movieName, movieShow);
                            moviesBookedInfo.put(customerID, customerMovies);
                        } else {
                            HashMap<String, Integer> movieTicketInfo = new HashMap<String, Integer>();
                            movieTicketInfo.put(movieID, numberOfTickets);
                            customerMovies.put(movieName, movieTicketInfo);
                            moviesBookedInfo.put(customerID, customerMovies);
                        }
                    } else {
                        HashMap<String, Integer> movieTicketInfo = new HashMap<String, Integer>();
                        movieTicketInfo.put(movieID, numberOfTickets);
                        customerInfo.put(movieName, movieTicketInfo);
                        moviesBookedInfo.put(customerID, customerInfo);
                    }
                    status = "Success";
                    serverResponse = "Movie Tickets Booked";
                    logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);
                    System.out.println(moviesBookedInfo.size());

                    for (Map.Entry<String, HashMap<String, HashMap<String, Integer>>> customerEntry : moviesBookedInfo.entrySet()) {
                        String customerName = customerEntry.getKey();
                        HashMap<String, HashMap<String, Integer>> customerMovies = customerEntry.getValue();

                        System.out.println("Customer: " + customerName);
                        for (Map.Entry<String, HashMap<String, Integer>> movieEntry : customerMovies.entrySet()) {
                            String movieNames = movieEntry.getKey();
                            HashMap<String, Integer> movieShows = movieEntry.getValue();

                            System.out.println("\tMovie: " + movieNames);
                            for (Map.Entry<String, Integer> showEntry : movieShows.entrySet()) {
                                String showId = showEntry.getKey();
                                int ticketsBooked = showEntry.getValue();

                                System.out.println("\t\tShow ID: " + showId + ", Tickets Booked: " + ticketsBooked);
                            }
                        }
                    }
                } else {
                    status = "Failed";
                    serverResponse = "Not enough tickets available";
                    logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);
                }
            } else {
                status = "Failed";
                serverResponse = "Movie ID not found";
                logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);
            }
            return status;
        } else {
            status = "Failed";
            serverResponse = "Movie name not found";
            logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);
            return status;
        }
    }

    /**
     * Cancels a ticket for a specific movie slot.
     *
     * @param customerID      The identifier of the user who is booking the ticket.
     * @param movieID         The identifier of the movie.
     * @param movieName       The name of the movie.
     * @param numberOfTickets The number of tickets to cancel.
     * @return A string indicating the success or failure of the operation.
     */

    @WebMethod
    public String cancelMovieTicketsVER(String customerID, String movieID, String movieName, int numberOfTickets) {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getTimeInstance();
        String currentTime = dateFormat.format(date);
        String requestType = "Cancel Movie Tickets";
        String requestParameters = customerID + "," + movieID + "," + movieName + "," + numberOfTickets;
        String status = "Success";
        String serverResponse = "Movie Tickets Cancelled";
        try {
            HashMap<String, HashMap<String, Integer>> customerBookedMovies = moviesBookedInfo.get(customerID);
            if (customerBookedMovies != null && customerBookedMovies.containsKey(movieName) && customerBookedMovies.get(movieName).containsKey(movieID)) {
                HashMap<String, Integer> movieInfo = customerBookedMovies.get(movieName);
                int numberOfBookedTickets = movieInfo.get(movieID);
                if (numberOfBookedTickets >= numberOfTickets) {
                    int remainingBookedTickets = numberOfBookedTickets - numberOfTickets;
                    movieInfo.put(movieID, remainingBookedTickets);
                    customerBookedMovies.put(movieName, movieInfo);
                    moviesBookedInfo.put(customerID, customerBookedMovies);

                    HashMap<String, Integer> movieCapacity = this.movieInfo.get(movieName);
                    int movieCapacityForID = movieCapacity.get(movieID);
                    movieCapacityForID += numberOfTickets;
                    movieCapacity.put(movieID, movieCapacityForID);
                    this.movieInfo.put(movieName, movieCapacity);

                    logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);

                    Set<String> keys = movieInfo.keySet();
                    for (String key : keys) {
                        System.out.println(key + ": " + movieInfo.get(key));
                    }
                    for (Map.Entry<String, HashMap<String, HashMap<String, Integer>>> customerEntry : moviesBookedInfo.entrySet()) {
                        String customerName = customerEntry.getKey();
                        HashMap<String, HashMap<String, Integer>> customerMovies = customerEntry.getValue();

                        System.out.println("Customer: " + customerName);
                        for (Map.Entry<String, HashMap<String, Integer>> movieEntry : customerMovies.entrySet()) {
                            String movieNames = movieEntry.getKey();
                            HashMap<String, Integer> movieShows = movieEntry.getValue();

                            System.out.println("\tMovie: " + movieNames);
                            for (Map.Entry<String, Integer> showEntry : movieShows.entrySet()) {
                                String showId = showEntry.getKey();
                                int ticketsBooked = showEntry.getValue();

                                System.out.println("\t\tShow ID: " + showId + ", Tickets Booked: " + ticketsBooked);
                            }
                        }
                    }
                } else {
                    status = "Failed";
                    serverResponse = "Not Enough Tickets Booked to Cancel";
                    logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);
                }
            } else {
                status = "Failed";
                serverResponse = "No Booking Found for the Movie";
                logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);
            }
            return status;
        } catch (Exception e) {
            status = "Failed";
            serverResponse = "Error Occured: " + e.getMessage();
            logVER(currentTime, requestType, requestParameters, status, serverResponse, customerID);
            return status;
        }
    }

    @WebMethod
    public String getBookingScheduleVER(String customerID) {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getTimeInstance();
        String currentTime = dateFormat.format(date);
        String requestType = "Get movie booking schedule";
        String requestParameters = customerID;
        String status;
        String serverResponse;
        try {
            if (moviesBookedInfo.containsKey(customerID)) {
                status = "Success";
                serverResponse = "Movie bookings listed, if any.";
                logVER(currentTime, requestType, requestParameters, status, serverResponse, "");


                HashMap<String, HashMap<String, Integer>> result = moviesBookedInfo.get(customerID);
                String resultReturned = "";
                for (Map.Entry<String, HashMap<String, Integer>> movie : result.entrySet()) {
                    String movieNames = movie.getKey();
                    HashMap<String, Integer> showDetails = movie.getValue();
                    for (Map.Entry<String, Integer> show : showDetails.entrySet()) {
                        if (show.getValue() != 0) {
                            resultReturned = resultReturned + "Movie Name: " + movieNames + " | Show ID: " + show.getKey() + " | Tickets Booked: " + show.getValue();
                            System.out.println("Movie Name: " + movieNames + " | Show ID: " + show.getKey() + " | Tickets Booked: " + show.getValue());
                        }
                    }
                    System.out.println();
                }
                return resultReturned;
            }
            return "";
        } catch (Exception e) {
            status = "Failure";
            serverResponse = "Server error";
            logVER(currentTime, requestType, requestParameters, status, serverResponse, "");
            return "";
        }
    }

    @WebMethod
    public void logVER(String currentTime, String requestType, String requestParameters, String status, String serverResponse, String userID) {
        File logFile = new File(this.getClass().getSimpleName() + ".txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.write(currentTime + " | Request type:" + requestType + " | Request Parameter: " + requestParameters + " | Status: " + status + " | Server Response: " + serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File logFolder = new File("userLogs");
        if (!logFolder.exists()) {
            logFolder.mkdir();
        }
        File userLogFile = new File(logFolder + File.separator + userID + ".txt");
        try {
            writer = new BufferedWriter(new FileWriter(userLogFile, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.write(currentTime + " | Request type:" + requestType + " | Request Parameter: " + requestParameters + " | Status: " + status + " | Server Response: " + serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
