package com.medlitai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medlitai.dto.LiteratureDTO;
import com.medlitai.dto.LiteratureSearchRequest;
import com.medlitai.model.Literature;
import com.medlitai.repository.LiteratureRepository;
import com.medlitai.service.impl.LiteratureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiteratureServiceTest {

    @Mock
    private LiteratureRepository literatureRepository;

    private LiteratureService literatureService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        literatureService = new LiteratureServiceImpl(literatureRepository, objectMapper);
    }

    @Test
    void testGetAllLiterature() {
        Literature lit1 = new Literature("Troponin in ACS", "Smith et al.", 2023, "JACC", "Troponin abstract", "Cardiovascular Disease", "Demo Literature Record");
        lit1.setId(1L);
        when(literatureRepository.findAll()).thenReturn(List.of(lit1));

        List<LiteratureDTO> results = literatureService.getAllLiterature();
        assertEquals(1, results.size());
        assertEquals("Troponin in ACS", results.get(0).getTitle());
    }

    @Test
    void testGetLiteratureById() {
        Literature lit = new Literature("Troponin in ACS", "Smith et al.", 2023, "JACC", "Troponin abstract", "Cardiovascular Disease", "Demo Literature Record");
        lit.setId(1L);
        when(literatureRepository.findById(1L)).thenReturn(Optional.of(lit));

        LiteratureDTO dto = literatureService.getLiteratureById(1L);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Troponin in ACS", dto.getTitle());
    }

    @Test
    void testSearchLiteratureByQueryAndTopic() {
        Literature lit1 = new Literature("Troponin in ACS", "Smith et al.", 2023, "JACC", "Troponin abstract", "Cardiovascular Disease", "Demo Literature Record");
        lit1.setId(1L);
        Literature lit2 = new Literature("Standards of Care in Diabetes", "ADA", 2024, "Diabetes Care", "SGLT2 inhibitors", "Diabetes", "Demo Literature Record");
        lit2.setId(2L);
        when(literatureRepository.findAll()).thenReturn(List.of(lit1, lit2));

        LiteratureSearchRequest req = new LiteratureSearchRequest("troponin", "Cardiovascular Disease", 10);
        List<LiteratureDTO> results = literatureService.searchLiterature(req);

        assertEquals(1, results.size());
        assertEquals("Troponin in ACS", results.get(0).getTitle());
    }

    @Test
    void testFindRelevantForCase() {
        Literature lit1 = new Literature("Troponin in ACS", "Smith et al.", 2023, "JACC", "High-sensitivity cardiac troponin", "Cardiovascular Disease", "Demo Literature Record");
        lit1.setId(1L);
        when(literatureRepository.findAll()).thenReturn(List.of(lit1));

        List<LiteratureDTO> results = literatureService.findRelevantForCase("chest pain", List.of("Chest Pain", "Elevated Troponin"), 5);
        assertFalse(results.isEmpty());
        assertTrue(results.get(0).getRelevanceScore() > 60);
    }
}
