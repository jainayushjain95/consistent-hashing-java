package com.consistenthashing;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ConsistentHashRing ring = new ConsistentHashRing();
        ring.addNode("Node-1", 150);
        ring.addNode("Node-2", 150);
        ring.addNode("Node-3", 150);
        ring.addNode("Node-4", 150);

        String[] keys = {"swiggy", "zomato", "flipkart", "amazon", "meesho"};
        int rf = 3;

        System.out.println("=== Replica placement (RF=" + rf + ") ===");
        for (String key : keys) {
            List<String> replicas = ring.getReplicaNodes(key, rf);
            System.out.println(key + " → " + replicas);
        }

    }
}
