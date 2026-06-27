package com.consistenthashing;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ConsistentHashRing ring = new ConsistentHashRing();
        ring.addNode("Node-1", 150);
        ring.addNode("Node-2", 150);
        ring.addNode("Node-3", 150);
        ring.addNode("Node-4", 150);

        List<String> nodeNames = new ArrayList<>();
        nodeNames.add("Node-1");
        nodeNames.add("Node-2");
        nodeNames.add("Node-3");
        nodeNames.add("Node-4");

// N=3, W=2, R=2 → W+R > N
        ReplicatedStore store = new ReplicatedStore(ring, nodeNames, 3, 2, 2);

        System.out.println("=== Writing payment for swiggy ===");
        store.write("swiggy", "payment_500");

        System.out.println("\n=== Reading payment for swiggy ===");
        String result = store.read("swiggy");
        System.out.println("Result: " + result);
    }
}
