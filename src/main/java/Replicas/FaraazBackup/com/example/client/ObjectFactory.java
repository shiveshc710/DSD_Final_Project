
package Replicas.FaraazBackup.com.example.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the Replicas.FaraazBackup.com.example.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetBookingScheduleOUT_QNAME = new QName("http://example.com/out", "getBookingScheduleOUT");
    private final static QName _CancelMovieTicketsOUTResponse_QNAME = new QName("http://example.com/out", "cancelMovieTicketsOUTResponse");
    private final static QName _ListMovieShowsAvailabilityOUT_QNAME = new QName("http://example.com/out", "listMovieShowsAvailabilityOUT");
    private final static QName _RemoveMovieSlotsOUT_QNAME = new QName("http://example.com/out", "removeMovieSlotsOUT");
    private final static QName _ListMovieShowsAvailabilityOUTResponse_QNAME = new QName("http://example.com/out", "listMovieShowsAvailabilityOUTResponse");
    private final static QName _AddMovieSlotsOUT_QNAME = new QName("http://example.com/out", "addMovieSlotsOUT");
    private final static QName _GetBookingScheduleOUTResponse_QNAME = new QName("http://example.com/out", "getBookingScheduleOUTResponse");
    private final static QName _LogOUT_QNAME = new QName("http://example.com/out", "logOUT");
    private final static QName _AddMovieSlotsOUTResponse_QNAME = new QName("http://example.com/out", "addMovieSlotsOUTResponse");
    private final static QName _CancelMovieTicketsOUT_QNAME = new QName("http://example.com/out", "cancelMovieTicketsOUT");
    private final static QName _LogOUTResponse_QNAME = new QName("http://example.com/out", "logOUTResponse");
    private final static QName _BookMovieTicketsOUT_QNAME = new QName("http://example.com/out", "bookMovieTicketsOUT");
    private final static QName _RemoveMovieSlotsOUTResponse_QNAME = new QName("http://example.com/out", "removeMovieSlotsOUTResponse");
    private final static QName _BookMovieTicketsOUTResponse_QNAME = new QName("http://example.com/out", "bookMovieTicketsOUTResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: Replicas.FaraazBackup.com.example.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CancelMovieTicketsOUTResponse }
     * 
     */
    public CancelMovieTicketsOUTResponse createCancelMovieTicketsOUTResponse() {
        return new CancelMovieTicketsOUTResponse();
    }

    /**
     * Create an instance of {@link GetBookingScheduleOUT }
     * 
     */
    public GetBookingScheduleOUT createGetBookingScheduleOUT() {
        return new GetBookingScheduleOUT();
    }

    /**
     * Create an instance of {@link RemoveMovieSlotsOUT }
     * 
     */
    public RemoveMovieSlotsOUT createRemoveMovieSlotsOUT() {
        return new RemoveMovieSlotsOUT();
    }

    /**
     * Create an instance of {@link ListMovieShowsAvailabilityOUT }
     * 
     */
    public ListMovieShowsAvailabilityOUT createListMovieShowsAvailabilityOUT() {
        return new ListMovieShowsAvailabilityOUT();
    }

    /**
     * Create an instance of {@link ListMovieShowsAvailabilityOUTResponse }
     * 
     */
    public ListMovieShowsAvailabilityOUTResponse createListMovieShowsAvailabilityOUTResponse() {
        return new ListMovieShowsAvailabilityOUTResponse();
    }

    /**
     * Create an instance of {@link AddMovieSlotsOUT }
     * 
     */
    public AddMovieSlotsOUT createAddMovieSlotsOUT() {
        return new AddMovieSlotsOUT();
    }

    /**
     * Create an instance of {@link GetBookingScheduleOUTResponse }
     * 
     */
    public GetBookingScheduleOUTResponse createGetBookingScheduleOUTResponse() {
        return new GetBookingScheduleOUTResponse();
    }

    /**
     * Create an instance of {@link AddMovieSlotsOUTResponse }
     * 
     */
    public AddMovieSlotsOUTResponse createAddMovieSlotsOUTResponse() {
        return new AddMovieSlotsOUTResponse();
    }

    /**
     * Create an instance of {@link CancelMovieTicketsOUT }
     * 
     */
    public CancelMovieTicketsOUT createCancelMovieTicketsOUT() {
        return new CancelMovieTicketsOUT();
    }

    /**
     * Create an instance of {@link LogOUT }
     * 
     */
    public LogOUT createLogOUT() {
        return new LogOUT();
    }

    /**
     * Create an instance of {@link BookMovieTicketsOUT }
     * 
     */
    public BookMovieTicketsOUT createBookMovieTicketsOUT() {
        return new BookMovieTicketsOUT();
    }

    /**
     * Create an instance of {@link LogOUTResponse }
     * 
     */
    public LogOUTResponse createLogOUTResponse() {
        return new LogOUTResponse();
    }

    /**
     * Create an instance of {@link RemoveMovieSlotsOUTResponse }
     * 
     */
    public RemoveMovieSlotsOUTResponse createRemoveMovieSlotsOUTResponse() {
        return new RemoveMovieSlotsOUTResponse();
    }

    /**
     * Create an instance of {@link BookMovieTicketsOUTResponse }
     * 
     */
    public BookMovieTicketsOUTResponse createBookMovieTicketsOUTResponse() {
        return new BookMovieTicketsOUTResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBookingScheduleOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "getBookingScheduleOUT")
    public JAXBElement<GetBookingScheduleOUT> createGetBookingScheduleOUT(GetBookingScheduleOUT value) {
        return new JAXBElement<GetBookingScheduleOUT>(_GetBookingScheduleOUT_QNAME, GetBookingScheduleOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelMovieTicketsOUTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "cancelMovieTicketsOUTResponse")
    public JAXBElement<CancelMovieTicketsOUTResponse> createCancelMovieTicketsOUTResponse(CancelMovieTicketsOUTResponse value) {
        return new JAXBElement<CancelMovieTicketsOUTResponse>(_CancelMovieTicketsOUTResponse_QNAME, CancelMovieTicketsOUTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListMovieShowsAvailabilityOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "listMovieShowsAvailabilityOUT")
    public JAXBElement<ListMovieShowsAvailabilityOUT> createListMovieShowsAvailabilityOUT(ListMovieShowsAvailabilityOUT value) {
        return new JAXBElement<ListMovieShowsAvailabilityOUT>(_ListMovieShowsAvailabilityOUT_QNAME, ListMovieShowsAvailabilityOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveMovieSlotsOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "removeMovieSlotsOUT")
    public JAXBElement<RemoveMovieSlotsOUT> createRemoveMovieSlotsOUT(RemoveMovieSlotsOUT value) {
        return new JAXBElement<RemoveMovieSlotsOUT>(_RemoveMovieSlotsOUT_QNAME, RemoveMovieSlotsOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListMovieShowsAvailabilityOUTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "listMovieShowsAvailabilityOUTResponse")
    public JAXBElement<ListMovieShowsAvailabilityOUTResponse> createListMovieShowsAvailabilityOUTResponse(ListMovieShowsAvailabilityOUTResponse value) {
        return new JAXBElement<ListMovieShowsAvailabilityOUTResponse>(_ListMovieShowsAvailabilityOUTResponse_QNAME, ListMovieShowsAvailabilityOUTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddMovieSlotsOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "addMovieSlotsOUT")
    public JAXBElement<AddMovieSlotsOUT> createAddMovieSlotsOUT(AddMovieSlotsOUT value) {
        return new JAXBElement<AddMovieSlotsOUT>(_AddMovieSlotsOUT_QNAME, AddMovieSlotsOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBookingScheduleOUTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "getBookingScheduleOUTResponse")
    public JAXBElement<GetBookingScheduleOUTResponse> createGetBookingScheduleOUTResponse(GetBookingScheduleOUTResponse value) {
        return new JAXBElement<GetBookingScheduleOUTResponse>(_GetBookingScheduleOUTResponse_QNAME, GetBookingScheduleOUTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "logOUT")
    public JAXBElement<LogOUT> createLogOUT(LogOUT value) {
        return new JAXBElement<LogOUT>(_LogOUT_QNAME, LogOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddMovieSlotsOUTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "addMovieSlotsOUTResponse")
    public JAXBElement<AddMovieSlotsOUTResponse> createAddMovieSlotsOUTResponse(AddMovieSlotsOUTResponse value) {
        return new JAXBElement<AddMovieSlotsOUTResponse>(_AddMovieSlotsOUTResponse_QNAME, AddMovieSlotsOUTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelMovieTicketsOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "cancelMovieTicketsOUT")
    public JAXBElement<CancelMovieTicketsOUT> createCancelMovieTicketsOUT(CancelMovieTicketsOUT value) {
        return new JAXBElement<CancelMovieTicketsOUT>(_CancelMovieTicketsOUT_QNAME, CancelMovieTicketsOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogOUTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "logOUTResponse")
    public JAXBElement<LogOUTResponse> createLogOUTResponse(LogOUTResponse value) {
        return new JAXBElement<LogOUTResponse>(_LogOUTResponse_QNAME, LogOUTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BookMovieTicketsOUT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "bookMovieTicketsOUT")
    public JAXBElement<BookMovieTicketsOUT> createBookMovieTicketsOUT(BookMovieTicketsOUT value) {
        return new JAXBElement<BookMovieTicketsOUT>(_BookMovieTicketsOUT_QNAME, BookMovieTicketsOUT.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveMovieSlotsOUTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "removeMovieSlotsOUTResponse")
    public JAXBElement<RemoveMovieSlotsOUTResponse> createRemoveMovieSlotsOUTResponse(RemoveMovieSlotsOUTResponse value) {
        return new JAXBElement<RemoveMovieSlotsOUTResponse>(_RemoveMovieSlotsOUTResponse_QNAME, RemoveMovieSlotsOUTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BookMovieTicketsOUTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/out", name = "bookMovieTicketsOUTResponse")
    public JAXBElement<BookMovieTicketsOUTResponse> createBookMovieTicketsOUTResponse(BookMovieTicketsOUTResponse value) {
        return new JAXBElement<BookMovieTicketsOUTResponse>(_BookMovieTicketsOUTResponse_QNAME, BookMovieTicketsOUTResponse.class, null, value);
    }

}
