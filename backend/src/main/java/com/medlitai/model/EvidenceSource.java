package com.medlitai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evidence_sources")
public class EvidenceSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analysis_id", nullable = false)
    private Long analysisId;

    @Column(name = "literature_id", nullable = false)
    private Long literatureId;

    @Column(name = "relevance_score", nullable = false)
    private Integer relevanceScore;

    public EvidenceSource() {
    }

    public EvidenceSource(Long analysisId, Long literatureId, Integer relevanceScore) {
        this.analysisId = analysisId;
        this.literatureId = literatureId;
        this.relevanceScore = relevanceScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }

    public Long getLiteratureId() {
        return literatureId;
    }

    public void setLiteratureId(Long literatureId) {
        this.literatureId = literatureId;
    }

    public Integer getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Integer relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}
