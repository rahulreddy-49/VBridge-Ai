package com.medlitai.util;

import com.medlitai.model.ClinicalCase;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class ClinicalFeatureExtractor {

    private static final Map<String, List<String>> FEATURE_PATTERNS = new LinkedHashMap<>();

    static {
        FEATURE_PATTERNS.put("Chest Pain", List.of("chest pain", "chest discomfort", "angina", "pressure in chest", "sternal"));
        FEATURE_PATTERNS.put("Elevated Troponin", List.of("troponin", "c-tnt", "c-tni", "hs-ctn", "elevated troponin"));
        FEATURE_PATTERNS.put("Hypertension", List.of("hypertension", "high blood pressure", "htn", "elevated bp", "160/95", "150/90", "140/90"));
        FEATURE_PATTERNS.put("Diabetes Mellitus", List.of("diabetes", "type 2 diabetes", "t2d", "t2dm", "hba1c", "hyperglycemia", "diabetic"));
        FEATURE_PATTERNS.put("Dyspnea", List.of("shortness of breath", "dyspnea", "breathlessness", "difficulty breathing", "sob"));
        FEATURE_PATTERNS.put("Asthma", List.of("asthma", "wheeze", "wheezing", "bronchospasm", "inhaler", "albuterol"));
        FEATURE_PATTERNS.put("Pneumonia", List.of("pneumonia", "productive cough", "lung infiltrate", "consolidation", "fever", "crepitations", "crackles"));
        FEATURE_PATTERNS.put("Heart Failure", List.of("heart failure", "edema", "chf", "bnp", "orthopnea", "ejection fraction", "ef < 40"));
        FEATURE_PATTERNS.put("Renal Impairment", List.of("kidney disease", "ckd", "elevated creatinine", "renal failure", "nephropathy", "proteinuria"));
        FEATURE_PATTERNS.put("Diaphoresis", List.of("sweating", "diaphoresis", "cold sweat", "clammy"));
        FEATURE_PATTERNS.put("Tachycardia", List.of("tachycardia", "fast heart rate", "elevated pulse", "palpitations", "bpm > 100"));
        FEATURE_PATTERNS.put("ECG Abnormality", List.of("st-elevation", "st-depression", "t-wave", "ecg", "ekg", "arrhythmia"));
    }

    public List<String> extractFeatures(ClinicalCase clinicalCase) {
        if (clinicalCase == null) {
            return Collections.emptyList();
        }

        StringBuilder combinedText = new StringBuilder();
        appendIfPresent(combinedText, clinicalCase.getChiefComplaint());
        appendIfPresent(combinedText, clinicalCase.getSymptoms());
        appendIfPresent(combinedText, clinicalCase.getMedicalHistory());
        appendIfPresent(combinedText, clinicalCase.getLabResults());
        appendIfPresent(combinedText, clinicalCase.getMedications());
        appendIfPresent(combinedText, clinicalCase.getAdditionalInformation());

        String lowerCaseText = combinedText.toString().toLowerCase(Locale.ROOT);
        Set<String> matchedFeatures = new LinkedHashSet<>();

        for (Map.Entry<String, List<String>> entry : FEATURE_PATTERNS.entrySet()) {
            for (String synonym : entry.getValue()) {
                if (lowerCaseText.contains(synonym.toLowerCase(Locale.ROOT))) {
                    matchedFeatures.add(entry.getKey());
                    break;
                }
            }
        }

        // If no predefined feature pattern matched, fall back to chief complaint summary
        if (matchedFeatures.isEmpty() && clinicalCase.getChiefComplaint() != null && !clinicalCase.getChiefComplaint().isBlank()) {
            matchedFeatures.add(truncateFeature(clinicalCase.getChiefComplaint()));
        }

        return new ArrayList<>(matchedFeatures);
    }

    public String buildSearchQuery(ClinicalCase clinicalCase) {
        List<String> features = extractFeatures(clinicalCase);
        if (!features.isEmpty()) {
            return String.join(" ", features);
        }
        StringBuilder query = new StringBuilder();
        if (clinicalCase.getChiefComplaint() != null) query.append(clinicalCase.getChiefComplaint()).append(" ");
        if (clinicalCase.getSymptoms() != null) query.append(clinicalCase.getSymptoms()).append(" ");
        if (clinicalCase.getMedicalHistory() != null) query.append(clinicalCase.getMedicalHistory());
        return query.toString().trim();
    }

    private void appendIfPresent(StringBuilder sb, String text) {
        if (text != null && !text.isBlank()) {
            sb.append(text).append(" ");
        }
    }

    private String truncateFeature(String input) {
        String clean = input.replaceAll("[^a-zA-Z0-9 ]", "").trim();
        return clean.length() > 30 ? clean.substring(0, 30) + "..." : clean;
    }
}
