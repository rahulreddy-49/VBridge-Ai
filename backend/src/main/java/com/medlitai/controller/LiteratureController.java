package com.medlitai.controller;

import com.medlitai.dto.LiteratureDTO;
import com.medlitai.dto.LiteratureSearchRequest;
import com.medlitai.service.LiteratureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/literature")
public class LiteratureController {

    private final LiteratureService literatureService;

    public LiteratureController(LiteratureService literatureService) {
        this.literatureService = literatureService;
    }

    @GetMapping
    public ResponseEntity<List<LiteratureDTO>> getAllLiterature() {
        return ResponseEntity.ok(literatureService.getAllLiterature());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiteratureDTO> getLiteratureById(@PathVariable Long id) {
        return ResponseEntity.ok(literatureService.getLiteratureById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<LiteratureDTO>> searchLiterature(@RequestBody(required = false) LiteratureSearchRequest request) {
        if (request == null) {
            request = new LiteratureSearchRequest("", "", 10);
        }
        return ResponseEntity.ok(literatureService.searchLiterature(request));
    }
}
