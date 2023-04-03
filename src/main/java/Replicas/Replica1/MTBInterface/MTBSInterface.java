package Replicas.Replica1.MTBInterface;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MTBSInterface {
        @WebMethod
        String addMovieSlots(String movieID, String movieName, int bookingCapacity);
        @WebMethod
        String removeMovieSlots(String movieID, String movieName);
        @WebMethod
        String listMovieShowsAvailability(String movieName);
        @WebMethod
        String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets);
        @WebMethod
        String getBookingSchedule(String customerID);
        @WebMethod
        String cancelMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets);
        @WebMethod
        String exchangeTickets(String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets);
        
}
