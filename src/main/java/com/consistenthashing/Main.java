package com.consistenthashing;

public class Main {
    public static void main(String[] args) {
        ConsistentHashRing ring = new ConsistentHashRing();
        String[] merchants = new String[1000];
        for (int i = 0; i < 1000; i++) {
            merchants[i] = "merchant-" + i;
        }

        System.out.println("=== 1 vnode per node ===");
        ConsistentHashRing ring1 = new ConsistentHashRing();
        ring1.addNode("Node-1", 1);
        ring1.addNode("Node-2", 1);
        ring1.addNode("Node-3", 1);
        ring1.printDistribution(merchants);

        System.out.println("\n=== 150 vnodes per node ===");
        ConsistentHashRing ring150 = new ConsistentHashRing();
        ring150.addNode("Node-1", 150);
        ring150.addNode("Node-2", 150);
        ring150.addNode("Node-3", 150);
        ring150.printDistribution(merchants);

    }
}
