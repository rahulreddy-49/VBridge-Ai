package com.medlitai.repository;

import com.medlitai.model.EvidenceSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceSourceRepository extends JpaRepository<EvidenceSource, Long> {
    List<EvidenceSource> findByAnalysisIdOrderByRelevanceScoreDesc(Long analysisId);
}
