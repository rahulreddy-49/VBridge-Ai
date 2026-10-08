package com.medlitai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "evidence_strength", nullable = false, length = 50)
    private String evidenceStrength;

    @Column(name = "key_features_json", columnDefinition = "TEXT")
    private String keyFeaturesJson;

    @Column(name = "clinical_considerations_json", columnDefinition = "TEXT")
    private String clinicalConsiderationsJson;

    @Column(name = "safety_limitations", columnDefinition = "TEXT")
    private String safetyLimitations;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AnalysisResult() {
    }

    public AnalysisResult(Long caseId, String summary, String evidenceStrength,
                          String keyFeaturesJson, String clinicalConsiderationsJson,
                          String safetyLimitations) {
        this.caseId = caseId;
        this.summary = summary;
        this.evidenceStrength = evidenceStrength;
        this.keyFeaturesJson = keyFeaturesJson;
        this.clinicalConsiderationsJson = clinicalConsiderationsJson;
        this.safetyLimitations = safetyLimitations;
        this.createdAt = LocalDateTime.now();
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

    public String getKeyFeaturesJson() {
        return keyFeaturesJson;
    }

    public void setKeyFeaturesJson(String keyFeaturesJson) {
        this.keyFeaturesJson = keyFeaturesJson;
    }

    public String getClinicalConsiderationsJson() {
        return clinicalConsiderationsJson;
    }

    public void setClinicalConsiderationsJson(String clinicalConsiderationsJson) {
        this.clinicalConsiderationsJson = clinicalConsiderationsJson;
    }

    public String getSafetyLimitations() {
        return safetyLimitations;
    }

    public void setSafetyLimitations(String safetyLimitations) {
        this.safetyLimitations = safetyLimitations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
