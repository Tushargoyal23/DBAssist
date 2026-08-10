package com.tushar.dbassist.dbassist.model;

import lombok.Data;

@Data
public class QueryResponse {
    private String sql;
    private String explanation;
    private String schemaSummary;
    private boolean success;
    private String error;

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSchemaSummary(String schemaSummary) {
        this.schemaSummary = schemaSummary;
    }

    public String getSchemaSummary() {
        return schemaSummary;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
