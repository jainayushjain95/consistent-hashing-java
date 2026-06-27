package com.consistenthashing;

import java.util.*;

public class SimulatedNode {
    private final String nodeName;

    // Stores key → value
    private final Map<String, String> data;

    // Stores key → timestamp of last write
    private final Map<String, Long> timestamps;

    public SimulatedNode(String nodeName) {
       this.nodeName = nodeName;
       data = new HashMap<>();
       timestamps = new HashMap<>();
    }

    public void write(String key, String value, long timestamp) {
        data.put(key, value);
        timestamps.put(key, timestamp);
    }

    public String read(String key) {
        return data.get(key);
    }

    public Long getTimestamp(String key) {
       return timestamps.get(key);
    }

    public String getNodeName() {
        return nodeName;
    }
}