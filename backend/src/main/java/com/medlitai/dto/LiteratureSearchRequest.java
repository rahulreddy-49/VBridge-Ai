package com.medlitai.dto;

public class LiteratureSearchRequest {
    private String query;
    private String topic;
    private Integer limit;

    public LiteratureSearchRequest() {
    }

    public LiteratureSearchRequest(String query, String topic, Integer limit) {
        this.query = query;
        this.topic = topic;
        this.limit = limit;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
