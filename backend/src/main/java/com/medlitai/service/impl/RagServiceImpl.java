package com.medlitai.service.impl;

import com.medlitai.dto.LiteratureDTO;
import com.medlitai.model.ClinicalCase;
import com.medlitai.service.LiteratureService;
import com.medlitai.service.RagService;
import com.medlitai.util.ClinicalFeatureExtractor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagServiceImpl implements RagService {

    private final LiteratureService literatureService;
    private final ClinicalFeatureExtractor featureExtractor;

    public RagServiceImpl(LiteratureService literatureService, ClinicalFeatureExtractor featureExtractor) {
        this.literatureService = literatureService;
        this.featureExtractor = featureExtractor;
    }

    @Override
    public List<LiteratureDTO> retrieveEvidence(ClinicalCase clinicalCase, List<String> extractedFeatures) {
        String query = featureExtractor.buildSearchQuery(clinicalCase);
        // Retrieve top 4 most relevant literature records
        return literatureService.findRelevantForCase(query, extractedFeatures, 4);
    }

    @Override
    public String buildEvidenceContext(List<LiteratureDTO> retrievedLiterature) {
        if (retrievedLiterature == null || retrievedLiterature.isEmpty()) {
            return "No specific literature evidence retrieved for this clinical scenario.";
        }

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < retrievedLiterature.size(); i++) {
            LiteratureDTO lit = retrievedLiterature.get(i);
            context.append(String.format("[%d] Title: %s\n", i + 1, lit.getTitle()));
            context.append(String.format("    Authors: %s (%d), Journal: %s\n", lit.getAuthors(), lit.getPublicationYear(), lit.getJournal()));
            context.append(String.format("    Topic: %s | Relevance: %d%%\n", lit.getTopic(), lit.getRelevanceScore()));
            context.append(String.format("    Abstract: %s\n\n", lit.getAbstractText()));
        }
        return context.toString();
    }
}
