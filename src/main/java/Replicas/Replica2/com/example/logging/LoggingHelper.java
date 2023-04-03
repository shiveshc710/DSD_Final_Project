package Replicas.Replica2.com.example.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class is logging helper which is helping in logging the client/server operations.
 *
 */
public class LoggingHelper {


    /**
     * Logs the info at the client/server side logs.
     *
     * @param userID            The user who is sending/receiving the request.
     * @param requestType       The type of request received.
     * @param requestParameters The parameters of the request.
     * @param requestCompletion The status of the request.
     * @param serverResponse    The response of the server.
     */
    public static void log(String userID, String requestType, String requestParameters,
                           String requestCompletion, String serverResponse) {

        String LOG_FILE = "C:\\Users\\shive\\IdeaProjects\\DSD-3-ASG-Final\\src\\main\\java\\Replicas\\Replica2\\com\\example\\userLogs\\" + userID + ".txt";
        File file = new File(LOG_FILE);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String logLine = formatter.format(new Date()) +  " | Reqeuest Type: " + requestType + " | Request Parameters: " +
                    requestParameters + " | Status: " + requestCompletion + " | Server Response: " + serverResponse + "\n";
            fileWriter.write(logLine);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error while writing to log file: " + e.getMessage());
        }
    }
}
