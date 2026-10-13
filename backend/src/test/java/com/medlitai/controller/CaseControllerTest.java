package com.medlitai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medlitai.dto.*;
import com.medlitai.service.CaseAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CaseController.class)
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaseAnalysisService caseAnalysisService;

    @Test
    void testCreateCase() throws Exception {
        ClinicalCaseRequest req = new ClinicalCaseRequest(
                55, "Male", "Substernal chest pain", "Diaphoresis", "HTN", "Amlodipine", "Troponin high", "None", true
        );

        ClinicalCaseResponse resp = new ClinicalCaseResponse();
        resp.setId(1L);
        resp.setAge(55);
        resp.setGender("Male");
        resp.setChiefComplaint("Substernal chest pain");

        when(caseAnalysisService.createCase(any(ClinicalCaseRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.chiefComplaint").value("Substernal chest pain"));
    }

    @Test
    void testGetAllCases() throws Exception {
        ClinicalCaseResponse c = new ClinicalCaseResponse();
        c.setId(1L);
        c.setAge(55);
        c.setGender("Male");
        c.setChiefComplaint("Chest pain");

        when(caseAnalysisService.getAllCases()).thenReturn(List.of(c));

        mockMvc.perform(get("/api/cases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testAnalyzeCase() throws Exception {
        AnalysisResultResponse analysis = new AnalysisResultResponse();
        analysis.setId(10L);
        analysis.setCaseId(1L);
        analysis.setSummary("Patient presenting with acute chest discomfort");
        analysis.setEvidenceStrength("High");
        analysis.setDemoMode(true);
        analysis.setCreatedAt(LocalDateTime.now());

        when(caseAnalysisService.analyzeCase(1L)).thenReturn(analysis);

        mockMvc.perform(post("/api/cases/1/analyze"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.caseId").value(1))
                .andExpect(jsonPath("$.evidenceStrength").value("High"));
    }
}
