package Replicas.Replica1.implementation;

import Replicas.Replica1.MTBInterface.MTBSInterface;
import config.CONFIGURATION;
import Replicas.Replica1.model.BookingDetails;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@WebService(endpointInterface = "Replicas.Replica1.MTBInterface.MTBSInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class OUTImplementation implements MTBSInterface {


    Map<String, Map<String, BookingDetails>> OUTdata = null;
    public Map<String, BookingDetails> userSlotData = null;
    public List<String> customerList = null;
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    FileHandler fh;
    SimpleFormatter formatter = new SimpleFormatter();
    String server = null;
    public String ver_result = "", atw_result = "";
    TimeUnit time = TimeUnit.SECONDS;

    public OUTImplementation(){
        super();

        this.server = server;

        OUTdata = new ConcurrentHashMap<>();
        userSlotData = new ConcurrentHashMap<>();
        customerList = new ArrayList<>();

        customerList.add("ATWC1234");
        customerList.add("OUTC0733");
        customerList.add("OUTC2345");
        userSlotData.put("OUTA140223", new BookingDetails(customerList, 3));

        customerList = new ArrayList<>();
        customerList.add("ATWC1234");
        customerList.add("OUTC0733");
        customerList.add("OUTC2345");
        userSlotData.put("OUTM150223", new BookingDetails(customerList, 6));

        customerList = new ArrayList<>();
        customerList.add("OUTC4567");
        userSlotData.put("OUTA180223", new BookingDetails(customerList, 10));

        userSlotData.put("OUTE210223", new BookingDetails(new ArrayList<>(), 10));
        OUTdata.put(CONFIGURATION.AVATAR, userSlotData);

        userSlotData = new ConcurrentHashMap<>();
        customerList = new ArrayList<>();
        customerList.add("OUTC1234");
        customerList.add("OUTC0733");
        customerList.add("OUTC2345");
        userSlotData.put("OUTE200223", new BookingDetails(customerList, 4));
        OUTdata.put(CONFIGURATION.TITANIC, userSlotData);
    }

    @Override
    public String addMovieSlots(String movieID, String movieName, int bookingCapacity){

        String result = "";

        if(movieID.charAt(3)== 'M' | movieID.charAt(3)== 'A' | movieID.charAt(3)== 'E' ) {
            if (movieID.startsWith(CONFIGURATION.OUTSERVER)) {
                try {
                    if (!checkDate(movieID)) {
                        result = "Slots cannot be added for Date more than a week or for previous date.";
                    } else {
                        if (!(movieID.startsWith(CONFIGURATION.OUTSERVER))) {
                            result = "Invalid movieID!!!";
                            writeLog("Unable to add slot : Invalid movieID");
                        } else if (!OUTdata.containsKey(movieName)) {
                            Map<String, BookingDetails> tmp = new ConcurrentHashMap<>();
                            tmp.put(movieID, new BookingDetails(new ArrayList<>(), bookingCapacity));
                            OUTdata.put(movieName, tmp);
                            result = "Movie Slot added for " + movieName;
                            writeLog("Movie slot "+ movieID+" added for : "+movieName);
                        } else {
                            if (OUTdata.get(movieName).containsKey(movieID)) {
                                result = "Movie Slot already exist for movieID!!!";
                                writeLog("Movie Slot already exist for "+movieID);
                            } else {
                                Map<String, BookingDetails> tmp = OUTdata.get(movieName);
                                tmp.put(movieID, new BookingDetails(new ArrayList<>(), bookingCapacity));
                                OUTdata.put(movieName, tmp);
                                result = "Movie Slot added for " + movieName;
                                writeLog("Movie slot "+ movieID+" added for : "+movieName);

                            }
                        }
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                result = "Unable to add slots for other servers.";
                writeLog("Unable to add slots for other servers.");

            }
        }else {
            result = "Invalid MovieID";
            writeLog("Unable to add slot : Invalid movieID");

        }

        return result;
    }

    @Override
    public String removeMovieSlots(String movieID, String movieName){
        String result = "";
        boolean transfer = false;
        String log = "";
        int day = Integer.parseInt(movieID.substring(4, 6));
        int month = Integer.parseInt(movieID.substring(6, 8));


        if (!OUTdata.containsKey(movieName)) {
            result = "No movie slots is available for this type!!!";
            log = "No movie slots is available.";
        } else {
            Map<String, BookingDetails> tmp = OUTdata.get(movieName);
            if (tmp.containsKey(movieID)) {
                if (tmp.get(movieID).getCustomerID().size() == 0) {
                    result = "Booking found, no shows booked, cancelling it!!";
                    OUTdata.get(movieName).remove(movieID);
                    log = "Movie show found, no shows booked, deleting it without transfer";
                } else {
                    result = "Movie slot found, customers have booked this shows, unable to find other movie shows for the customers.";
                    for (Map.Entry<String, BookingDetails> bookingData : tmp.entrySet()) {
                        if ((Integer.parseInt(bookingData.getKey().substring(4, 8)) > month)
                                || (Integer.parseInt(bookingData.getKey().substring(4, 6)) > day)) {
                            if ((bookingData.getValue().getCapacity() - bookingData.getValue().getCustomerID().size()) >= tmp
                                    .get(movieID).getCustomerID().size()) {
                                transfer = true;
                                List<String> customers = new ArrayList<>();
                                customers.addAll(bookingData.getValue().getCustomerID());
                                customers.addAll(tmp.get(movieID).getCustomerID());

                                OUTdata.get(movieName).get(bookingData.getKey()).setCustomerID(customers);
                                OUTdata.get(movieName).remove(movieID);
                                result = "Slot is deleted and booking is now transferred to show "+bookingData.getKey();
                                break;
                            }
                        }
                    }
                    if (transfer) {
                        log = "Slot deleted, transferred booking to new show.";
                    } else {
                        log = "Booking found for this show, Unable to assign to other shows.";

                        String customers = "";
                        for (String temp : OUTdata.get(movieName).get(movieID).getCustomerID()) {
                            customers += temp + ",";
                        }


                        String finalCustomers = customers;

                        ver_result = udpThread("removeSlots:" + finalCustomers + " " + movieName, CONFIGURATION.VER_LISTENER);

                        if (ver_result.contains("success")){
                            OUTdata.get(movieName).remove(movieID);
                            result = "Slot is deleted and booking is now transferred to Verdun show: " + ver_result.split(" ")[1];
                            log = "Slot is deleted and booking is now transferred to Verdun show: " + ver_result.split(" ")[1];

                        }else {
                            atw_result = udpThread("removeSlots:" + finalCustomers + " " + movieName, CONFIGURATION.ATW_LISTENER);
                            if (atw_result.contains("success")){
                                OUTdata.get(movieName).remove(movieID);
                                result = "Slot is deleted and booking is now transferred to Atwater show: " + atw_result.split(" ")[1];
                                log = "Slot is deleted and booking is now transferred to Atwater show: " + atw_result.split(" ")[1];

                            }
                        }
                    }
                }

            } else {
                result = "No shows are available for "+movieName+"!!!";
                OUTdata.get(movieName).remove(movieID);
                log = "No shows are available for "+movieName+"!!!";
            }
        }
        writeLog(log);

        return result;
    }

    @Override
    public String listMovieShowsAvailability(String movieName){
        String result = "Outremont,";
        String log = "";
        if (OUTdata.containsKey(movieName)) {
            for (Map.Entry<String, BookingDetails> data : OUTdata.get(movieName).entrySet()) {
                if ((data.getValue().getCapacity() - data.getValue().getCustomerID().size() >= 0))
                    result += data.getKey() + " : "
                            + (data.getValue().getCapacity() - data.getValue().getCustomerID().size() + ",");

            }
        }

        if (result.contains("OUTA") | result.contains("OUTM")| result.contains("OUTE"))
            result += "\n";
        else
            result += "No bookings available,";

        new Thread() {
            public void run() {
                ver_result = "\nVerdun," + udpThread("showsList:" + movieName, CONFIGURATION.VER_LISTENER);
            }
        }.start();

        new Thread() {
            public void run() {
                atw_result = "\nAtwater," + udpThread("showsList:" + movieName, CONFIGURATION.ATW_LISTENER);
            }
        }.start();

        try {
            time.sleep(2L);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String final_result = result + ver_result + atw_result;
        log = final_result.trim().isEmpty() ? "No result Found!!" : "Data found from all servers";
        writeLog(log);
        return (final_result.trim().isEmpty() ? "No result Found!!" : final_result);
    }

    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets){
        String result = "";
        String status = "failed";
        boolean canBook = true;
        int emptySlots = 0;
        String ID = "";
        String log = "";

        List<String> slots = Arrays.asList(retriveAvailableSlots(movieName).split(","));


        if (movieID.startsWith(CONFIGURATION.OUTSERVER)) {
            if(customerID.startsWith(CONFIGURATION.VERSERVER) | customerID.startsWith(CONFIGURATION.ATWSERVER)) {
                boolean isAvailable = true;
                String date = movieID.substring(3, 10);
                List<String> local_slots = Arrays.asList(retriveCustomerBookingsFromServers(customerID,"All").split(","));

                for (int i = 0; i < local_slots.size(); i++) {
                    if (slots.get(i).substring(3, 10).equals(date))
                        isAvailable = false;
                }
                if(!isAvailable)
                {
                    result = "You cannot book show with same timing more than once";
                    log = "Unable to book show with same timing more than once";

                }else {
                    for (int i = 0; i < slots.size(); i++) {
                        ID = slots.get(i).split(":")[0].trim();
                        emptySlots = Integer.parseInt(slots.get(i).split(":")[1].trim());
                        if (emptySlots >= numberOfTickets && ID.contains(movieID)) {
                            int numberOfSeats = (OUTdata.get(movieName).get(movieID).getCapacity()
                                    - OUTdata.get(movieName).get(movieID).getCustomerID().size());
                            if (numberOfSeats >= numberOfTickets) {
                                BookingDetails bookings = OUTdata.get(movieName).get(movieID);
                                for (int j = 0; j < numberOfTickets; j++) {
                                    bookings.getCustomerID().add(customerID);
                                }
                                result = "Tickets booked for " + movieName + " for " + movieID.substring(4, 6)
                                        + "/" + movieID.substring(6, 8);
                                log = result;
                                break;
                            } else {
                                result = "Not enough seats available for this show!!!";
                                log = "Not enough seats available for this show!!!";
                                status = "failed";

                            }
                        }
                    }
                }
            }else {
                if (OUTdata.containsKey(movieName) && OUTdata.get(movieName).containsKey(movieID)) {
                    for (int i = 0; i < slots.size(); i++) {
                        ID = slots.get(i).split(":")[0].trim();
                        emptySlots = Integer.parseInt(slots.get(i).split(":")[1].trim());
                        if (emptySlots >= numberOfTickets && ID.contains(movieID)) {
                            int numberOfSeats = (OUTdata.get(movieName).get(movieID).getCapacity()
                                    - OUTdata.get(movieName).get(movieID).getCustomerID().size());
                            if (numberOfSeats >= numberOfTickets) {
                                BookingDetails bookings = OUTdata.get(movieName).get(movieID);
                                for (int j = 0; j < numberOfTickets; j++) {
                                    bookings.getCustomerID().add(customerID);
                                }
                                result = "Tickets booked for " + movieName + " for " + movieID.substring(4, 6)
                                        + "/" + movieID.substring(6, 8);
                                log = result;
                                break;
                            } else {
                                result = "Not enough seats available for this show!!!";
                                log = "Not enough seats available for this show!!!";
                                status = "failed";

                            }
                        }
                    }
                } else {
                    result = "No show available for " + movieName + " and with this movieID!!!";
                    log = "Unable to book show " + movieID + " for "+ movieName;
                    status = "failed";
                }
            }
        } else {
            int totalBookings = getTotalBookings(customerID);

            if (totalBookings <= 2) {
                if (movieID.contains(CONFIGURATION.ATWSERVER)) {
                    result = udpThread("bookTickets:" + customerID + " " + movieID + " " + movieName + " " + numberOfTickets, CONFIGURATION.ATW_LISTENER);
                    status = (result.startsWith("No") ? "failed" : "success");
                } else if (movieID.contains(CONFIGURATION.VERSERVER)) {
                    result = udpThread("bookTickets:" + customerID + " " + movieID + " " + movieName + " " + numberOfTickets, CONFIGURATION.VER_LISTENER);
                    status = (result.startsWith("No") ? "failed" : "success");
                } else {
                    result = "Invalid movieID";
                    log = "Failed : Invalid movieID";

                }

            } else {
                result = "You cannot book more than 3 tickets for Different Locations";
                log = "Failed : Unable to book more than 3 tickets for Different Locations";
            }
        }
        writeLog(log);

        return result;
    }
    @Override
    public String getBookingSchedule(String customerID){
        String key = null;
        String log = "";
        String result = "----------Outremont----------,";

        for (Map.Entry<String, Map<String, BookingDetails>> set : OUTdata.entrySet()) {
            key = set.getKey();
            boolean isAppended = false;
            int count = 0;
            for (Map.Entry<String, BookingDetails> data : set.getValue().entrySet()) {

                for (int i = 0; i < data.getValue().getCustomerID().size(); i++) {
                    if (data.getValue().getCustomerID().get(i).equals(customerID))
                        count++;
                }
                if ((data.getValue().getCustomerID()).contains(customerID)) {
                    if (!isAppended) {
                        result += key + " , " + data.getKey() + " : "+ count;
                        isAppended = true;
                        count = 0;
                    } else
                        result += ", " + data.getKey()+ " : "+ count + " ";
                    count = 0;
                }

            }
            count = 0;
            result += ",";

        }

        if (result.contains("OUTA") | result.contains("OUTM")| result.contains("OUTE"))
            result += "\n";
        else
            result += "No bookings available,";

        new Thread(){
            @Override
            public void run() {
                atw_result = udpThread("showsSchedule:" + customerID,CONFIGURATION.ATW_LISTENER);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                ver_result = udpThread("showsSchedule:" + customerID,CONFIGURATION.VER_LISTENER);
            }
        }.start();

        try {
            time.sleep(2L);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String final_result = result + ver_result + atw_result;

        log = final_result.trim().isEmpty() ? "No result Found!!" : "Bookings found from all servers for user : " + customerID;
        writeLog(log);


        return (result.isEmpty() ? "" : final_result);
    }

    public String getBookingScheduleForServer(String customerID){
        String key = null;
        String log = "";
        String result = "----------Outremont----------,";

        for (Map.Entry<String, Map<String, BookingDetails>> set : OUTdata.entrySet()) {
            key = set.getKey();
            boolean isAppended = false;
            int count = 0;
            for (Map.Entry<String, BookingDetails> data : set.getValue().entrySet()) {

                for (int i = 0; i < data.getValue().getCustomerID().size(); i++) {
                    if (data.getValue().getCustomerID().get(i).equals(customerID))
                        count++;
                }
                if ((data.getValue().getCustomerID()).contains(customerID)) {
                    if (!isAppended) {
                        result += key + " , " + data.getKey() + " : "+ count;
                        isAppended = true;
                        count = 0;
                    } else
                        result += ", " + data.getKey()+ " : "+ count + " ";
                    count = 0;
                }

            }
            count = 0;
            result += ",";

        }

        if (result.contains("OUTA") | result.contains("OUTM")| result.contains("OUTE"))
            result += "\n";
        else
            result += "No bookings available,";

        log = result.trim().isEmpty() ? "No result Found!!" : "Bookings found for Outremont server for user "+ customerID;
        writeLog(log);

        return result;
    }
    @Override
    public String cancelMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets){
        String status = "failed";
        String log = "No bookings found";
        String result = "No bookings found!!!";

        String server = movieId.substring(0,3);

        String serverData = customerID+" "+movieId+" "+movieName+" "+numberOfTickets;

        switch (server) {
            case "OUT":
                for (Map.Entry<String, Map<String, BookingDetails>> set : OUTdata.entrySet()) {
                    for (Map.Entry<String, BookingDetails> data : set.getValue().entrySet()) {
                        if (data.getValue().getCustomerID().stream().filter(customerID::equals).count() >= numberOfTickets) {
                            if (data.getValue().getCustomerID().contains(customerID) && data.getKey().equals(movieId)) {
                                System.out.println("Bookings Found");
                                for (int i = 0; i < numberOfTickets; i++) {
                                    data.getValue().getCustomerID().remove(customerID);
                                }
                                log = "Ticket(s) canceled successfully.";
                                status = "success";
                                result = "Ticket(s) canceled!!!";
                                break;
                            }
                        }
                    }
                }
                break;
            case "ATW":
                new Thread() {
                    public void run() {
                        atw_result = udpThread("cancelTickets:" + serverData, CONFIGURATION.ATW_LISTENER);
                    }
                }.start();
                result = atw_result;
                break;
            case "VER":
                result = "";
                new Thread() {
                    public void run() {
                        ver_result = udpThread("cancelTickets:" + serverData, CONFIGURATION.VER_LISTENER);
                    }
                }.start();
                result = ver_result;
                break;
        }
        writeLog(log);

        return result;
    }

    @Override
    public String exchangeTickets(String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets) {
        return null;
    }

    private int getTotalBookings(String customerID){
        List<String> slots;
        slots = Arrays.asList(retriveCustomerBookingsFromServers(customerID, CONFIGURATION.ATWSERVER).split(","));
        int totalBookings = 0;
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).contains(CONFIGURATION.ATWSERVER)) {
                totalBookings += Integer.parseInt(slots.get(i).split(":")[1].trim());
            }
        }

        slots = Arrays.asList(retriveCustomerBookingsFromServers(customerID, CONFIGURATION.VERSERVER).split(","));
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).contains(CONFIGURATION.VERSERVER)) {
                totalBookings += Integer.parseInt(slots.get(i).split(":")[1].trim());
            }
        }
        return totalBookings;
    }
    public String retriveAvailableSlots(String movieName){
        String key = null;
        String result = "";

        if (OUTdata.containsKey(movieName)) {
            for (Map.Entry<String, BookingDetails> data : OUTdata.get(movieName).entrySet()) {
                if ((data.getValue().getCapacity() - data.getValue().getCustomerID().size() >= 0))
                    result += data.getKey() + " : "
                            + (data.getValue().getCapacity() - data.getValue().getCustomerID().size() + ",");

            }
        }


        return (result.isEmpty() ? "" : result);
    }
    public String retriveCustomerBookingsFromServers(String customerID, String server){
        String key = null;
        String result = "";

        if (server.equals(CONFIGURATION.ATWSERVER)) {
            new Thread() {
                @Override
                public void run() {
                    atw_result = udpThread("showsSchedule:" + customerID, CONFIGURATION.ATW_LISTENER);
                    atw_result = atw_result.replace("----------Atwater----------", "Atwater");

                }
            }.start();
            return (atw_result.isEmpty() ? "" : atw_result);
        }else if (server.equals(CONFIGURATION.VERSERVER)) {
            new Thread() {
                @Override
                public void run() {
                    ver_result = udpThread("showsSchedule:" + customerID, CONFIGURATION.VER_LISTENER);
                    ver_result = ver_result.replace("----------Verdun----------", "Verdun");
                }
            }.start();
            return (ver_result.isEmpty() ? "" : ver_result);

        }else {
            new Thread() {
                @Override
                public void run() {
                    atw_result = udpThread("showsSchedule:" + customerID, CONFIGURATION.ATW_LISTENER);
                    atw_result = atw_result.replace("----------Atwater----------", "Atwater");

                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    ver_result = udpThread("showsSchedule:" + customerID, CONFIGURATION.VER_LISTENER);
                    ver_result = ver_result.replace("----------Verdun----------", "Verdun");
                }
            }.start();
        }

        try {
            time.sleep(2L);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String final_result = result + ver_result + atw_result;

        System.out.println("Called : "+ atw_result);

        return (result.isEmpty() ? "" : final_result);
    }

    public String listAvailableForServer(String movieName){
        String result = "";
        if (OUTdata.containsKey(movieName)) {
            for (Map.Entry<String, BookingDetails> data : OUTdata.get(movieName.trim()).entrySet()) {
                if ((data.getValue().getCapacity() - data.getValue().getCustomerID().size() > 0)) {
                    result += data.getKey() + " : "
                            + (data.getValue().getCapacity() - data.getValue().getCustomerID().size() + ",");
                }
            }
        }
        return result;
    }


    public String udpThread(String data, int port) {
        String result = "";
        try (DatagramSocket aSocket = new DatagramSocket()) {
            DatagramPacket request = new DatagramPacket(data.getBytes(), data.getBytes().length,
                    InetAddress.getByName("localhost"), port);
            aSocket.send(request);
            writeLog("UDP request sent from Outremont to port "+port);


            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            aSocket.close();
            result = new String(reply.getData()).trim();
            writeLog("UDP response received from port "+port);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean checkDate(String movieID) throws ParseException {

        String date_temp = movieID.substring(4);
        Date date = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);


        String tempCurrentDate =  new SimpleDateFormat("ddMMyy").format(new Date());
        Date currentDate = new SimpleDateFormat("ddMMyy").parse(tempCurrentDate);;
        Date nextWeek = null;
        String temp = new SimpleDateFormat("ddMMyy").format(c.getTime());

        nextWeek = new SimpleDateFormat("ddMMyy").parse(temp);
        date = new SimpleDateFormat("ddMMyy").parse(date_temp);

        if(date.equals(currentDate)){
            return true;
        }
        else if(date.before(currentDate)){
            return false;
        }
        else if(date.after(nextWeek)){
            return false;
        }

        return true;

    }

    public void writeLog(String message){
        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("src/logs/OUTLog.log", 0,1,true);

            fh.setFormatter(formatter);

            logger.addHandler(fh);

            logger.setUseParentHandlers(false);

            // the following statement is used to log any messages
            logger.info("Log from Outremont : "+ message);

            fh.close();

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            logger.info("File Handler Exception!");
            fh.close();
        }
    }

    public String ServerexchangeTicketsCheck(String customerID,String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets) {

        if(OUTdata.containsKey(old_movieName))
        {
            if (OUTdata.get(old_movieName).containsKey(movieID))
            {
                if (OUTdata.get(old_movieName).get(movieID).getCustomerID().contains(customerID))
                {

                    List<String> s = OUTdata.get(old_movieName).get(movieID).getCustomerID();
                    int count = 0;
                    for (String id : s){
                        if(id.equals(customerID))
                            count++;
                    }

                    if (count >= numberOfTickets)
                        return "done";

                }
            }
        }

        return "not done";
    }

    public String ServerexchangeTicketsCheckNewMovie(String customerID,String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets) {
        if(OUTdata.containsKey(new_movieName))
        {
            if (OUTdata.get(new_movieName).containsKey(new_movieID))
            {
                int numberOfSeats = (OUTdata.get(new_movieName).get(movieID).getCapacity()
                        - OUTdata.get(new_movieName).get(movieID).getCustomerID().size());

                System.out.println("Reached inside");
                if (numberOfSeats >= numberOfTickets)
                    return "done";
            }
        }
        System.out.println("Reached outside");

        return "not done";
    }
}