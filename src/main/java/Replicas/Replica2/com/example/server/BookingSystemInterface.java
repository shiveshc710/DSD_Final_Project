package Replicas.Replica2.com.example.server;


import javax.jws.WebMethod;
import javax.jws.WebService;
import java.net.MalformedURLException;

@WebService
public interface BookingSystemInterface {

    @WebMethod
    public String addMovieSlots(String adminID, String movieID, String movieName, int bookingCapacity) throws MalformedURLException;

    @WebMethod
    public String removeMovieSlots(String adminID, String movieID, String movieName) throws MalformedURLException;

    @WebMethod
    public String listMovieShowsAvailability(String movieName) throws MalformedURLException;

    @WebMethod
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws MalformedURLException;

    @WebMethod
    public String getBookingSchedule(String customerID) throws MalformedURLException;

    @WebMethod
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws MalformedURLException;

    @WebMethod
    public String exchangeTickets(String customerID, String movieID, String movieName, String new_movieID, String new_movieName, int numberOfTickets) throws MalformedURLException;


    @WebMethod(exclude = true)
    public void createATWObject() throws MalformedURLException;

    @WebMethod(exclude = true)
    public void createOUTObject() throws MalformedURLException;

    @WebMethod(exclude = true)
    public void createVERObject() throws MalformedURLException;
}
