package com.consistenthashing;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        String[] merchants = new String[1000];
        for (int i = 0; i < 1000; i++) {
            merchants[i] = "merchant-" + i;
        }

        ConsistentHashRing ring = new ConsistentHashRing();
        ring.addNode("Node-1", 150);
        ring.addNode("Node-2", 150);
        ring.addNode("Node-3", 150);

        System.out.println("=== Before Node-4 joins ===");
        ring.printDistribution(merchants);

// Take snapshot before
        Map<String, String> before = ring.getRoutingSnapshot(merchants);

// Node-4 joins
        ring.addNode("Node-4", 150);

        System.out.println("\n=== After Node-4 joins ===");
        ring.printDistribution(merchants);

// Take snapshot after
        Map<String, String> after = ring.getRoutingSnapshot(merchants);

        System.out.println("\n=== Migration impact ===");
        ring.printMigrationImpact(before, after);

    }
}
