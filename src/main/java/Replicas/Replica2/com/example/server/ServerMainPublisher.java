package Replicas.Replica2.com.example.server;
import config.CONFIGURATION;

import javax.xml.ws.Endpoint;

public class ServerMainPublisher {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:"+CONFIGURATION.Main_PORT_2+"/masterservice", new MasterServerImpl());
        Endpoint.publish("http://localhost:"+CONFIGURATION.ATW_PORT_2+"/ATWServer", new ATWServerImpl());
        Endpoint.publish("http://localhost:"+CONFIGURATION.OUT_PORT_2+"/OUTServer", new OUTServerImpl());
        Endpoint.publish("http://localhost:" +CONFIGURATION.VER_PORT_2+"/VERServer", new VERServerImpl());

        System.out.println("All Servers are now running.....");
    }

    public void test() {
        System.out.println("AOOOOOOOOOOOOOOOOOOO");
        System.exit(0);
    }
}
