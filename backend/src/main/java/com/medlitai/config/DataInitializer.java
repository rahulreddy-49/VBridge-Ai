package com.medlitai.config;

import com.medlitai.service.CaseAnalysisService;
import com.medlitai.service.LiteratureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final LiteratureService literatureService;
    private final CaseAnalysisService caseAnalysisService;

    public DataInitializer(LiteratureService literatureService, CaseAnalysisService caseAnalysisService) {
        this.literatureService = literatureService;
        this.caseAnalysisService = caseAnalysisService;
    }

    @Override
    public void run(String... args) {
        log.info("Checking initial dataset for MedLit AI Simulator...");
        try {
            literatureService.seedLiteratureIfEmpty();
            caseAnalysisService.seedDemoCasesIfEmpty();
            log.info("MedLit AI initial dataset ready.");
        } catch (Exception e) {
            log.error("Data initialization warning: {}", e.getMessage(), e);
        }
    }
}
