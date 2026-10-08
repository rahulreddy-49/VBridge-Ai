package com.medlitai.service;

import com.medlitai.dto.LiteratureDTO;
import com.medlitai.model.ClinicalCase;

import java.util.List;

public interface RagService {
    List<LiteratureDTO> retrieveEvidence(ClinicalCase clinicalCase, List<String> extractedFeatures);
    String buildEvidenceContext(List<LiteratureDTO> retrievedLiterature);
}
