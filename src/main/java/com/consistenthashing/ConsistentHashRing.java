package com.consistenthashing;

import com.google.common.hash.Hashing;

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
}
