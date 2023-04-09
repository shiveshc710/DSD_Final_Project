package Replicas.CrashReplica.model;

import java.util.List;

public class BookingDetails {

    private List<String> customerID;
    private int capacity;

    public BookingDetails(List<String> customerID, int capacity) {
        super();
        this.customerID = customerID;
        this.capacity = capacity;
    }

    public List<String> getCustomerID() {
        return customerID;
    }

    public void setCustomerID(List<String> customerID) {
        this.customerID = customerID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "BookingDetails [customerID=" + customerID + "]";
    }

}
