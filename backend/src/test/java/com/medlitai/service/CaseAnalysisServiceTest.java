package com.medlitai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medlitai.dto.*;
import com.medlitai.model.AnalysisResult;
import com.medlitai.model.ClinicalCase;
import com.medlitai.repository.AnalysisResultRepository;
import com.medlitai.repository.ClinicalCaseRepository;
import com.medlitai.repository.EvidenceSourceRepository;
import com.medlitai.repository.LiteratureRepository;
import com.medlitai.service.impl.CaseAnalysisServiceImpl;
import com.medlitai.util.ClinicalFeatureExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseAnalysisServiceTest {

    @Mock
    private ClinicalCaseRepository clinicalCaseRepository;

    @Mock
    private AnalysisResultRepository analysisResultRepository;

    @Mock
    private EvidenceSourceRepository evidenceSourceRepository;

    @Mock
    private LiteratureRepository literatureRepository;

    @Mock
    private RagService ragService;

    @Mock
    private LlmService llmService;

    private CaseAnalysisService caseAnalysisService;
    private ClinicalFeatureExtractor featureExtractor;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        featureExtractor = new ClinicalFeatureExtractor();
        caseAnalysisService = new CaseAnalysisServiceImpl(
                clinicalCaseRepository,
                analysisResultRepository,
                evidenceSourceRepository,
                literatureRepository,
                ragService,
                llmService,
                featureExtractor,
                objectMapper
        );
    }

    @Test
    void testCreateCase() {
        ClinicalCaseRequest request = new ClinicalCaseRequest(
                55, "Male", "Substernal chest pain", "Diaphoresis", "HTN", "Amlodipine", "Elevated Troponin", "None", true
        );

        ClinicalCase savedCase = new ClinicalCase(
                55, "Male", "Substernal chest pain", "Diaphoresis", "HTN", "Amlodipine", "Elevated Troponin", "None", true
        );
        savedCase.setId(10L);

        when(clinicalCaseRepository.save(any(ClinicalCase.class))).thenReturn(savedCase);

        ClinicalCaseResponse response = caseAnalysisService.createCase(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Substernal chest pain", response.getChiefComplaint());
    }

    @Test
    void testAnalyzeCase() {
        ClinicalCase c = new ClinicalCase(
                55, "Male", "Substernal chest pain", "Diaphoresis", "HTN", "Amlodipine", "Elevated Troponin", "None", true
        );
        c.setId(1L);

        when(clinicalCaseRepository.findById(1L)).thenReturn(Optional.of(c));

        LiteratureDTO lit = new LiteratureDTO(
                100L, "Troponin Guideline", "Smith et al.", 2023, "JACC", "Troponin abstracts", "Cardiovascular", "Demo", 90
        );
        when(ragService.retrieveEvidence(any(), any())).thenReturn(List.of(lit));

        LlmService.LlmAnalysisOutput output = new LlmService.LlmAnalysisOutput(
                "Patient with chest pain",
                "Evidence supports rapid troponin rule out",
                "High",
                List.of(new ClinicalConsiderationDTO("Ischemia evaluation", "Serial troponin", "High", "Guidelines", List.of("Troponin Guideline"), "Non-ischemic elevation possible")),
                "Simulation prototype disclaimer",
                true
        );
        when(llmService.generateSynthesis(any(), any(), any())).thenReturn(output);

        AnalysisResult savedResult = new AnalysisResult(1L, output.caseSummary(), output.evidenceStrength(), "[\"Chest Pain\"]", "[]", output.safetyLimitations());
        savedResult.setId(50L);
        when(analysisResultRepository.save(any())).thenReturn(savedResult);

        AnalysisResultResponse response = caseAnalysisService.analyzeCase(1L);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals(1L, response.getCaseId());
        assertEquals("High", response.getEvidenceStrength());
        assertNotNull(response.getClinicalConsiderations());
    }
}
