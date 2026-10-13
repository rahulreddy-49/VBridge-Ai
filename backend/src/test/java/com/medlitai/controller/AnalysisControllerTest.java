package com.medlitai.controller;

import com.medlitai.dto.AnalysisResultResponse;
import com.medlitai.service.CaseAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaseAnalysisService caseAnalysisService;

    @Test
    void testGetAnalysisById() throws Exception {
        AnalysisResultResponse analysis = new AnalysisResultResponse();
        analysis.setId(5L);
        analysis.setCaseId(2L);
        analysis.setSummary("Summary of metabolic clinical case");
        analysis.setEvidenceStrength("Moderate");
        analysis.setDemoMode(true);
        analysis.setCreatedAt(LocalDateTime.now());

        when(caseAnalysisService.getAnalysisById(5L)).thenReturn(analysis);

        mockMvc.perform(get("/api/analyses/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.caseId").value(2))
                .andExpect(jsonPath("$.summary").value("Summary of metabolic clinical case"));
    }

    @Test
    void testGetAllAnalyses() throws Exception {
        AnalysisResultResponse analysis = new AnalysisResultResponse();
        analysis.setId(5L);
        analysis.setCaseId(2L);
        analysis.setSummary("Summary");

        when(caseAnalysisService.getAllAnalyses()).thenReturn(List.of(analysis));

        mockMvc.perform(get("/api/analyses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5));
    }
}
