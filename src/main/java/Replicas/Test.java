package Replicas;

import Replicas.CrashReplica.ReplicaManager.ReplicaManager;
import config.CONFIGURATION;

public class Test {
    public static void main(String[] args) throws Exception {
        int numberOfReplicas = 3;
        for (int i = 0; i < numberOfReplicas; i++) {
            ReplicaManager replicaManager = new ReplicaManager(CONFIGURATION.CRASH_MAIN_RM);
            new Thread(() -> {
                try {
                    replicaManager.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

}
