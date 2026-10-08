package com.medlitai.service;

import com.medlitai.dto.AnalysisResultResponse;
import com.medlitai.dto.ClinicalCaseRequest;
import com.medlitai.dto.ClinicalCaseResponse;

import java.util.List;

public interface CaseAnalysisService {
    ClinicalCaseResponse createCase(ClinicalCaseRequest request);
    List<ClinicalCaseResponse> getAllCases();
    List<ClinicalCaseResponse> getDemoCases();
    ClinicalCaseResponse getCaseById(Long id);
    AnalysisResultResponse analyzeCase(Long caseId);
    AnalysisResultResponse getAnalysisForCase(Long caseId);
    void seedDemoCasesIfEmpty();
}
