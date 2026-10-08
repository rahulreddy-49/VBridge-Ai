package com.medlitai.dto;

import java.time.LocalDateTime;

public class HealthResponse {
    private String status;
    private String database;
    private Boolean demoMode;
    private Long caseCount;
    private Long literatureCount;
    private LocalDateTime timestamp;

    public HealthResponse() {
    }

    public HealthResponse(String status, String database, Boolean demoMode, Long caseCount,
                          Long literatureCount, LocalDateTime timestamp) {
        this.status = status;
        this.database = database;
        this.demoMode = demoMode;
        this.caseCount = caseCount;
        this.literatureCount = literatureCount;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Boolean getDemoMode() {
        return demoMode;
    }

    public void setDemoMode(Boolean demoMode) {
        this.demoMode = demoMode;
    }

    public Long getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(Long caseCount) {
        this.caseCount = caseCount;
    }

    public Long getLiteratureCount() {
        return literatureCount;
    }

    public void setLiteratureCount(Long literatureCount) {
        this.literatureCount = literatureCount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
