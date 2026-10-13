package com.medlitai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medlitai.dto.LiteratureDTO;
import com.medlitai.dto.LiteratureSearchRequest;
import com.medlitai.exception.ResourceNotFoundException;
import com.medlitai.model.Literature;
import com.medlitai.repository.LiteratureRepository;
import com.medlitai.service.LiteratureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LiteratureServiceImpl implements LiteratureService {

    private static final Logger log = LoggerFactory.getLogger(LiteratureServiceImpl.class);

    private final LiteratureRepository literatureRepository;
    private final ObjectMapper objectMapper;

    public LiteratureServiceImpl(LiteratureRepository literatureRepository, ObjectMapper objectMapper) {
        this.literatureRepository = literatureRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<LiteratureDTO> getAllLiterature() {
        return literatureRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LiteratureDTO getLiteratureById(Long id) {
        Literature lit = literatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical literature record with id " + id + " not found"));
        return toDTO(lit);
    }

    @Override
    public List<LiteratureDTO> searchLiterature(LiteratureSearchRequest request) {
        String query = request.getQuery() != null ? request.getQuery().trim().toLowerCase(Locale.ROOT) : "";
        String topic = request.getTopic() != null ? request.getTopic().trim().toLowerCase(Locale.ROOT) : "";
        int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit() : 10;

        List<Literature> all = literatureRepository.findAll();

        return all.stream()
                .filter(lit -> {
                    if (!topic.isEmpty() && !lit.getTopic().toLowerCase(Locale.ROOT).contains(topic)) {
                        return false;
                    }
                    if (query.isEmpty()) {
                        return true;
                    }
                    return lit.getTitle().toLowerCase(Locale.ROOT).contains(query)
                            || lit.getAbstractText().toLowerCase(Locale.ROOT).contains(query)
                            || lit.getTopic().toLowerCase(Locale.ROOT).contains(query);
                })
                .map(lit -> {
                    int score = computeRelevance(lit, query, Collections.emptyList());
                    return toDTO(lit, score > 0 ? score : 75);
                })
                .sorted(Comparator.comparingInt(LiteratureDTO::getRelevanceScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<LiteratureDTO> findRelevantForCase(String query, List<String> features, int limit) {
        List<Literature> all = literatureRepository.findAll();
        if (all.isEmpty()) {
            return Collections.emptyList();
        }

        List<LiteratureDTO> scoredList = new ArrayList<>();
        for (Literature lit : all) {
            int score = computeRelevance(lit, query, features);
            if (score > 35) { // Minimum relevance threshold
                scoredList.add(toDTO(lit, score));
            }
        }

        // If no records passed threshold, return top 3 highest or fallback to first 3 with baseline score
        if (scoredList.isEmpty()) {
            return all.stream()
                    .limit(limit)
                    .map(lit -> toDTO(lit, 65))
                    .collect(Collectors.toList());
        }

        scoredList.sort(Comparator.comparingInt(LiteratureDTO::getRelevanceScore).reversed());
        return scoredList.stream().limit(limit).collect(Collectors.toList());
    }

    private int computeRelevance(Literature lit, String query, List<String> features) {
        String title = lit.getTitle().toLowerCase(Locale.ROOT);
        String abstractText = lit.getAbstractText().toLowerCase(Locale.ROOT);
        String topic = lit.getTopic().toLowerCase(Locale.ROOT);

        int score = 50; // baseline

        // Feature matching (high priority)
        if (features != null) {
            for (String feature : features) {
                String f = feature.toLowerCase(Locale.ROOT);
                if (title.contains(f)) score += 24;
                else if (abstractText.contains(f)) score += 14;
                if (topic.contains(f)) score += 12;

                // Also match significant keywords of the feature (e.g. "troponin", "hypertension")
                for (String word : f.split("[\\s,;]+")) {
                    if (word.length() > 3) {
                        if (title.contains(word)) score += 18;
                        else if (abstractText.contains(word)) score += 10;
                    }
                }
            }
        }

        // Query keywords matching
        if (query != null && !query.isBlank()) {
            String[] tokens = query.toLowerCase(Locale.ROOT).split("[\\s,;]+");
            for (String token : tokens) {
                if (token.length() > 3) {
                    if (title.contains(token)) score += 10;
                    else if (abstractText.contains(token)) score += 6;
                }
            }
        }

        // Cap score logically between 60 and 96 for realism
        if (score > 96) score = 96;
        return score;
    }

    @Override
    @Transactional
    public void seedLiteratureIfEmpty() {
        if (literatureRepository.count() == 0) {
            log.info("Literature table empty. Seeding medical literature dataset...");
            try {
                ClassPathResource resource = new ClassPathResource("data/medical_literature.json");
                try (InputStream is = resource.getInputStream()) {
                    List<Literature> list = objectMapper.readValue(is, new TypeReference<List<Literature>>() {});
                    literatureRepository.saveAll(list);
                    log.info("Successfully seeded {} medical literature records into MySQL.", list.size());
                }
            } catch (Exception e) {
                log.error("Failed to seed medical literature dataset from JSON: {}", e.getMessage(), e);
            }
        } else {
            log.info("Literature table already contains {} records.", literatureRepository.count());
        }
    }

    private LiteratureDTO toDTO(Literature lit) {
        return toDTO(lit, 80);
    }

    private LiteratureDTO toDTO(Literature lit, int relevanceScore) {
        return new LiteratureDTO(
                lit.getId(),
                lit.getTitle(),
                lit.getAuthors(),
                lit.getPublicationYear(),
                lit.getJournal(),
                lit.getAbstractText(),
                lit.getTopic(),
                lit.getSourceType(),
                relevanceScore
        );
    }
}
