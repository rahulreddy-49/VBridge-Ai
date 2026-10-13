package com.medlitai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medlitai.dto.ClinicalConsiderationDTO;
import com.medlitai.dto.LiteratureDTO;
import com.medlitai.model.ClinicalCase;
import com.medlitai.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class LlmServiceImpl implements LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${medlit.ai.openai.api-key:}")
    private String openAiApiKey;

    @Value("${medlit.ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${medlit.ai.demo-mode:true}")
    private boolean demoMode;

    public LlmServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public boolean isDemoMode() {
        return demoMode || (isEmpty(openAiApiKey) && isEmpty(geminiApiKey));
    }

    @Override
    public LlmAnalysisOutput generateSynthesis(
            ClinicalCase clinicalCase,
            List<String> keyFeatures,
            List<LiteratureDTO> retrievedLiterature
    ) {
        if (!isDemoMode()) {
            try {
                if (!isEmpty(geminiApiKey)) {
                    return callGemini(clinicalCase, keyFeatures, retrievedLiterature);
                } else if (!isEmpty(openAiApiKey)) {
                    return callOpenAi(clinicalCase, keyFeatures, retrievedLiterature);
                }
            } catch (Exception e) {
                log.warn("External LLM call failed ({}), gracefully falling back to deterministic Demo Mode RAG synthesis", e.getMessage());
            }
        }

        // Demo Mode / Fallback synthesis
        return generateDemoSynthesis(clinicalCase, keyFeatures, retrievedLiterature);
    }

    private LlmAnalysisOutput generateDemoSynthesis(
            ClinicalCase clinicalCase,
            List<String> keyFeatures,
            List<LiteratureDTO> retrievedLiterature
    ) {
        String patientProfile = String.format("%d-year-old %s",
                clinicalCase.getAge(),
                clinicalCase.getGender() != null ? clinicalCase.getGender().toLowerCase(Locale.ROOT) : "patient");

        // 1. Clinical Case Summary
        StringBuilder caseSummary = new StringBuilder();
        caseSummary.append(String.format("Presentation of a %s presenting with a chief complaint of \"%s\". ",
                patientProfile, clinicalCase.getChiefComplaint()));
        if (clinicalCase.getSymptoms() != null && !clinicalCase.getSymptoms().isBlank()) {
            caseSummary.append(String.format("Reported symptoms include %s. ", clinicalCase.getSymptoms()));
        }
        if (clinicalCase.getMedicalHistory() != null && !clinicalCase.getMedicalHistory().isBlank()) {
            caseSummary.append(String.format("Relevant background history: %s. ", clinicalCase.getMedicalHistory()));
        }
        if (clinicalCase.getLabResults() != null && !clinicalCase.getLabResults().isBlank()) {
            caseSummary.append(String.format("Key laboratory and diagnostic findings noted: %s. ", clinicalCase.getLabResults()));
        }
        if (clinicalCase.getMedications() != null && !clinicalCase.getMedications().isBlank()) {
            caseSummary.append(String.format("Current documented medications: %s.", clinicalCase.getMedications()));
        }

        // 2. Evidence Strength Assessment
        int avgRelevance = retrievedLiterature.stream()
                .mapToInt(LiteratureDTO::getRelevanceScore)
                .findFirst().orElse(70);
        String evidenceStrength = (avgRelevance >= 85) ? "High" : (avgRelevance >= 70 ? "Moderate" : "Limited");

        // 3. Evidence Summary grounded on retrieved literature
        StringBuilder evidenceSummary = new StringBuilder();
        evidenceSummary.append("Retrieved medical literature supports an evidence-grounded evaluation of this clinical presentation. ");
        if (!retrievedLiterature.isEmpty()) {
            LiteratureDTO primaryLit = retrievedLiterature.get(0);
            evidenceSummary.append(String.format("According to %s (%s, %d), ",
                    primaryLit.getAuthors(), primaryLit.getJournal(), primaryLit.getPublicationYear()));
            evidenceSummary.append(primaryLit.getAbstractText().substring(0, Math.min(220, primaryLit.getAbstractText().length())));
            evidenceSummary.append("... ");

            if (retrievedLiterature.size() > 1) {
                LiteratureDTO secondaryLit = retrievedLiterature.get(1);
                evidenceSummary.append(String.format("Furthermore, findings in %s (%d) emphasize ",
                        secondaryLit.getJournal(), secondaryLit.getPublicationYear()));
                evidenceSummary.append(secondaryLit.getAbstractText().substring(0, Math.min(180, secondaryLit.getAbstractText().length())));
                evidenceSummary.append("... ");
            }
        }
        evidenceSummary.append("All retrieved considerations reflect simulated educational evidence synthesis.");

        // 4. Clinical Considerations (Strict non-diagnostic language)
        List<ClinicalConsiderationDTO> considerations = buildConsiderations(clinicalCase, keyFeatures, retrievedLiterature);

        // 5. Safety & Limitations Statement
        String safetyLimitations = "CRITICAL EDUCATIONAL LIMITATION: This analysis is generated by an educational simulation prototype "
                + "and does NOT constitute medical diagnosis, clinical prescription, or definitive triage. "
                + "Actual clinical decision-making requires comprehensive in-person physical assessment, validated diagnostic testing, "
                + "and the judgment of a licensed healthcare provider.";

        return new LlmAnalysisOutput(
                caseSummary.toString().trim(),
                evidenceSummary.toString().trim(),
                evidenceStrength,
                considerations,
                safetyLimitations,
                true // Demo Mode
        );
    }

    private List<ClinicalConsiderationDTO> buildConsiderations(
            ClinicalCase clinicalCase,
            List<String> keyFeatures,
            List<LiteratureDTO> retrievedLiterature
    ) {
        List<ClinicalConsiderationDTO> considerations = new ArrayList<>();
        List<String> sourceTitles = retrievedLiterature.stream()
                .map(l -> l.getTitle() + " (" + l.getPublicationYear() + ")")
                .limit(3)
                .toList();

        boolean hasChestPainOrTroponin = keyFeatures.contains("Chest Pain") || keyFeatures.contains("Elevated Troponin");
        boolean hasHypertension = keyFeatures.contains("Hypertension");
        boolean hasDiabetes = keyFeatures.contains("Diabetes Mellitus");
        boolean hasAsthma = keyFeatures.contains("Asthma") || keyFeatures.contains("Dyspnea");
        boolean hasPneumonia = keyFeatures.contains("Pneumonia");

        if (hasChestPainOrTroponin) {
            considerations.add(new ClinicalConsiderationDTO(
                    "Potential Acute Coronary / Ischemic Evaluation",
                    "Evidence-supported clinical pathways emphasize serial high-sensitivity cardiac troponin testing and urgent 12-lead electrocardiography for chest discomfort.",
                    "High",
                    "Guidelines indicate accelerated diagnostic pathways rule-in or rule-out acute myocardial injury with >99% NPV when serial biomarker curves remain concordant.",
                    sourceTitles,
                    "Elevated troponin may also occur in myocarditis, acute pulmonary embolism, severe sepsis, or chronic renal failure."
            ));
        }

        if (hasHypertension) {
            considerations.add(new ClinicalConsiderationDTO(
                    "Blood Pressure Optimization and End-Organ Screening",
                    "Retrieved literature highlights guideline-directed multi-drug combination strategies when systolic targets remain elevated.",
                    "Moderate",
                    "Single-pill dual or triple combinations (e.g., ACEi/ARB with CCB/diuretic) provide superior compliance and blood pressure control compared to stepped monotherapy.",
                    sourceTitles,
                    "Requires monitoring of serum electrolytes and renal function; secondary etiology screening should be considered if refractory."
            ));
        }

        if (hasDiabetes) {
            considerations.add(new ClinicalConsiderationDTO(
                    "Metabolic & Cardiorenal Protection Considerations",
                    "Current clinical practice guidelines recommend screening for microvascular and macrovascular complications, and evaluating cardiorenal protective pharmacotherapy.",
                    "High",
                    "Cardiovascular outcome trials establish significant event reduction with SGLT2 inhibitors and GLP-1 receptor agonists in comorbid metabolic disease.",
                    sourceTitles,
                    "Individualized HbA1c goals are necessary to balance hypoglycemia risk with long-term vascular protection."
            ));
        }

        if (hasAsthma) {
            considerations.add(new ClinicalConsiderationDTO(
                    "Airway Inflammation & Anti-Inflammatory Reliever Strategy",
                    "Contemporary asthma management strategies advise against short-acting beta2-agonist monotherapy in favor of inhaled corticosteroid-containing regimens.",
                    "High",
                    "GINA recommendations identify low-dose ICS-formoterol as the primary reliever and controller strategy to reduce severe exacerbation frequency.",
                    sourceTitles,
                    "Inhaler technique and environmental trigger exposure must be verified by a clinician before therapy escalation."
            ));
        }

        if (hasPneumonia) {
            considerations.add(new ClinicalConsiderationDTO(
                    "Infectious Respiratory Stratification & Antimicrobial Stewardship",
                    "Clinical decision protocols recommend objective severity scoring (CURB-65 / PSI) and radiographic correlation to determine appropriate site of care.",
                    "Moderate",
                    "Empiric guideline-concordant therapy tailored to comorbid risk factors improves clinical resolution while minimizing antimicrobial resistance.",
                    sourceTitles,
                    "Atypical pathogens and drug allergies require verified clinical and microbiological confirmation."
            ));
        }

        // Fallback generic consideration if no specific trigger fired
        if (considerations.isEmpty()) {
            considerations.add(new ClinicalConsiderationDTO(
                    "Multifactorial Clinical Assessment & Specialist Review",
                    "Synthesized literature suggests structured diagnostic workup correlating presenting signs, laboratory parameters, and documented clinical history.",
                    "Moderate",
                    "Retrieved clinical evidence underscores holistic risk stratification rather than reliance on isolated symptoms.",
                    sourceTitles,
                    "Synthetic educational case with limited clinical parameters; requires formal bedside evaluation."
            ));
        }

        return considerations;
    }

    private LlmAnalysisOutput callGemini(ClinicalCase clinicalCase, List<String> keyFeatures, List<LiteratureDTO> retrievedLiterature) {
        try {
            String prompt = buildPrompt(clinicalCase, keyFeatures, retrievedLiterature);
            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> generationConfig = Map.of(
                    "temperature", 0.2,
                    "responseMimeType", "application/json"
            );
            Map<String, Object> payload = Map.of(
                    "contents", List.of(content),
                    "generationConfig", generationConfig
            );

            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey.trim()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                if (!textNode.isMissingNode()) {
                    return parseStructuredOutput(textNode.asText(), false);
                }
            } else {
                log.warn("Gemini API returned HTTP status {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("Error calling Gemini API: {}", e.getMessage());
        }
        return generateDemoSynthesis(clinicalCase, keyFeatures, retrievedLiterature);
    }

    private LlmAnalysisOutput callOpenAi(ClinicalCase clinicalCase, List<String> keyFeatures, List<LiteratureDTO> retrievedLiterature) {
        try {
            String prompt = buildPrompt(clinicalCase, keyFeatures, retrievedLiterature);
            Map<String, Object> payload = Map.of(
                    "model", "gpt-4o-mini",
                    "temperature", 0.2,
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are an educational medical literature research synthesis assistant. You output only valid structured JSON conforming to the requested schema. Never provide prescriptive advice or diagnoses."),
                            Map.of("role", "user", "content", prompt)
                    )
            );

            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey.trim())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
                if (!contentNode.isMissingNode()) {
                    return parseStructuredOutput(contentNode.asText(), false);
                }
            } else {
                log.warn("OpenAI API returned HTTP status {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("Error calling OpenAI API: {}", e.getMessage());
        }
        return generateDemoSynthesis(clinicalCase, keyFeatures, retrievedLiterature);
    }

    private String buildPrompt(ClinicalCase clinicalCase, List<String> keyFeatures, List<LiteratureDTO> retrievedLiterature) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are analyzing a synthetic educational clinical scenario for evidence grounding.\n\n");
        sb.append("CLINICAL PRESENTATION:\n");
        sb.append(String.format("- Patient: %d-year-old %s\n", clinicalCase.getAge(), clinicalCase.getGender()));
        sb.append(String.format("- Chief Complaint: %s\n", clinicalCase.getChiefComplaint()));
        if (clinicalCase.getSymptoms() != null) sb.append(String.format("- Symptoms: %s\n", clinicalCase.getSymptoms()));
        if (clinicalCase.getMedicalHistory() != null) sb.append(String.format("- Medical History: %s\n", clinicalCase.getMedicalHistory()));
        if (clinicalCase.getMedications() != null) sb.append(String.format("- Medications: %s\n", clinicalCase.getMedications()));
        if (clinicalCase.getLabResults() != null) sb.append(String.format("- Labs/Diagnostics: %s\n", clinicalCase.getLabResults()));
        sb.append(String.format("- Extracted Clinical Features: %s\n\n", String.join(", ", keyFeatures)));

        sb.append("RETRIEVED MEDICAL LITERATURE CONTEXT (DATA):\n");
        for (int i = 0; i < retrievedLiterature.size(); i++) {
            LiteratureDTO lit = retrievedLiterature.get(i);
            sb.append(String.format("[%d] %s (%s, %d) - %s\n", i + 1, lit.getTitle(), lit.getJournal(), lit.getPublicationYear(), lit.getAbstractText()));
        }

        sb.append("\nReturn a JSON object with this exact structure:\n");
        sb.append("{\n");
        sb.append("  \"caseSummary\": \"Comprehensive narrative summary of the case presentation\",\n");
        sb.append("  \"evidenceSummary\": \"Objective synthesis of the retrieved literature relevant to this scenario\",\n");
        sb.append("  \"evidenceStrength\": \"High\" | \"Moderate\" | \"Limited\",\n");
        sb.append("  \"clinicalConsiderations\": [\n");
        sb.append("    {\n");
        sb.append("      \"title\": \"Area of clinical evaluation\",\n");
        sb.append("      \"description\": \"Informational explanation\",\n");
        sb.append("      \"confidenceLevel\": \"High\" | \"Moderate\" | \"Limited\",\n");
        sb.append("      \"supportingEvidence\": \"Guideline-backed evidence rationale\",\n");
        sb.append("      \"relevantSources\": [\"Title of source 1\", \"Title of source 2\"],\n");
        sb.append("      \"potentialRisksAndLimitations\": \"Important limitations or differential questions\"\n");
        sb.append("    }\n");
        sb.append("  ],\n");
        sb.append("  \"safetyLimitations\": \"Clear plain-language statement of clinical limitations, missing information, and necessity of licensed medical judgment\"\n");
        sb.append("}\n");
        return sb.toString();
    }

    private LlmAnalysisOutput parseStructuredOutput(String jsonText, boolean isDemoMode) {
        try {
            JsonNode root = objectMapper.readTree(jsonText);
            String caseSummary = root.path("caseSummary").asText("Clinical case summary extracted from scenario.");
            String evidenceSummary = root.path("evidenceSummary").asText("Synthesized evidence from medical literature.");
            String evidenceStrength = root.path("evidenceStrength").asText("Moderate");
            String safetyLimitations = root.path("safetyLimitations").asText("Prototype simulation output. Requires professional physician review.");

            List<ClinicalConsiderationDTO> considerations = new ArrayList<>();
            JsonNode consNode = root.path("clinicalConsiderations");
            if (consNode.isArray()) {
                for (JsonNode item : consNode) {
                    List<String> sources = new ArrayList<>();
                    JsonNode sourcesNode = item.path("relevantSources");
                    if (sourcesNode.isArray()) {
                        sourcesNode.forEach(s -> sources.add(s.asText()));
                    }
                    considerations.add(new ClinicalConsiderationDTO(
                            item.path("title").asText("Clinical Consideration"),
                            item.path("description").asText(""),
                            item.path("confidenceLevel").asText("Moderate"),
                            item.path("supportingEvidence").asText(""),
                            sources,
                            item.path("potentialRisksAndLimitations").asText("")
                    ));
                }
            }

            return new LlmAnalysisOutput(
                    caseSummary,
                    evidenceSummary,
                    evidenceStrength,
                    considerations,
                    safetyLimitations,
                    isDemoMode
            );
        } catch (Exception e) {
            log.warn("Failed to parse external LLM structured JSON response: {}", e.getMessage());
            throw new RuntimeException("LLM JSON parsing failed", e);
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
