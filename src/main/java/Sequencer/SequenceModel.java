package Sequencer;

public class SequenceModel {
    int sequenceID;
    String request;

    public SequenceModel(int sequenceID, String request){
        this.sequenceID = sequenceID;
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getSequenceID() {
        return sequenceID;
    }

    public void setSequenceID(int sequenceID) {
        this.sequenceID = sequenceID;
    }
}
