package config;

public class CONFIGURATION {


//    Ports

    //    Replica 1 Ports
    public static final int RM1_PORT = 5007;
    public static final int ATW_PORT = 5000;
    public static final int VER_PORT = 5001;
    public static final int OUT_PORT = 5002;
    public static final int ATW_LISTENER = 7001;
    public static final int VER_LISTENER = 7002;
    public static final int OUT_LISTENER = 7003;

    //    Crash Replica ports
    public static final int CRASH_ATW_LISTENER = 7004;
    public static final int CRASH_VER_LISTENER = 7005;
    public static final int CRASH_OUT_LISTENER = 7006;
    public static final int CRASH_MAIN_RM = 8005;
    public static final int CRASH_MAIN_PORT_ATW = 8001;
    public static final int CRASH_MAIN_PORT_VER = 8002;
    public static final int CRASH_MAIN_PORT_OUT = 8003;

    //    Replica 2 ports
    public static final int RM2_PORT = 6000;
    public static final int SERVER_MAIN_PUBLISHER_PORT = 5006;

    public static final int ATW_PORT_2 = 5003;

    public static final int OUT_PORT_2 = 5004;

    public static final int VER_PORT_2 = 5005;

//    Replica 3 Ports

    public static final int RM3_PORT = 7000;

    //    Sequencer
    public static final int SEQUENCER_PORT = 1333;
    public static final int SEQUENCER_BACKUP_PORT = 8006;


    //    Client Ports
    public static final int CLIENT_PORT_ADMIN = 4000;
    public static final int CLIENT_PORT_CUSTOMER = 4001;

    //    FE Ports
    public static final int FE_PORT = 9000;

    public static final int FE_RECEIVE_PORT = 9001;


//    Strings

    public static final String ATWSERVER = "ATW";
    public static final String OUTSERVER = "OUT";
    public static final String VERSERVER = "VER";

    public static final String AVATAR = "Avatar";
    public static final String AVENGERS = "Avengers";
    public static final String TITANIC = "Titanic";


    //    IP Configuration
    public static final String HOSTNAME = "localhost";
//    public static final String SEQUENCER_IP = "10.0.0.9";
//    public static final String RM1_IP = "10.0.0.179";
//    public static final String RM2_IP = "10.0.0.179";
//    public static final String RM3_IP = "10.0.0.179";
//    public static final String CRASH_RM_IP = "10.0.0.179";
//    public static final String FE_IP = "10.0.0.9";

    public static final String SEQUENCER_IP = "localhost";
    public static final String RM1_IP = "localhost";
    public static final String RM2_IP = "localhost";
    public static final String RM3_IP = "localhost";
    public static final String CRASH_RM_IP = "localhost";
    public static final String FE_IP = "localhost";


//    CONSTANTS

    public static final int TIMEOUT = 5000;
}
