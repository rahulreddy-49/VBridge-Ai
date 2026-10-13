package com.medlitai.controller;

import com.medlitai.repository.ClinicalCaseRepository;
import com.medlitai.repository.LiteratureRepository;
import com.medlitai.service.LlmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicalCaseRepository clinicalCaseRepository;

    @MockBean
    private LiteratureRepository literatureRepository;

    @MockBean
    private LlmService llmService;

    @Test
    void testGetHealthEndpoint() throws Exception {
        when(clinicalCaseRepository.count()).thenReturn(5L);
        when(literatureRepository.count()).thenReturn(12L);
        when(llmService.isDemoMode()).thenReturn(true);

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.demoMode").value(true))
                .andExpect(jsonPath("$.caseCount").value(5))
                .andExpect(jsonPath("$.literatureCount").value(12));
    }

    @Test
    void testGetStatusEndpoint() throws Exception {
        when(clinicalCaseRepository.count()).thenReturn(3L);
        when(literatureRepository.count()).thenReturn(12L);
        when(llmService.isDemoMode()).thenReturn(true);

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.caseCount").value(3));
    }
}
