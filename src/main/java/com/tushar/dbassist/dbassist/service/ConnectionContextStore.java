package com.tushar.dbassist.dbassist.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionContextStore {
    private final Map<String, String> latestSchemaSummaryByConnection = new ConcurrentHashMap<>();
    private volatile String latestSchemaSummary;

    public void storeSchemaSummary(String connectionKey, String schemaSummary) {
        latestSchemaSummaryByConnection.put(connectionKey, schemaSummary);
        this.latestSchemaSummary = schemaSummary;
    }

    public String getSchemaSummary(String connectionKey) {
        return latestSchemaSummaryByConnection.get(connectionKey);
    }

    public String getLatestSchemaSummary() {
        return latestSchemaSummary;
    }

    public void clear(String connectionKey) {
        latestSchemaSummaryByConnection.remove(connectionKey);
    }
}
