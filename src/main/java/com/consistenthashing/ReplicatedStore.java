package com.consistenthashing;
import java.util.*;

public class ReplicatedStore {
    private final ConsistentHashRing ring;
    private final Map<String, SimulatedNode> nodes; // nodeName → SimulatedNode
    private final int rf;  // replication factor
    private final int w;   // write quorum
    private final int r;   // read quorum

    public ReplicatedStore(ConsistentHashRing ring,
                           List<String> nodeNames,
                           int rf, int w, int r) {
        this.ring = ring;
        nodes = new HashMap<>();
        this.rf = rf;
        this.w = w;
        this.r = r;
        for(String node : nodeNames) {
            nodes.put(node, new SimulatedNode(node));
        }
    }

    public void write(String key, String value) {
        long timestamp = System.currentTimeMillis();
        List<String> replicas = ring.getReplicaNodes(key, rf);
        int acks = 0;
        for (String nodeName : replicas) {
            if (nodeName.equals(replicas.get(replicas.size() - 1))) {
                System.out.println("  [SLOW] " + nodeName + " did not receive write yet");
                continue;
            }
            nodes.get(nodeName).write(key, value, timestamp);
            acks++;
            System.out.println("  [ACK]  " + nodeName + " wrote key=" + key);
        }
        if (acks >= w) {
            System.out.println("  Write quorum met (" + acks + "/" + w + ") — success confirmed to client");
        }
    }

    public String read(String key) {
        List<String> replicas = ring.getReplicaNodes(key, rf);
        String freshestValue = null;
        long freshestTimestamp = -1;
        int reads = 0;

        for (String nodeName : replicas) {
            if (reads >= r) {
                break;
            }
            SimulatedNode node = nodes.get(nodeName);
            String value = node.read(key);
            Long timestamp = node.getTimestamp(key);
            System.out.println("  [READ] " + nodeName +
                    " → value=" + value + ", timestamp=" + timestamp);
            if (timestamp != null && timestamp > freshestTimestamp) {
                freshestValue = value;
                freshestTimestamp = timestamp;
            }
            reads++;
        }
        return freshestValue;
    }
}