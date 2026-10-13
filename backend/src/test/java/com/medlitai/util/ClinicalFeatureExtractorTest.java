package com.medlitai.util;

import com.medlitai.model.ClinicalCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClinicalFeatureExtractorTest {

    private ClinicalFeatureExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new ClinicalFeatureExtractor();
    }

    @Test
    void testExtractFeaturesCardiovascularIschemia() {
        ClinicalCase clinicalCase = new ClinicalCase(
                55, "Male",
                "Acute substernal chest discomfort radiating to left arm",
                "Retrosternal pressure, diaphoresis",
                "Essential hypertension, hyperlipidemia",
                "Amlodipine, Atorvastatin",
                "High-sensitivity Troponin T: 0.082 ng/mL (elevated), ECG: T-wave inversions",
                "Synthetic demo case",
                true
        );

        List<String> features = extractor.extractFeatures(clinicalCase);

        assertNotNull(features);
        assertTrue(features.contains("Chest Pain"), "Should detect Chest Pain");
        assertTrue(features.contains("Elevated Troponin"), "Should detect Elevated Troponin");
        assertTrue(features.contains("Hypertension"), "Should detect Hypertension");
        assertTrue(features.contains("Diaphoresis"), "Should detect Diaphoresis");
    }

    @Test
    void testExtractFeaturesHypertensionAndDiabetes() {
        ClinicalCase clinicalCase = new ClinicalCase(
                45, "Female",
                "Uncontrolled blood pressure with chronic fatigue and polyuria",
                "Morning occipital headaches, exertional fatigue",
                "Hypertension (5 years), Type 2 Diabetes Mellitus (8 years)",
                "Metformin 1000mg, Lisinopril 20mg",
                "Blood pressure: 158/96 mmHg, HbA1c: 9.4%",
                "Synthetic demo case",
                true
        );

        List<String> features = extractor.extractFeatures(clinicalCase);

        assertNotNull(features);
        assertTrue(features.contains("Hypertension"), "Should detect Hypertension");
        assertTrue(features.contains("Diabetes Mellitus"), "Should detect Diabetes Mellitus");
    }

    @Test
    void testExtractFeaturesAsthma() {
        ClinicalCase clinicalCase = new ClinicalCase(
                30, "Female",
                "Worsening nocturnal cough and recurrent wheezing",
                "Exertional breathlessness, chest tightness worse at night",
                "Intermittent asthma",
                "Albuterol MDI",
                "Spirometry: FEV1 72% predicted with reversibility",
                "Synthetic demo case",
                true
        );

        List<String> features = extractor.extractFeatures(clinicalCase);

        assertNotNull(features);
        assertTrue(features.contains("Asthma"), "Should detect Asthma");
        assertTrue(features.contains("Dyspnea"), "Should detect Dyspnea / breathlessness");
    }

    @Test
    void testNullClinicalCase() {
        List<String> features = extractor.extractFeatures(null);
        assertNotNull(features);
        assertTrue(features.isEmpty());
    }

    @Test
    void testBuildSearchQuery() {
        ClinicalCase clinicalCase = new ClinicalCase(
                60, "Male", "Chest pain and shortness of breath",
                "Pressure", "Hypertension", "None", "None", "", true
        );
        String query = extractor.buildSearchQuery(clinicalCase);
        assertNotNull(query);
        assertFalse(query.isBlank());
        assertTrue(query.contains("Chest Pain"));
    }
}
