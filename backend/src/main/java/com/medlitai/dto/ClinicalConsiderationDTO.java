package com.medlitai.dto;

import java.util.List;

public class ClinicalConsiderationDTO {
    private String title;
    private String description;
    private String confidenceLevel; // High, Moderate, Limited
    private String supportingEvidence;
    private List<String> relevantSources;
    private String potentialRisksAndLimitations;

    public ClinicalConsiderationDTO() {
    }

    public ClinicalConsiderationDTO(String title, String description, String confidenceLevel,
                                    String supportingEvidence, List<String> relevantSources,
                                    String potentialRisksAndLimitations) {
        this.title = title;
        this.description = description;
        this.confidenceLevel = confidenceLevel;
        this.supportingEvidence = supportingEvidence;
        this.relevantSources = relevantSources;
        this.potentialRisksAndLimitations = potentialRisksAndLimitations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getSupportingEvidence() {
        return supportingEvidence;
    }

    public void setSupportingEvidence(String supportingEvidence) {
        this.supportingEvidence = supportingEvidence;
    }

    public List<String> getRelevantSources() {
        return relevantSources;
    }

    public void setRelevantSources(List<String> relevantSources) {
        this.relevantSources = relevantSources;
    }

    public String getPotentialRisksAndLimitations() {
        return potentialRisksAndLimitations;
    }

    public void setPotentialRisksAndLimitations(String potentialRisksAndLimitations) {
        this.potentialRisksAndLimitations = potentialRisksAndLimitations;
    }
}
