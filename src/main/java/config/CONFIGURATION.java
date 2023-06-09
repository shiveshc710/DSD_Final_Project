package config;

public class CONFIGURATION {

//    Server Configuration

    public static final String HOSTNAME = "localhost";
    public static final int ATW_LISTENER = 7000;
    public static final int VER_LISTENER = 7001;
    public static final int OUT_LISTENER = 7002;
    public static final int ATW_PORT = 5000;
    public static final int VER_PORT = 5001;
    public static final int OUT_PORT = 5002;

    public static final int Main_PORT_2 = 5006;

    public static final int ATW_PORT_2 = 5003;

    public static final int OUT_PORT_2 = 5004;

    public static final int VER_PORT_2 = 5005;
    public static final String ATWSERVER = "ATW";
    public static final String OUTSERVER = "OUT";
    public static final String VERSERVER = "VER";
    public static final String AVATAR = "Avatar";
    public static final String AVENGERS = "Avengers";
    public static final String TITANIC = "Titanic";


//    Sequencer Configuration

    public static final String SEQUENCER_IP = "192.168.2.18";
    public static final int SEQUENCER_PORT = 1333;

//    Replica Manager Configuration

    public static final String RM_IP = "localhost";
    public static final int CLIENT_PORT_ADMIN = 4000;
    public static final int CLIENT_PORT_CUSTOMER = 4001;


//    Crash Replica Configuration

    public static final int CRASH_MAIN_RM = 8000;
    public static final int CRASH_MAIN_PORT_ATW = 8001;
    public static final int CRASH_MAIN_PORT_VER = 8002;
    public static final int CRASH_MAIN_PORT_OUT = 8003;
}
