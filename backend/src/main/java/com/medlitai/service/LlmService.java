package com.medlitai.service;

import com.medlitai.dto.ClinicalConsiderationDTO;
import com.medlitai.dto.LiteratureDTO;
import com.medlitai.model.ClinicalCase;

import java.util.List;

public interface LlmService {
    
    record LlmAnalysisOutput(
            String caseSummary,
            String evidenceSummary,
            String evidenceStrength,
            List<ClinicalConsiderationDTO> clinicalConsiderations,
            String safetyLimitations,
            boolean isDemoMode
    ) {}

    LlmAnalysisOutput generateSynthesis(
            ClinicalCase clinicalCase,
            List<String> keyFeatures,
            List<LiteratureDTO> retrievedLiterature
    );

    boolean isDemoMode();
}
