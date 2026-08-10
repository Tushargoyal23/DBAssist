package com.tushar.dbassist.dbassist.model;

import java.util.List;

public class MongoSchemaSummaryResponse {
    private boolean success;
    private String message;
    private String databaseName;
    private List<String> collections;
    private String schemaSummary;

    public MongoSchemaSummaryResponse() {
    }

    public MongoSchemaSummaryResponse(boolean success, String message, String databaseName, List<String> collections, String schemaSummary) {
        this.success = success;
        this.message = message;
        this.databaseName = databaseName;
        this.collections = collections;
        this.schemaSummary = schemaSummary;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<String> getCollections() {
        return collections;
    }

    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    public String getSchemaSummary() {
        return schemaSummary;
    }

    public void setSchemaSummary(String schemaSummary) {
        this.schemaSummary = schemaSummary;
    }
}
