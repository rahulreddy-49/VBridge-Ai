package com.medlitai.controller;

import com.medlitai.dto.AnalysisResultResponse;
import com.medlitai.service.CaseAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyses")
public class AnalysisController {

    private final CaseAnalysisService caseAnalysisService;

    public AnalysisController(CaseAnalysisService caseAnalysisService) {
        this.caseAnalysisService = caseAnalysisService;
    }

    @GetMapping
    public ResponseEntity<List<AnalysisResultResponse>> getAllAnalyses() {
        return ResponseEntity.ok(caseAnalysisService.getAllAnalyses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResultResponse> getAnalysisById(@PathVariable Long id) {
        return ResponseEntity.ok(caseAnalysisService.getAnalysisById(id));
    }
}
