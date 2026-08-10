package com.tushar.dbassist.dbassist.model;

import lombok.Data;

@Data
public class QueryRequest {

    private String schemaSummary;
    private String question;

    public String getSchemaSummary() {
        return schemaSummary;
    }

    public void setSchemaSummary(String schemaSummary) {
        this.schemaSummary = schemaSummary;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}