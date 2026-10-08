package com.medlitai.service;

import com.medlitai.dto.LiteratureDTO;
import com.medlitai.dto.LiteratureSearchRequest;
import com.medlitai.model.Literature;

import java.util.List;

public interface LiteratureService {
    List<LiteratureDTO> getAllLiterature();
    LiteratureDTO getLiteratureById(Long id);
    List<LiteratureDTO> searchLiterature(LiteratureSearchRequest request);
    List<LiteratureDTO> findRelevantForCase(String query, List<String> features, int limit);
    void seedLiteratureIfEmpty();
}
