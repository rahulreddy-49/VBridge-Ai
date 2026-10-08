package com.medlitai.controller;

import com.medlitai.dto.AnalysisResultResponse;
import com.medlitai.dto.ClinicalCaseRequest;
import com.medlitai.dto.ClinicalCaseResponse;
import com.medlitai.service.CaseAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseAnalysisService caseAnalysisService;

    public CaseController(CaseAnalysisService caseAnalysisService) {
        this.caseAnalysisService = caseAnalysisService;
    }

    @PostMapping
    public ResponseEntity<ClinicalCaseResponse> createCase(@Valid @RequestBody ClinicalCaseRequest request) {
        ClinicalCaseResponse created = caseAnalysisService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ClinicalCaseResponse>> getAllCases() {
        return ResponseEntity.ok(caseAnalysisService.getAllCases());
    }

    @GetMapping("/demo")
    public ResponseEntity<List<ClinicalCaseResponse>> getDemoCases() {
        return ResponseEntity.ok(caseAnalysisService.getDemoCases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicalCaseResponse> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseAnalysisService.getCaseById(id));
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<AnalysisResultResponse> analyzeCase(@PathVariable Long id) {
        return ResponseEntity.ok(caseAnalysisService.analyzeCase(id));
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<AnalysisResultResponse> getAnalysisForCase(@PathVariable Long id) {
        return ResponseEntity.ok(caseAnalysisService.getAnalysisForCase(id));
    }
}
