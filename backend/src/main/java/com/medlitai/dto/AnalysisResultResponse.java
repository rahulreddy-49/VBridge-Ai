package com.medlitai.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AnalysisResultResponse {
    private Long id;
    private Long caseId;
    private ClinicalCaseResponse clinicalCase;
    private List<String> keyClinicalFeatures;
    private String summary;
    private String evidenceStrength;
    private String evidenceSummary;
    private List<ClinicalConsiderationDTO> clinicalConsiderations;
    private String safetyLimitations;
    private List<LiteratureDTO> relevantLiterature;
    private Boolean demoMode;
    private String disclaimer;
    private LocalDateTime createdAt;

    public AnalysisResultResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public ClinicalCaseResponse getClinicalCase() {
        return clinicalCase;
    }

    public void setClinicalCase(ClinicalCaseResponse clinicalCase) {
        this.clinicalCase = clinicalCase;
    }

    public List<String> getKeyClinicalFeatures() {
        return keyClinicalFeatures;
    }

    public void setKeyClinicalFeatures(List<String> keyClinicalFeatures) {
        this.keyClinicalFeatures = keyClinicalFeatures;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEvidenceStrength() {
        return evidenceStrength;
    }

    public void setEvidenceStrength(String evidenceStrength) {
        this.evidenceStrength = evidenceStrength;
    }

    public String getEvidenceSummary() {
        return evidenceSummary;
    }

    public void setEvidenceSummary(String evidenceSummary) {
        this.evidenceSummary = evidenceSummary;
    }

    public List<ClinicalConsiderationDTO> getClinicalConsiderations() {
        return clinicalConsiderations;
    }

    public void setClinicalConsiderations(List<ClinicalConsiderationDTO> clinicalConsiderations) {
        this.clinicalConsiderations = clinicalConsiderations;
    }

    public String getSafetyLimitations() {
        return safetyLimitations;
    }

    public void setSafetyLimitations(String safetyLimitations) {
        this.safetyLimitations = safetyLimitations;
    }

    public List<LiteratureDTO> getRelevantLiterature() {
        return relevantLiterature;
    }

    public void setRelevantLiterature(List<LiteratureDTO> relevantLiterature) {
        this.relevantLiterature = relevantLiterature;
    }

    public Boolean getDemoMode() {
        return demoMode;
    }

    public void setDemoMode(Boolean demoMode) {
        this.demoMode = demoMode;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
