package com.medlitai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medlitai.dto.*;
import com.medlitai.exception.ResourceNotFoundException;
import com.medlitai.model.AnalysisResult;
import com.medlitai.model.ClinicalCase;
import com.medlitai.model.EvidenceSource;
import com.medlitai.model.Literature;
import com.medlitai.repository.AnalysisResultRepository;
import com.medlitai.repository.ClinicalCaseRepository;
import com.medlitai.repository.EvidenceSourceRepository;
import com.medlitai.repository.LiteratureRepository;
import com.medlitai.service.CaseAnalysisService;
import com.medlitai.service.LlmService;
import com.medlitai.service.RagService;
import com.medlitai.util.ClinicalFeatureExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseAnalysisServiceImpl implements CaseAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(CaseAnalysisServiceImpl.class);

    private final ClinicalCaseRepository clinicalCaseRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final EvidenceSourceRepository evidenceSourceRepository;
    private final LiteratureRepository literatureRepository;
    private final RagService ragService;
    private final LlmService llmService;
    private final ClinicalFeatureExtractor featureExtractor;
    private final ObjectMapper objectMapper;

    public CaseAnalysisServiceImpl(ClinicalCaseRepository clinicalCaseRepository,
                                   AnalysisResultRepository analysisResultRepository,
                                   EvidenceSourceRepository evidenceSourceRepository,
                                   LiteratureRepository literatureRepository,
                                   RagService ragService,
                                   LlmService llmService,
                                   ClinicalFeatureExtractor featureExtractor,
                                   ObjectMapper objectMapper) {
        this.clinicalCaseRepository = clinicalCaseRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.evidenceSourceRepository = evidenceSourceRepository;
        this.literatureRepository = literatureRepository;
        this.ragService = ragService;
        this.llmService = llmService;
        this.featureExtractor = featureExtractor;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ClinicalCaseResponse createCase(ClinicalCaseRequest request) {
        ClinicalCase entity = new ClinicalCase(
                request.getAge(),
                request.getGender(),
                request.getChiefComplaint(),
                request.getSymptoms(),
                request.getMedicalHistory(),
                request.getMedications(),
                request.getLabResults(),
                request.getAdditionalInformation(),
                request.getIsDemoCase()
        );
        ClinicalCase saved = clinicalCaseRepository.save(entity);
        return toCaseResponse(saved);
    }

    @Override
    public List<ClinicalCaseResponse> getAllCases() {
        return clinicalCaseRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toCaseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalCaseResponse> getDemoCases() {
        return clinicalCaseRepository.findByIsDemoCaseTrue().stream()
                .map(this::toCaseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClinicalCaseResponse getCaseById(Long id) {
        ClinicalCase clinicalCase = clinicalCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical case with id " + id + " not found"));
        return toCaseResponse(clinicalCase);
    }

    @Override
    @Transactional
    public AnalysisResultResponse analyzeCase(Long caseId) {
        ClinicalCase clinicalCase = clinicalCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical case with id " + caseId + " not found"));

        // Step 1 & 2: Extract clinical features
        List<String> keyFeatures = featureExtractor.extractFeatures(clinicalCase);

        // Step 3 & 4: Literature Retrieval & Evidence Context via RAG
        List<LiteratureDTO> relevantLiterature = ragService.retrieveEvidence(clinicalCase, keyFeatures);

        // Step 5: Evidence Synthesis via LLM / Demo Engine
        LlmService.LlmAnalysisOutput output = llmService.generateSynthesis(clinicalCase, keyFeatures, relevantLiterature);

        // Persist Analysis Result
        String keyFeaturesJson = writeJson(keyFeatures);
        String considerationsJson = writeJson(output.clinicalConsiderations());

        AnalysisResult result = new AnalysisResult(
                clinicalCase.getId(),
                output.caseSummary(),
                output.evidenceStrength(),
                keyFeaturesJson,
                considerationsJson,
                output.safetyLimitations()
        );
        AnalysisResult savedResult = analysisResultRepository.save(result);

        // Persist Evidence Sources
        for (LiteratureDTO lit : relevantLiterature) {
            EvidenceSource evidenceSource = new EvidenceSource(
                    savedResult.getId(),
                    lit.getId(),
                    lit.getRelevanceScore()
            );
            evidenceSourceRepository.save(evidenceSource);
        }

        // Build Response
        AnalysisResultResponse response = new AnalysisResultResponse();
        response.setId(savedResult.getId());
        response.setCaseId(clinicalCase.getId());
        response.setClinicalCase(toCaseResponse(clinicalCase));
        response.setKeyClinicalFeatures(keyFeatures);
        response.setSummary(output.caseSummary());
        response.setEvidenceStrength(output.evidenceStrength());
        response.setEvidenceSummary(output.evidenceSummary());
        response.setClinicalConsiderations(output.clinicalConsiderations());
        response.setSafetyLimitations(output.safetyLimitations());
        response.setRelevantLiterature(relevantLiterature);
        response.setDemoMode(output.isDemoMode());
        response.setDisclaimer("Educational research prototype. Not a substitute for professional medical advice or clinical judgment.");
        response.setCreatedAt(savedResult.getCreatedAt());

        return response;
    }

    @Override
    public AnalysisResultResponse getAnalysisForCase(Long caseId) {
        ClinicalCase clinicalCase = clinicalCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical case with id " + caseId + " not found"));

        Optional<AnalysisResult> resultOpt = analysisResultRepository.findTopByCaseIdOrderByCreatedAtDesc(caseId);
        if (resultOpt.isEmpty()) {
            return analyzeCase(caseId);
        }

        return buildAnalysisResponse(resultOpt.get(), clinicalCase);
    }

    @Override
    public AnalysisResultResponse getAnalysisById(Long id) {
        // Look up either by analysis ID or fallback to case ID
        Optional<AnalysisResult> resultOpt = analysisResultRepository.findById(id);
        if (resultOpt.isPresent()) {
            AnalysisResult result = resultOpt.get();
            ClinicalCase clinicalCase = clinicalCaseRepository.findById(result.getCaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Associated clinical case not found for analysis id " + id));
            return buildAnalysisResponse(result, clinicalCase);
        }

        // Try lookup by case ID
        Optional<ClinicalCase> caseOpt = clinicalCaseRepository.findById(id);
        if (caseOpt.isPresent()) {
            return getAnalysisForCase(id);
        }

        throw new ResourceNotFoundException("Analysis result or case not found with identifier " + id);
    }

    @Override
    public List<AnalysisResultResponse> getAllAnalyses() {
        List<AnalysisResult> all = analysisResultRepository.findAll();
        all.sort(Comparator.comparing(AnalysisResult::getCreatedAt).reversed());

        List<AnalysisResultResponse> responses = new ArrayList<>();
        for (AnalysisResult result : all) {
            Optional<ClinicalCase> caseOpt = clinicalCaseRepository.findById(result.getCaseId());
            caseOpt.ifPresent(c -> responses.add(buildAnalysisResponse(result, c)));
        }
        return responses;
    }

    private AnalysisResultResponse buildAnalysisResponse(AnalysisResult result, ClinicalCase clinicalCase) {
        List<String> keyFeatures = readJsonList(result.getKeyFeaturesJson());
        List<ClinicalConsiderationDTO> considerations = readJsonConsiderations(result.getClinicalConsiderationsJson());

        // Resolve stored evidence sources with literature records
        List<EvidenceSource> sources = evidenceSourceRepository.findByAnalysisIdOrderByRelevanceScoreDesc(result.getId());
        List<LiteratureDTO> relevantLiterature = new ArrayList<>();

        if (sources != null && !sources.isEmpty()) {
            for (EvidenceSource src : sources) {
                Optional<Literature> litOpt = literatureRepository.findById(src.getLiteratureId());
                if (litOpt.isPresent()) {
                    Literature lit = litOpt.get();
                    relevantLiterature.add(new LiteratureDTO(
                            lit.getId(),
                            lit.getTitle(),
                            lit.getAuthors(),
                            lit.getPublicationYear(),
                            lit.getJournal(),
                            lit.getAbstractText(),
                            lit.getTopic(),
                            lit.getSourceType(),
                            src.getRelevanceScore()
                    ));
                }
            }
        }

        // Fallback to RAG retrieval if no persisted sources found
        if (relevantLiterature.isEmpty()) {
            relevantLiterature = ragService.retrieveEvidence(clinicalCase, keyFeatures);
        }

        AnalysisResultResponse response = new AnalysisResultResponse();
        response.setId(result.getId());
        response.setCaseId(clinicalCase.getId());
        response.setClinicalCase(toCaseResponse(clinicalCase));
        response.setKeyClinicalFeatures(keyFeatures);
        response.setSummary(result.getSummary());
        response.setEvidenceStrength(result.getEvidenceStrength());
        response.setEvidenceSummary(
                "Evidence synthesis grounded on retrieved clinical literature and simulated RAG evidence."
        );
        response.setClinicalConsiderations(considerations);
        response.setSafetyLimitations(result.getSafetyLimitations());
        response.setRelevantLiterature(relevantLiterature);
        response.setDemoMode(llmService.isDemoMode());
        response.setDisclaimer("Educational research prototype. Not a substitute for professional medical advice or clinical judgment.");
        response.setCreatedAt(result.getCreatedAt());

        return response;
    }

    @Override
    @Transactional
    public void seedDemoCasesIfEmpty() {
        if (clinicalCaseRepository.findByIsDemoCaseTrue().isEmpty()) {
            log.info("Seeding synthetic demo clinical cases...");

            ClinicalCase case1 = new ClinicalCase(
                    55,
                    "Male",
                    "Acute substernal chest discomfort radiating to left arm",
                    "Pressure-like retrosternal pain for 3 hours, associated diaphoresis and mild nausea",
                    "Essential hypertension (6 years), hyperlipidemia",
                    "Amlodipine 5mg daily, Atorvastatin 20mg daily",
                    "High-sensitivity Troponin T: 0.082 ng/mL (elevated), ECG: T-wave inversions in V3-V5",
                    "Non-smoker, no prior documented myocardial infarction",
                    true
            );

            ClinicalCase case2 = new ClinicalCase(
                    45,
                    "Female",
                    "Uncontrolled blood pressure with chronic fatigue and polyuria",
                    "Occasional morning occipital headaches, exertional fatigue, nocturia x3",
                    "Hypertension diagnosed 5 years ago, Type 2 Diabetes Mellitus (8 years)",
                    "Metformin 1000mg twice daily, Lisinopril 20mg daily",
                    "Blood pressure: 158/96 mmHg, HbA1c: 9.4%, Serum Creatinine: 1.1 mg/dL, Spot urine albumin/creatinine: 180 mg/g",
                    "Sedentary desk worker, family history of premature cardiovascular disease",
                    true
            );

            ClinicalCase case3 = new ClinicalCase(
                    30,
                    "Female",
                    "Worsening nocturnal cough and recurrent wheezing",
                    "Exertional breathlessness, chest tightness worse at night and cold air exposure",
                    "Childhood allergic rhinitis, intermittent asthma",
                    "Albuterol MDI as needed (using 4-5 times per week)",
                    "Spirometry: FEV1 72% predicted with 15% bronchodilator reversibility, SpO2 96% on room air",
                    "Symptoms exacerbated after recent upper respiratory tract viral infection",
                    true
            );

            clinicalCaseRepository.saveAll(List.of(case1, case2, case3));
            log.info("Successfully seeded 3 synthetic demo cases.");
        }
    }

    private ClinicalCaseResponse toCaseResponse(ClinicalCase entity) {
        ClinicalCaseResponse dto = new ClinicalCaseResponse();
        dto.setId(entity.getId());
        dto.setAge(entity.getAge());
        dto.setGender(entity.getGender());
        dto.setChiefComplaint(entity.getChiefComplaint());
        dto.setSymptoms(entity.getSymptoms());
        dto.setMedicalHistory(entity.getMedicalHistory());
        dto.setMedications(entity.getMedications());
        dto.setLabResults(entity.getLabResults());
        dto.setAdditionalInformation(entity.getAdditionalInformation());
        dto.setIsDemoCase(entity.getIsDemoCase());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<ClinicalConsiderationDTO> readJsonConsiderations(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<ClinicalConsiderationDTO>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
