package com.consistenthashing;

import com.google.common.hash.Hashing;
import org.checkerframework.checker.units.qual.A;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConsistentHashRing {
    private final TreeMap<Long, String> ring = new TreeMap<>();

    private long hash(String key) {
        int hash = Hashing.murmur3_32_fixed().hashString(key, StandardCharsets.UTF_8).asInt();
        return Integer.toUnsignedLong(hash);
    }

    public void addNode(String nodeName, int vnodeCount) {
        for(int i = 1; i <= vnodeCount; i++) {
            String vnodeName = nodeName + "-vnode-" + i;
            long pos = hash(vnodeName);
            ring.put(pos, vnodeName);
        }
    }

    public void removeNode(String nodeName, int vnodeCount) {
        for(int i = 1; i <= vnodeCount; i++) {
            String vnodeName = nodeName + "-vnode-" + i;
            long pos = hash(vnodeName);
            ring.remove(pos);
        }
    }

    private String extractPhysicalNode(String vnodeLabel) {
        return vnodeLabel.split("-vnode-")[0];
    }

    public String getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        long pos = hash(key);
        Long serverPos = ring.ceilingKey(pos);
        if(serverPos == null) {
            serverPos = ring.firstKey();
        }
        return extractPhysicalNode(ring.get(serverPos));
    }

    public void printRing() {
        for(Long pos : ring.keySet()) {
            System.out.println(pos + ", " + ring.get(pos));
        }
    }

    public void printDistribution(String[] keys) {
        Map<String, Integer> map = new HashMap<>();
        int total = keys.length;
        for(String key : keys) {
            String node = getNode(key);
            map.put(node, 1 + map.getOrDefault(node, 0));
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            double percentage = (100.0 * entry.getValue()) / total;
            System.out.println(entry.getKey() + " : " + percentage);
        }
    }

    public Map<String, String> getRoutingSnapshot(String[] keys) {
        Map<String, String> map = new HashMap<>();
        for(String key : keys) {
            String node = getNode(key);
            map.put(key, node);
        }
        return map;
    }

    public void printMigrationImpact(Map<String, String> before,
                                     Map<String, String> after) {
        int moved = 0;
        int total = before.size();

        for(Map.Entry<String, String> entry : before.entrySet()) {
            String key = entry.getKey();
            if(!after.get(key).equals(before.get(key))) {
                System.out.println("MOVED: " + key + " → was on "+before.get(key)+", now on " + after.get(key));
                moved++;
            }
        }
        double percentage = (100.0 * moved) / total;
        System.out.println("\nTotal moved: " + moved + " / " + total + " (" + percentage + "%)");
    }

    public List<String> getReplicaNodes(String key, int rf) {
        List<String> replicaNodes = new ArrayList<>();
        long position = hash(key);
        int visited = 0;
        int totalVnodes = ring.size();

        while (replicaNodes.size() < rf && visited < totalVnodes) {
            Long nodePosition = ring.ceilingKey(position);
            if (nodePosition == null) {
                nodePosition = ring.firstKey();
            }
            String node = extractPhysicalNode(ring.get(nodePosition));
            if (!replicaNodes.contains(node)) {
                replicaNodes.add(node);
            }
            position = nodePosition + 1;
            visited++;
        }
        return replicaNodes;
    }
}
