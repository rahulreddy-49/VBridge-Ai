package com.medlitai.controller;

import com.medlitai.dto.HealthResponse;
import com.medlitai.repository.ClinicalCaseRepository;
import com.medlitai.repository.LiteratureRepository;
import com.medlitai.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final ClinicalCaseRepository clinicalCaseRepository;
    private final LiteratureRepository literatureRepository;
    private final LlmService llmService;

    public HealthController(ClinicalCaseRepository clinicalCaseRepository,
                            LiteratureRepository literatureRepository,
                            LlmService llmService) {
        this.clinicalCaseRepository = clinicalCaseRepository;
        this.literatureRepository = literatureRepository;
        this.llmService = llmService;
    }

    @GetMapping({"/health", "/status"})
    public ResponseEntity<HealthResponse> getHealth() {
        long caseCount = clinicalCaseRepository.count();
        long litCount = literatureRepository.count();

        HealthResponse health = new HealthResponse(
                "UP",
                "Connected (MySQL medlit_ai)",
                llmService.isDemoMode(),
                caseCount,
                litCount,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(health);
    }
}
