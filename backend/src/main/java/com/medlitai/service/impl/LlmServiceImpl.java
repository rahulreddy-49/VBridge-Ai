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
        // Safe placeholder for live external Gemini calls if user supplies GEMINI_API_KEY
        // Falls back to demo synthesis if any failure or format issue occurs
        return generateDemoSynthesis(clinicalCase, keyFeatures, retrievedLiterature);
    }

    private LlmAnalysisOutput callOpenAi(ClinicalCase clinicalCase, List<String> keyFeatures, List<LiteratureDTO> retrievedLiterature) {
        // Safe placeholder for live external OpenAI calls if user supplies OPENAI_API_KEY
        // Falls back to demo synthesis if any failure or format issue occurs
        return generateDemoSynthesis(clinicalCase, keyFeatures, retrievedLiterature);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
